package com.cloud.kafkastreamjoins.service;

import com.cloud.kafkastreamjoins.model.output.TransactionStatusEnum;
import com.cloud.kafkastreamjoins.model.input.PaymentConfirmation;
import com.cloud.kafkastreamjoins.model.input.PaymentRequest;
import com.cloud.kafkastreamjoins.model.output.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RecordBuilderService {

    public TransactionStatus getTransactionStatus(PaymentRequest paymentRequest,
                                                  PaymentConfirmation paymentConfirmation) {

        TransactionStatusEnum status= TransactionStatusEnum.FAILURE;
        if(paymentRequest.getOTP().equals(paymentConfirmation.getOTP()))
            status= TransactionStatusEnum.SUCCESS;

        TransactionStatus transactionStatus= new TransactionStatus();
        transactionStatus.setTransactionID(paymentRequest.getTransactionID());
        transactionStatus.setStatus(status.name());
        return transactionStatus;
    }
}
