package com.javatechie.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.web.reactive.client.ObservationWebClientCustomizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Autowired
    private ObservationWebClientCustomizer webClientCustomizer;

    @Bean
    @LoadBalanced
    WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @LoadBalanced
    @Bean(name = "identityServiceWebClient")
    WebClient identityServiceWebClient(WebClient.Builder webClientBuilder) {
        webClientCustomizer.customize(webClientBuilder);// makes context propagation work in WebClient
        return webClientBuilder.clone()
                               .baseUrl("http://identity-service")
                               // .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT)
                               .build();
    }

    @LoadBalanced
    @Bean(name = "restaurentServiceWebClient")
    WebClient restaurentServiceWebClient(WebClient.Builder webClientBuilder) {
        webClientCustomizer.customize(webClientBuilder);// makes context propagation work in WebClient
        return webClientBuilder.clone()
                               .baseUrl("http://restaurant-service")
                               .build();
    }
}
