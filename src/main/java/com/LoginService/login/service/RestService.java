package com.LoginService.login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RestService {


    @Autowired
    private WebClient webClient;

    public <T> T get(String url , Class<T> responseType ) {
            return  webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
    }

    public <T> T post(String url , Object responseBody, Class<T> responseType ) {
        return  webClient.post()
                .uri(url)
                .bodyValue(responseBody)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

}
