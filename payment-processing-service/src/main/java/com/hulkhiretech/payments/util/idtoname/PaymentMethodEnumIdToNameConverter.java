package com.hulkhiretech.payments.util.idtoname;

import org.modelmapper.AbstractConverter;

import com.hulkhiretech.payments.constant.PaymentMethodEnum;

public class PaymentMethodEnumIdToNameConverter extends AbstractConverter<Integer, String> {
    @Override
    protected String convert(Integer source) {
        return PaymentMethodEnum.fromId(source).getName();
    }
}