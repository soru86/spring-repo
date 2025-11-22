package com.sagademo.inventory.application;

import com.sagademo.common.dto.OrderRequest;
import com.sagademo.inventory.domain.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryWorkflowService {

    private final InventoryRepository repository;

    public InventoryWorkflowService(InventoryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public boolean reserve(OrderRequest request) {
        return request.getItems().stream().allMatch(line -> repository.findBySku(line.getSku())
                .filter(item -> item.getAvailableQuantity() >= line.getQuantity())
                .map(item -> {
                    item.setAvailableQuantity(item.getAvailableQuantity() - line.getQuantity());
                    repository.save(item);
                    return true;
                })
                .orElse(false));
    }

    @Transactional
    public void release(OrderRequest request) {
        request.getItems().forEach(line -> repository.findBySku(line.getSku()).ifPresent(item -> {
            item.setAvailableQuantity(item.getAvailableQuantity() + line.getQuantity());
            repository.save(item);
        }));
    }
}

