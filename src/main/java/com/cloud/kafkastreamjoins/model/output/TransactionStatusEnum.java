package com.cloud.kafkastreamjoins.model.output;

public enum TransactionStatusEnum {

    FAILURE("Failure"), SUCCESS("Success");
    private String value;

    TransactionStatusEnum(String value) {
        this.value= value;
    }
}
