package com.hulkhiretech.payments.constant;

import lombok.Getter;

@Getter
public enum PaymentTypeEnum {
    SALE(1, "SALE");

    private final int id;
    private final String name;

    PaymentTypeEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static PaymentTypeEnum fromId(int id) {
        for (PaymentTypeEnum e : values()) {
            if (e.id == id) return e;
        }
        throw new IllegalArgumentException("Invalid id: " + id);
    }

    public static PaymentTypeEnum fromName(String name) {
        for (PaymentTypeEnum e : values()) {
            if (e.name.equalsIgnoreCase(name)) return e;
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }
}
