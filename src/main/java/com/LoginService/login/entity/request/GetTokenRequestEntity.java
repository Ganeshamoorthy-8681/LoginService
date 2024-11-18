package com.LoginService.login.entity.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class GetTokenRequestEntity {

    public GetTokenRequestEntity( String code, String client_id, String client_secret ){
        this.code = code;
        this.client_id = client_id;
        this.client_secret =client_secret;
    }

    private String code;

    private String client_id;

    private String client_secret;

    private String redirect_uri;

    private String grant_type;

}
