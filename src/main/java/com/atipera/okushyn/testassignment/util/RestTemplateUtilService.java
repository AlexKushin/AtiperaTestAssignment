package com.atipera.okushyn.testassignment.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestTemplateUtilService {

    private final RestTemplate restTemplate;


    @Autowired
    public RestTemplateUtilService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T getResource(Class<T> clazz, String url) {
        return restTemplate.getForEntity(url, clazz).getBody();
    }


    public <T> T[] getResourceArray(Class<T[]> clazz, String url) {
        return restTemplate.getForEntity(url, clazz).getBody();
    }
}
