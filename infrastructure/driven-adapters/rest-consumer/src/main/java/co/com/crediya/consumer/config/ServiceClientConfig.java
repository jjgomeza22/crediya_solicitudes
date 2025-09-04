package co.com.crediya.consumer.config;

import co.com.crediya.consumer.client.AuthenticationServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ServiceClientConfig {
    @Bean
    public AuthenticationServiceClient authenticationServiceClient(
            @Value("${adapters.consumer.authentication.url}") String baseUrl
    ) {
        return new AuthenticationServiceClient((createWebClient(baseUrl)));
    }

    private WebClient createWebClient(String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
