package com.todo.spring.shared.utils;

import lombok.Data;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

import java.util.Collections;

@Data
@Configuration
public class RestService {

    @Bean("RemoteTodoClient")
    public RestTemplate RestService() {
         return new RestTemplateBuilder()
                .defaultHeader("x-api-key", "n0KRcjVvm63H2Y7yubJJH5xZGentrS0k6qMw4lvB")
                .rootUri("https://l23ezadku1.execute-api.us-east-2.amazonaws.com/staging")
                .build();
    }
}
