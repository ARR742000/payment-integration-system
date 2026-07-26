package com.hulkhiretech.payments.constant;

import lombok.Getter;

@Getter
public enum ProviderEnum {
    TRUSTLY(1, "TRUSTLY");

    private final int id;
    private final String name;

    ProviderEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static ProviderEnum fromId(int id) {
        for (ProviderEnum e : values()) {
            if (e.id == id) return e;
        }
        throw new IllegalArgumentException("Invalid id: " + id);
    }

    public static ProviderEnum fromName(String name) {
        for (ProviderEnum e : values()) {
            if (e.name.equalsIgnoreCase(name)) return e;
        }
        throw new IllegalArgumentException("Invalid name: " + name);
    }
}
