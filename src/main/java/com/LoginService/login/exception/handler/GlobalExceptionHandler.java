package com.LoginService.login.exception.handler;

import com.LoginService.login.exception.CustomException.RefreshTokenNotFoundException;
import com.LoginService.login.exception.CustomException.UserAlreadyExistsException;
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
        return new ResponseEntity<>(GlobalExceptionHandler.getErrorInstance(e.getMessage(),HttpServletResponse.SC_METHOD_NOT_ALLOWED),
                HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public  ResponseEntity<Error> handleJwtTokenExpired(ExpiredJwtException e){
        return new ResponseEntity<>(GlobalExceptionHandler.getErrorInstance(e.getMessage(),HttpServletResponse.SC_UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public  ResponseEntity<Error> handleRefreshTokenNotFoundException(RefreshTokenNotFoundException e) {
        return new ResponseEntity<>(GlobalExceptionHandler.getErrorInstance(e.getMessage() , HttpServletResponse.SC_NOT_FOUND),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public  ResponseEntity<Error>handleUserAlreadyExistException(UserAlreadyExistsException e){
        return new ResponseEntity<>(GlobalExceptionHandler.getErrorInstance(e.getMessage(),HttpServletResponse.SC_CONFLICT),HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleException(Exception e){
       return new ResponseEntity<> (GlobalExceptionHandler.getErrorInstance(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
               HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private static Error getErrorInstance(String errorMessage, int statusCode){
        return  new Error(errorMessage,statusCode);
    }
}
