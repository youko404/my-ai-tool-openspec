package com.ai.content.domain.enums;

public enum ChatMessageStatus {
    SUCCESS("success"), ERROR("error");

    private final String value;

    ChatMessageStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
