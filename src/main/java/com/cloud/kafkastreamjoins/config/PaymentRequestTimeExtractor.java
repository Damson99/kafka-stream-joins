package com.cloud.kafkastreamjoins.config;

import com.cloud.kafkastreamjoins.model.input.PaymentRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PaymentRequestTimeExtractor implements TimestampExtractor {


    @Override
    public long extract(ConsumerRecord<Object, Object> consumerRecord, long prevTime) {
        PaymentRequest payReq= (PaymentRequest) consumerRecord.value();
        return ((payReq.getCreatedTime()>0) ? payReq.getCreatedTime() : prevTime);
    }

    @Bean
    public TimestampExtractor requestTimeExtractor() {
        return new PaymentConfirmationTimeExtractor();
    }
}
