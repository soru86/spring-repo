package com.sagademo.payment.application;

import com.sagademo.common.dto.OrderRequest;
import com.sagademo.payment.domain.PaymentTransaction;
import com.sagademo.payment.domain.PaymentTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentWorkflowService {

    private final PaymentTransactionRepository repository;

    public PaymentWorkflowService(PaymentTransactionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public boolean charge(String orderId, OrderRequest request) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrderId(orderId);
        transaction.setAmount(request.getTotalAmount());
        transaction.setStatus("APPROVED");
        repository.save(transaction);
        return true;
    }

    @Transactional
    public void refund(String orderId, OrderRequest request) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrderId(orderId);
        transaction.setAmount(request.getTotalAmount().negate());
        transaction.setStatus("REFUNDED");
        repository.save(transaction);
    }
}

