package com.ecom.PaymentService.producer;


import com.ecom.PaymentService.event.PaymentCompletedEvent;
import com.ecom.PaymentService.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
//import org.springframework.kafka.support.mapping.JsonHeaders;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentStatusEventProducer {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void publishPaymentCompleted(PaymentCompletedEvent event)
    {
        log.info("---------------payment completed {}",event);
        kafkaTemplate.send("payment.completed",event);

    }


    public void publishPaymentFailed(PaymentFailedEvent event)
    {
        log.info("---------------payment failed {}",event);
        kafkaTemplate.send("payment.failed",event);

    }
}
