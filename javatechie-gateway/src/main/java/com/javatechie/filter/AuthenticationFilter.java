package com.javatechie.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

//import brave.Span;
//import brave.Tracer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    // @Autowired
    // private WebClient.Builder webClientBuilder;

    @Qualifier("identityServiceWebClient")
    @Autowired
    private WebClient webClient;

    // @Autowired
    // private Tracer tracer;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            log.info("Gateway filter called");
            if (validator.isSecured.test(exchange.getRequest())) {
                // header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                // validate token

                // Gateway is async (webflux) but LoadBalanced RestTemplate is sync so we can't use it in Gateway
                // template.getForObject("http://identity-service/auth/validate?token=" + authHeader, String.class);

                // Span span = tracer.currentSpan();
                // String traceId = span.context().traceIdString();
                // WebClient is async
                return webClient // webClientBuilder.build()
                                .get()
                                .uri("http://identity-service/auth/validate?token=" + authHeader)
                                .retrieve()
                                .onStatus(status -> !HttpStatus.OK.equals(status), ClientResponse::createError)
                                .bodyToMono(String.class)
                                .flatMap(response -> chain.filter(exchange));

            }
            return chain.filter(exchange);
        });
    }

    public static class Config {

    }
}
