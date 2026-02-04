package com.ai.content.domain.enums;

public enum ChatMessageRole {
    USER("user"), ASSISTANT("assistant");

    private final String value;

    ChatMessageRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
