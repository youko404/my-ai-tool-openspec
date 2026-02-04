package com.ai.content.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum ModelProvider {
    OPENROUTER("OPENROUTER", "OpenRouter"), OPENAI("OPENAI", "OpenAI");

    private final String value;
    private final String description;

    ModelProvider(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static ModelProvider fromValue(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Model provider cannot be empty");
        }
        return Arrays.stream(values()).filter(
                provider -> provider.value.equalsIgnoreCase(normalized) || provider.name().equalsIgnoreCase(normalized))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Unsupported model provider: " + value));
    }
}
