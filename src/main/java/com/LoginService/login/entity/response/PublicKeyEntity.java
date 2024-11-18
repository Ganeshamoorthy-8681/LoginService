package com.LoginService.login.entity.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicKeyEntity {
    String kid;
    String alg;
    String n;
    String e;
    String kty;
}
