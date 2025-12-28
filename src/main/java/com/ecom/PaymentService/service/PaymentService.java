package com.ecom.PaymentService.service;

import com.ecom.PaymentService.entity.Payment;
import com.ecom.PaymentService.entity.PaymentStatus;
import com.ecom.PaymentService.event.PaymentCompletedEvent;
import com.ecom.PaymentService.event.PaymentFailedEvent;
import com.ecom.PaymentService.producer.PaymentStatusEventProducer;
import com.ecom.PaymentService.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentStatusEventProducer paymentStatusEventProducer;

    @Transactional
    public void createPayment(UUID orderId, double amount){
        Payment payment = Payment.builder()
                .orderId(orderId)
                .amount(amount)
                .status(PaymentStatus.INITIATED)
                .createdAt(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // simulate payment result
        boolean success = amount < 1000;

        if (success){
            savedPayment.setStatus(PaymentStatus.FAILED);
            PaymentFailedEvent failedEvent = new PaymentFailedEvent(orderId,"failed due to internal error");
            paymentStatusEventProducer.publishPaymentFailed(failedEvent);
        }
        else {
            savedPayment.setStatus(PaymentStatus.COMPLETED);
            PaymentCompletedEvent paymentCompletedEvent = new PaymentCompletedEvent(orderId,savedPayment.getId());//sending orderid as payment beacuse we are not created a payment for this order yet
            paymentStatusEventProducer.publishPaymentCompleted(paymentCompletedEvent);
        }
    }
}
