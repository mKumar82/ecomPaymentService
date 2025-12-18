package com.ecom.PaymentService.consumer;

import com.ecom.PaymentService.entity.Payment;
import com.ecom.PaymentService.entity.PaymentStatus;
import com.ecom.PaymentService.event.OrderCreatedEvent;
import com.ecom.PaymentService.event.PaymentCompletedEvent;
import com.ecom.PaymentService.event.PaymentFailedEvent;
import com.ecom.PaymentService.producer.PaymentEventProducer;
import com.ecom.PaymentService.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;


    @KafkaListener(topics = "order.created",groupId = "payment-service-group")
    public void handleOrderCreated(OrderCreatedEvent event){

        log.info("✅ Payment initiated for orderId={}, amount={}",event.orderId(),event.totalAmount());

        Payment payment = Payment.builder()
                .orderId(event.orderId())
                .amount(event.totalAmount())
                .status(PaymentStatus.INITIATED)
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        // Simulate payment result
        boolean success = event.totalAmount() < 1000;

        if (success) {
            payment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);

            paymentEventProducer.publishPaymentCompleted(
                    new PaymentCompletedEvent(event.orderId(), payment.getId())
            );
            log.info("✅ Payment completed",event.orderId(),event.totalAmount());

        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);

            paymentEventProducer.publishPaymentFailed(
                    new PaymentFailedEvent(event.orderId(), "LIMIT_EXCEEDED")
            );
            log.info("❌  PAYMENT_FAILED", event.orderId());
        }
    }
}
