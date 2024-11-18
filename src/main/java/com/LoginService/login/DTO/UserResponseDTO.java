package com.LoginService.login.DTO;

import com.LoginService.login.enums.UserProviderEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDTO {

    private int id;

    private String username;

    private String email;

    private UserProviderEnum provider;

    private long createdOn;

    private long updatedOn;

}
