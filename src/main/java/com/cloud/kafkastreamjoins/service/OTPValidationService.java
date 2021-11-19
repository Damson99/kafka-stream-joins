package com.cloud.kafkastreamjoins.service;


import com.cloud.kafkastreamjoins.model.input.PaymentConfirmation;
import com.cloud.kafkastreamjoins.model.input.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

@Slf4j
@Service
@EnableBinding(OTPValidationService.class)
@RequiredArgsConstructor
public class OTPValidationService {
    private final RecordBuilderService recordBuilderService;

    @StreamListener
    public void process(@Input("payment-request-channel") KStream<String, PaymentRequest> payReqStream,
                        @Input("payment-confirmation-channel") KStream<String, PaymentConfirmation> payConStream) {

//        both have transactionID as a key
        payReqStream.foreach((key, value) -> log.info("request key: "+key+
                " created time: "+ Instant.ofEpochMilli(value.getCreatedTime()).atOffset(ZoneOffset.UTC)));

        payConStream.foreach((key, value) -> log.info("confirmation key: "+key+
                " created time: "+ Instant.ofEpochMilli(value.getCreatedTime()).atOffset(ZoneOffset.UTC)));

//        join takes mandatory three arguments
        payReqStream.join(
//                the first is the other stream that we want to join
                payConStream,
//                the second arg is a ValueJoiner lambda of two args
//                @param req is arg from the left side
//                @param conf is arg from the right side
//                can be replaced with: recordBuilderService::getTransactionStatus
                (req, conf) -> recordBuilderService.getTransactionStatus(req, conf),
//                the firth arg is setting time constraint
//                when the payment request and payment confirmation arrive within 5 minutes window
//                then ValueJoiner is triggered and the transaction status will be successful or failure
                JoinWindows.of(Duration.ofMinutes(5)),
                StreamJoined.with(Serdes.String(),
                        new JsonSerde<>(PaymentRequest.class),
                        new JsonSerde<>(PaymentConfirmation.class)))
        .foreach((key, value) -> log.info("transaction id: {}, status: {}", key, value.getStatus()));
    }
}
