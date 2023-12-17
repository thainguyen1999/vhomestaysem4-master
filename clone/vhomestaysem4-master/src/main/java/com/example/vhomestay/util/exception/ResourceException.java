package com.example.vhomestay.util.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ResourceException {
    private final String message;
    private final HttpStatus httpStatus;
    private final LocalDateTime timestamp;

}
