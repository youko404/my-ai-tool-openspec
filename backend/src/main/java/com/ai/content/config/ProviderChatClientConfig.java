package com.ai.content.config;

import com.ai.content.domain.enums.ModelProvider;
import com.ai.content.service.provider.ProviderChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ProviderChatClientConfig {

    @Bean
    public Map<ModelProvider, ProviderChatClient> providerChatClients(List<ProviderChatClient> clients) {
        Map<ModelProvider, ProviderChatClient> map = new EnumMap<>(ModelProvider.class);
        for (ProviderChatClient client : clients) {
            ModelProvider provider = client.getProvider();
            if (map.put(provider, client) != null) {
                throw new IllegalStateException("Duplicate provider chat client: " + provider);
            }
        }
        return map;
    }
}
