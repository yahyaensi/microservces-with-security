package com.javatechie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Hooks;

@SpringBootApplication
// optional if we have the spring-cloud-starter-netflix-eureka-client dependency on the classpath.
@EnableDiscoveryClient // @EnableDiscoveryClient or @EnableEurekaClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @PostConstruct
    public void init() {
        Hooks.enableAutomaticContextPropagation();
    }

}
