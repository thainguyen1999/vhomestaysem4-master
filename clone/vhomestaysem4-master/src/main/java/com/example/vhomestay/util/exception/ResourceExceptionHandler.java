package com.example.vhomestay.util.exception;

import com.example.vhomestay.controller.manager.ManagerReportController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ResourceExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(ManagerReportController.class);
    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<Object> handleApiRequestNotFoundException(ResourceNotFoundException e, WebRequest request) {
        ResourceException resourceException = new ResourceException(
                e.getMessage(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now());
        logger.error(e.getMessage());
        return new ResponseEntity<>(resourceException, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = {ResourceForbiddenException.class})
    public ResponseEntity<Object> handleApiRequestForbiddenException(ResourceForbiddenException e, WebRequest request) {
        ResourceException resourceException = new ResourceException(
                e.getMessage(),
                HttpStatus.FORBIDDEN,
                LocalDateTime.now());
        logger.error(e.getMessage());
        return new ResponseEntity<>(resourceException, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {ResourceBadRequestException.class})
    public ResponseEntity<Object> handleApiRequestBadRequestException(ResourceBadRequestException e, WebRequest request) {
        ResourceException resourceException = new ResourceException(
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());
        logger.error(e.getMessage());
        return new ResponseEntity<>(resourceException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ResourceUnauthorizedException.class})
    public ResponseEntity<Object> handleApiRequestUnauthorizedException(ResourceUnauthorizedException e, WebRequest request) {
        ResourceException resourceException = new ResourceException(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED,
                LocalDateTime.now());
        logger.error(e.getMessage());
        return new ResponseEntity<>(resourceException, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {ResourceNoContentException.class})
    public ResponseEntity<Object> handleApiRequestNoContentException(ResourceNoContentException e, WebRequest request) {
        ResourceException resourceException = new ResourceException(
                e.getMessage(),
                HttpStatus.NO_CONTENT,
                LocalDateTime.now());
        logger.error(e.getMessage());
        return new ResponseEntity<>(resourceException, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(value = {ResourceInternalServerErrorException.class})
    public ResponseEntity<Object> handleApiRequestInternalServerErrorException(ResourceInternalServerErrorException e, WebRequest request) {
        ResourceException resourceException = new ResourceException(
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now());
        logger.error(e.getMessage());
        return new ResponseEntity<>(resourceException, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(value = {ResourceConflictException.class})
    public ResponseEntity<Object> handleApiRequestConflictException(ResourceConflictException e, WebRequest request) {
        ResourceException resourceException = new ResourceException(
                e.getMessage(),
                HttpStatus.CONFLICT,
                LocalDateTime.now());
        logger.error(e.getMessage());
        return new ResponseEntity<>(resourceException, HttpStatus.CONFLICT);
    }
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ResourceException resourceException = new ResourceException(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now());
        logger.error(ex.getMessage());
        return new ResponseEntity<>(resourceException, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ResourceException resourceException = new ResourceException(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now());
        logger.error(ex.getMessage());
        return new ResponseEntity<>(resourceException, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ResourceException resourceException = new ResourceException(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now());
        logger.error(ex.getMessage());
        return new ResponseEntity<>(resourceException, HttpStatus.NOT_FOUND);
    }
}
