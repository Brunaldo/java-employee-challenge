package com.example.rqchallenge.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseBody<T> {

    private String status;

    private T data;

    private String message;

}
