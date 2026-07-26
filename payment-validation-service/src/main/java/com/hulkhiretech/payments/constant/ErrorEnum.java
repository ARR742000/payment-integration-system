package com.hulkhiretech.payments.constant;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    GENERIC_ERROR("10000", "Unable to process your request, please try later"),
    MISSING_CUSTOMER_ID("10001", "Customer ID is missing in the payment request"),
    MISSING_HMAC_SIGNATURE("10002", "HMAC signature is missing in the payment request"), 
    INVALID_HMAC_SIGNATURE("10003", "HMAC signature is invalid. Please check & try again"), 
    MERCHANT_TXN_REF_EMPTY("10004", "Merchant transaction reference is null or empty."), 
    DUPLICATE_MERCHANT_TXN_REF("10005", "Duplicate entry for merchant payment request"), 
    PAYMENT_NOT_SAVED("10006", "Unable to save payment in DB, please try again later"), 
    PAYMENT_ATTEMPT_THRESHOLD_EXCEEDED("10007", "Payment attempts exceeded threshold, please after some time");

    private final String errorCode;
    private final String errorMessage;

    ErrorEnum(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}

