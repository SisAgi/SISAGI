package com.agibank.sisagi.exception;

public record ErrorResponse(

        Object message,
        int status,
        String timeStamp,
        String path){}
