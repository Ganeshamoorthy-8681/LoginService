package com.LoginService.login.controller;

import com.LoginService.login.DTO.ForgotPasswordRequestDTO;
import com.LoginService.login.DTO.LoginRequestDTO;
import com.LoginService.login.DTO.SignUpRequestDTO;
import com.LoginService.login.DTO.UserResponseDTO;
import com.LoginService.login.exception.CustomException.RefreshTokenNotFoundException;
import com.LoginService.login.exception.CustomException.UsernameNotFoundException;
import com.LoginService.login.service.GoogleLoginService;
import com.LoginService.login.service.LoginService;
import com.LoginService.login.service.SignUpService;

import com.LoginService.login.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {


    @Autowired
    private LoginService loginService;

    @Autowired
    private SignUpService signUpService;

    @Autowired
    private GoogleLoginService googleLoginService;

    @PostMapping("login")
    public ResponseEntity<String> login (@RequestBody LoginRequestDTO loginRequestDTO , HttpServletResponse response){
        Map<String, String> authTokens =  loginService.verify(loginRequestDTO);

        authTokens.forEach((key, value) -> {
           Cookie cookie = CookieUtil.createCookie(key, value, Optional.empty());
            response.addCookie(cookie);
        });

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }


    @PostMapping("signup")
    public ResponseEntity<UserResponseDTO> signup(@RequestBody SignUpRequestDTO userCredentialsDTO){
        UserResponseDTO user = signUpService.signup(userCredentialsDTO);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }


    @GetMapping("refresh")
    public ResponseEntity<String> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) throws RefreshTokenNotFoundException {
        if(refreshToken!=null) {
            var jwtToken =  loginService.validateRefreshToken(refreshToken);
            if( jwtToken!= null && !jwtToken.isEmpty()){
                Cookie cookie = CookieUtil.createCookie("JWT", jwtToken, Optional.empty());
                response.addCookie(cookie);
                return new ResponseEntity<>("Success", HttpStatus.OK);
            }
        }
        throw new RefreshTokenNotFoundException();
    }


    @DeleteMapping("logout")
    public ResponseEntity<String> logOut(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response){

        if(refreshToken == null ){
            return new ResponseEntity<>("NOT_FOUND", HttpStatus.NOT_FOUND);
        }

        Cookie jwtCookie = CookieUtil.createCookie("JWT", null, Optional.of(0));
        Cookie refreshTokenCookie = CookieUtil.createCookie("refreshToken", null, Optional.of(0));
        response.addCookie(jwtCookie);
        response.addCookie(refreshTokenCookie);
        return loginService.logOut(refreshToken);
    }


    @GetMapping("data")
    public String mockData(){
        return "DATA CAN BE ACCESSED";
    }

    @GetMapping("login-with-google")
    public ResponseEntity<String> loginWithGoogle(HttpServletResponse response) throws IOException {

        String authorizationUrl = googleLoginService.generateAuthorizationUrl();

        System.out.printf("Auth Url Send to Redirect %s", authorizationUrl);

        // Redirect the user to Google’s login page
        response.sendRedirect(authorizationUrl);
        return new ResponseEntity<>("FOUND",HttpStatus.FOUND);
    }

    @PatchMapping("forgot-password")
    public  ResponseEntity<String> handleForgotPassword(@RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDTO) throws UsernameNotFoundException {
            return  loginService.forgotPassword(forgotPasswordRequestDTO);
    }

    @GetMapping("/google/callback")
    public ResponseEntity<String> authCode(@RequestParam("code") String code, HttpServletResponse response) {

       var tokens =  googleLoginService.handleGoogleLoginCallback(code);
        tokens.forEach((key, value) -> {
            Cookie cookie = CookieUtil.createCookie(key, value, Optional.empty());
            response.addCookie(cookie);
        });
        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }
}
