package com.LoginService.login.service;

import com.LoginService.login.DTO.SignUpRequestDTO;
import com.LoginService.login.DTO.UserResponseDTO;
import com.LoginService.login.entity.User;
import com.LoginService.login.enums.UserProviderEnum;
import com.LoginService.login.exception.CustomException.UserAlreadyExistsException;
import com.LoginService.login.mapper.UserDTO;
import com.LoginService.login.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignUpService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private UserDTO userDTO;

    public UserResponseDTO signup(SignUpRequestDTO signUpRequestDTO){
       Boolean isUserExist =  isUserExists(signUpRequestDTO.getEmail());

       if(isUserExist){
           throw new UserAlreadyExistsException();
       }

     BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
     String encryptedPassword =  bCryptPasswordEncoder.encode(signUpRequestDTO.getPassword());
     User user = new User();
     user.setUsername(signUpRequestDTO.getUsername());
     user.setEmail(signUpRequestDTO.getEmail());
     user.setPassword(encryptedPassword);
     user.setProvider(UserProviderEnum.SELF);
     return userDTO.covertUserToUserDto(usersRepo.save(user)) ;
    }


    private Boolean isUserExists(String email){
        User user = usersRepo.findByEmail(email);
        return user != null;
    }

}
