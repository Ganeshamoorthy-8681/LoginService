package com.LoginService.login.service;

import com.LoginService.login.DTO.LoginRequestDTO;
import com.LoginService.login.entity.User;
import com.LoginService.login.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginService {


    @Autowired
    private JWTService jwtService;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    public Map<String,String> verify(LoginRequestDTO loginRequestDTO){
       Authentication authentication = authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword()));

       Map<String,String> tokens = new HashMap<>();

       if(authentication.isAuthenticated()){
           var refreshToken  = jwtService.generateRefreshToken(authentication.getName());
           tokens.put("JWT",jwtService.generateJWTToken(authentication.getName()));
           tokens.put("refreshToken",refreshToken);
           User user =  usersRepo.findByEmail(loginRequestDTO.getEmail());
           user.setRefreshToken(refreshToken);
           usersRepo.save(user);
       }

        return tokens;
    }

    public String validateRefreshToken(String refreshToken) {
      var isTokenValid = jwtService.validateRefreshToken(refreshToken);
      if(isTokenValid){
          String email = jwtService.getTokenSubject(refreshToken);
          return jwtService.generateJWTToken(email);
      }
      return null;
    }

    public ResponseEntity<String>logOut(String refreshToken){
        User user = usersRepo.findByRefreshToken(refreshToken);

        if(user!=null){
            user.setRefreshToken(null);
            usersRepo.save(user);
            return new ResponseEntity<>("DELETED", HttpStatus.OK);
        }
        return new ResponseEntity<>("NOT_FOUND",HttpStatus.NOT_FOUND);
    }

}
