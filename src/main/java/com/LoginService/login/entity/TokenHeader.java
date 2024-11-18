package com.LoginService.login.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenHeader {

    String alg;
    String kid;

}
