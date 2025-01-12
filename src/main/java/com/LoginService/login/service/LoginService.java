package com.LoginService.login.service;

import com.LoginService.login.DTO.ForgotPasswordRequestDTO;
import com.LoginService.login.DTO.LoginRequestDTO;
import com.LoginService.login.DTO.UserResponseDTO;
import com.LoginService.login.entity.User;
import com.LoginService.login.exception.CustomException.UsernameNotFoundException;
import com.LoginService.login.mapper.UserDTO;
import com.LoginService.login.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private UserDTO userDTO;

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


    public ResponseEntity<String> forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO){
        User user = usersRepo.findByEmail(forgotPasswordRequestDTO.getEmail());
        if(user == null){
            throw new UsernameNotFoundException();
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
        String encryptedPassword =  bCryptPasswordEncoder.encode(forgotPasswordRequestDTO.getPassword());
        user.setPassword(encryptedPassword);
        usersRepo.save(user);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    public ResponseEntity<UserResponseDTO> getUser(String jwtToken){
       String email = jwtService.getTokenSubject(jwtToken);
        var user  =this.usersRepo.findByEmail(email);
        if(user!=null){
            var userResponseDto = this.userDTO.covertUserToUserDto(user);
            return new ResponseEntity<>(userResponseDto,HttpStatus.OK);
        }else {
            throw new UsernameNotFoundException();
        }
    }

}
