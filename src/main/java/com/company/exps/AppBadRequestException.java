package com.company.exps;

public class AppBadRequestException extends RuntimeException{
    public AppBadRequestException(String message) {
        super(message);
    }
}