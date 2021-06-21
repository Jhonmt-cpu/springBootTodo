package com.todo.spring.shared.utils;

import lombok.Data;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

import java.util.Collections;

@Data
@Service
public class RestService {
    private final RestTemplate restTemplate;
    private HttpHeaders defaultHeaders;
    private final String baseUrl = "https://l23ezadku1.execute-api.us-east-2.amazonaws.com/staging";

    public RestService() {
        this.restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("x-api-key", "n0KRcjVvm63H2Y7yubJJH5xZGentrS0k6qMw4lvB");
        this.defaultHeaders = headers;
    }
}
