package com.LoginService.login.entity.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetTokenResponseEntity {

    String access_token;

    Integer expires_in;

    String refresh_token;

    String scope;

    String token_type;

    String id_token;

}
