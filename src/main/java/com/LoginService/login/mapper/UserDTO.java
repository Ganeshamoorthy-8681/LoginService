package com.LoginService.login.mapper;


import com.LoginService.login.DTO.UserResponseDTO;
import com.LoginService.login.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserDTO {

    public UserResponseDTO covertUserToUserDto(User user){

        if(user == null ) return  null ;

        UserResponseDTO userResponseDTO = new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProvider(),
                user.getCreatedOn(),
                user.getUpdatedOn()
        );
        return userResponseDTO ;
    }
}
