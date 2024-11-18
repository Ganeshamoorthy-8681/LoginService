package com.LoginService.login.exception.CustomException;

public class UserAlreadyExistsException  extends  RuntimeException {

   public UserAlreadyExistsException(){
       super("Username already exists");
   }

}
