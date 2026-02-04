package com.ai.content.domain.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModelProviderTest {

    @Test
    void fromValueAcceptsEnumName() {
        assertThat(ModelProvider.fromValue("OPENROUTER")).isEqualTo(ModelProvider.OPENROUTER);
        assertThat(ModelProvider.fromValue("OPENAI")).isEqualTo(ModelProvider.OPENAI);
    }

    @Test
    void fromValueAcceptsLowercaseValue() {
        assertThat(ModelProvider.fromValue("openrouter")).isEqualTo(ModelProvider.OPENROUTER);
        assertThat(ModelProvider.fromValue("openai")).isEqualTo(ModelProvider.OPENAI);
    }

    @Test
    void fromValueRejectsUnknown() {
        assertThatThrownBy(() -> ModelProvider.fromValue("unknown")).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unsupported model provider: unknown");
    }
}
