package com.example.rqchallenge.service.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ApiClientService {

    private final RestTemplate restTemplate;

    public <T, R> ResponseEntity<R> exchange(String endpoint, HttpMethod method, T requestBody, ParameterizedTypeReference<R> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<T> requestEntity = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(endpoint, method, requestEntity, responseType);
    }

}
