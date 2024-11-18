package com.LoginService.login.exception.CustomException;

public class UsernameNotFoundException extends  RuntimeException{

    public  UsernameNotFoundException(){
        super("Username Not Found.");
    }

}
