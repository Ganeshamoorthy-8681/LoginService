package com.LoginService.login.exception.CustomException;


public class RefreshTokenNotFoundException extends RuntimeException{

    public RefreshTokenNotFoundException(){
        super("Refresh Token Not Found, Please login again");
    }
}
