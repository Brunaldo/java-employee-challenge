package com.example.rqchallenge.service.rest;

import com.example.rqchallenge.controller.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

@Slf4j
public class RestResponseHandler {

    public static <T> T handleResponse(ResponseEntity<ResponseBody<T>> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().getData();
        } else {
            log.error("Error calling external API {}", response.getBody().getMessage());
            throw new HttpServerErrorException(response.getStatusCode());
        }
    }
}