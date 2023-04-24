package com.example.demo.mizuho.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class CustomExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = ArrayIndexOutOfBoundsException.class)
    protected ResponseEntity handle(){
        return ResponseEntity.badRequest().body("Wrong index. Please try again");
    }
}
