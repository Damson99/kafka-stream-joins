package com.cloud.kafkastreamjoins.config;

import com.cloud.kafkastreamjoins.model.input.PaymentConfirmation;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@Profile("payment-otp")
public class PaymentConfirmationTimeExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> consumerRecord, long prevTime) {

        PaymentConfirmation payConf= (PaymentConfirmation) consumerRecord.value();
        return ((payConf.getCreatedTime()>0) ? payConf.getCreatedTime() : prevTime);
    }

    @Bean
    public TimestampExtractor confirmationTimeExtractor() {
        return new PaymentConfirmationTimeExtractor();
    }
}
