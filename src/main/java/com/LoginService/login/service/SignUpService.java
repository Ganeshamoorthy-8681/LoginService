package com.LoginService.login.service;

import com.LoginService.login.DTO.SignUpRequestDTO;
import com.LoginService.login.DTO.UserResponseDTO;
import com.LoginService.login.entity.ProducerDataModel;
import com.LoginService.login.entity.User;
import com.LoginService.login.enums.UserProviderEnum;
import com.LoginService.login.exception.CustomException.UserAlreadyExistsException;
import com.LoginService.login.mapper.UserDTO;
import com.LoginService.login.producer.AccountCreationProducer;
import com.LoginService.login.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class SignUpService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private UserDTO userDTO;

    @Autowired
    private AccountCreationProducer accountCreationProducer;

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
     var userData  = usersRepo.save(user);
//     ProducerDataModel producerData = new ProducerDataModel(userData.getId(),userData.getUsername(),userData.getEmail());
        HashMap map = new HashMap<String,String>();
        map.put("userId",userData.getId());
        map.put("username",userData.getUsername());
        map.put("email",userData.getEmail());
     accountCreationProducer.sendMessage(map,"account_create");
     return userDTO.covertUserToUserDto(userData);
    }


    private Boolean isUserExists(String email){
        User user = usersRepo.findByEmail(email);
        return user != null;
    }

}
