package com.cloud.kafkastreamjoins.binder;

import com.cloud.kafkastreamjoins.model.input.PaymentConfirmation;
import com.cloud.kafkastreamjoins.model.input.PaymentRequest;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;

public interface OTPListenerBinder {

    @Input("payment-confirmation-channel")
    KStream<String, PaymentConfirmation> paymentConfirmationInputStream();

    @Input("payment-request-channel")
    KStream<String, PaymentRequest> paymentRequestInputStream();
}
