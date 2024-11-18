package com.LoginService.login.exception.handler;

import com.LoginService.login.exception.entity.Error;
import io.jsonwebtoken.ExpiredJwtException;
import io.netty.handler.codec.http.HttpStatusClass;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Error> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        Error error = new Error(e.getMessage(), HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public  ResponseEntity<Error> handleJwtTokenExpired(ExpiredJwtException e){
        Error error = new Error(e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleException(Exception e){
       String errorMessage = e.getMessage();
       Error error = new Error(errorMessage, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
       return new ResponseEntity<> (error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
