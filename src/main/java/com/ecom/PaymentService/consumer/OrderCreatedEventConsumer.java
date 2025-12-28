package com.ecom.PaymentService.consumer;

import com.ecom.PaymentService.event.PaymentCompletedEvent;
import com.ecom.PaymentService.event.PaymentFailedEvent;
import com.ecom.PaymentService.producer.PaymentStatusEventProducer;
import com.ecom.PaymentService.repository.PaymentRepository;

import com.ecom.PaymentService.service.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedEventConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "order.created", groupId = "payment-service-group")
    public void handleOrderCreated(JsonNode event) {

        UUID orderId = UUID.fromString(event.get("orderId").asText());
        double amount = event.get("totalAmount").asDouble();

        log.info("OrderId={}, amount={}", orderId, amount);

        paymentService.createPayment(orderId,amount);



    }


}
