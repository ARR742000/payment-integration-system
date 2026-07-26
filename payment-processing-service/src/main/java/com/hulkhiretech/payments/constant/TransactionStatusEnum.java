package com.hulkhiretech.payments.constant;

import lombok.Getter;

@Getter
public enum TransactionStatusEnum {
    CREATED(1, "CREATED"),
    INITIATED(2, "INITIATED"),
    PENDING(3, "PENDING"),
    SUCCESS(4, "SUCCESS"),
    FAILED(5, "FAILED");

    private final int id;
    private final String name;

    TransactionStatusEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static TransactionStatusEnum fromId(int id) {
        for (TransactionStatusEnum e : values()) {
            if (e.id == id) return e;
        }
        throw new IllegalArgumentException("Invalid id: " + id);
    }

    public static TransactionStatusEnum fromName(String name) {
        for (TransactionStatusEnum e : values()) {
            if (e.name.equalsIgnoreCase(name)) return e;
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }
}
