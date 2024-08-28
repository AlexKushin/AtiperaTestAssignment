package com.atipera.okushyn.testassignment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    @Value("${github.api.baseUrl}")
    private String baseUrl;


    @Bean
    public RestClient restClient() {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

}