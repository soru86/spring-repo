package com.sagademo.orchestrator.application;

import com.sagademo.common.dto.OrderRequest;
import com.sagademo.common.dto.OrderStatus;
import com.sagademo.common.messaging.SagaEvent;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SagaCoordinator {

    private static final Logger log = LoggerFactory.getLogger(SagaCoordinator.class);

    private final WebClient.Builder webClientBuilder;
    private final String bearerToken;

    public SagaCoordinator(WebClient.Builder webClientBuilder,
                           @Value("${saga.internal-token}") String bearerToken) {
        this.webClientBuilder = webClientBuilder;
        this.bearerToken = bearerToken;
    }

    @KafkaListener(topics = "order-events", groupId = "saga-orchestrator")
    public void listen(SagaEvent event) {
        log.info("Saga event received {} status {}", event.getOrderId(), event.getStatus());
        if (event.getStatus() == OrderStatus.PENDING && event.getPayload() != null) {
            runSaga(event.getOrderId(), event.getPayload());
        }
    }

    @CircuitBreaker(name = "inventory", fallbackMethod = "handleFailure")
    public void runSaga(String orderId, OrderRequest request) {
        WebClient client = webClientBuilder.build();
        callInventory(client, request)
                .flatMap(reserved -> {
                    if (!reserved) {
                        return rejectOrder(client, orderId, "Inventory insufficient");
                    }
                    return callPayment(client, orderId, request)
                            .flatMap(paid -> paid
                                    ? completeOrder(client, orderId)
                                    : compensatingActions(client, orderId, request));
                })
                .onErrorResume(ex -> {
                    log.error("Saga failed {}", ex.getMessage());
                    return compensatingActions(client, orderId, request);
                })
                .block();
    }

    private Mono<Boolean> callInventory(WebClient client, OrderRequest request) {
        return client.post().uri("http://inventory-service/inventory/reserve")
                .headers(headers -> headers.setBearerAuth(bearerToken))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ApiSuccess.class)
                .map(ApiSuccess::success);
    }

    private Mono<Boolean> callPayment(WebClient client, String orderId, OrderRequest request) {
        return client.post().uri("http://payment-service/payments/{orderId}/charge", orderId)
                .headers(headers -> headers.setBearerAuth(bearerToken))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ApiSuccess.class)
                .map(ApiSuccess::success);
    }

    private Mono<Boolean> completeOrder(WebClient client, String orderId) {
        return client.patch().uri("http://order-service/orders/{orderId}/status?status=COMPLETED", orderId)
                .headers(headers -> headers.setBearerAuth(bearerToken))
                .retrieve()
                .bodyToMono(Void.class)
                .thenReturn(true);
    }

    private Mono<Boolean> rejectOrder(WebClient client, String orderId, String message) {
        return client.patch().uri("http://order-service/orders/{orderId}/status?status=REJECTED&message={message}", orderId, message)
                .headers(headers -> headers.setBearerAuth(bearerToken))
                .retrieve()
                .bodyToMono(Void.class)
                .thenReturn(false);
    }

    private Mono<Boolean> compensatingActions(WebClient client, String orderId, OrderRequest request) {
        return client.post().uri("http://inventory-service/inventory/release")
                .headers(headers -> headers.setBearerAuth(bearerToken))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ApiSuccess.class)
                .then(client.post().uri("http://payment-service/payments/{orderId}/refund", orderId)
                        .headers(headers -> headers.setBearerAuth(bearerToken))
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(ApiSuccess.class))
                .then(rejectOrder(client, orderId, "Compensated rollback"))
                .thenReturn(false);
    }

    @SuppressWarnings("unused")
    private void handleFailure(Exception ex) {
        log.error("Circuit breaker invoked {}", ex.getMessage());
    }

    private record ApiSuccess(boolean success) {
    }
}

