package com.LoginService.login.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequestDTO {

    @NotEmpty(message = "Email cannot be blank")
    @NotNull( message = "Email cannot be null")
    @NotBlank( message = "Email cannot be blank")
    private String email;

    @NotEmpty(message = "password cannot be blank")
    @NotNull( message = "password cannot be null")
    @NotBlank( message = "password cannot be blank")
    private  String password;
}
