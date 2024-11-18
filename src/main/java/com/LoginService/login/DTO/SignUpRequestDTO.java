package com.LoginService.login.DTO;

import lombok.Data;

@Data
public class SignUpRequestDTO {
    private String username;
    private String email;
    private String password;
}
