package com.agibank.sisagi.exception;

public record ErrorResponse(String message,
                            int status,
                            String timeStamp,
                            String path) {
}
