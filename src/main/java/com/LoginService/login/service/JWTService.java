package com.LoginService.login.service;

import com.LoginService.login.entity.UserDetails;
import com.LoginService.login.exception.CustomException.UsernameNotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JWTService {


    @Value("${SECRET_KEY}")
    private String SECRET_KEY;

    @Autowired
    private  UserDetailsService userDetailsService;

    private UserDetails userDetails;

    public String generateJWTToken(String email){
        return Jwts.builder()
                .claims()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 15 * 60* 1000))
                .and()
                .signWith(getKey())
                .compact();
    }

    public String generateRefreshToken(String email){
        return Jwts.builder()
                .claims()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
                .and()
                .signWith(getKey())
                .compact();
    }

    public Boolean validateRefreshToken(String refreshToken){
        Claims claims = getClaims(refreshToken);
        String username = claims.getSubject();
        userDetails = (UserDetails) userDetailsService.loadUserByUsername(username);
        return userDetails.getRefreshToken().equals(refreshToken) && !isTokenExpired(refreshToken) && userDetails.getUsername().equals(username);
    }


    private SecretKey getKey(){
        byte [] secretKey =Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(secretKey);
    }

    public boolean validateToken(String token ){
        String email  = getTokenSubject(token);
        if(email == null ){
            throw new UsernameNotFoundException();
        }
       userDetails = (UserDetails) userDetailsService.loadUserByUsername(email);
       return userDetails.getUsername().equals(email) && !isTokenExpired(token);
    }

    public UsernamePasswordAuthenticationToken getAuthentication(){
       return new UsernamePasswordAuthenticationToken(userDetails.getUsername(),userDetails.getPassword(),userDetails.getAuthorities());
    }

    private  Boolean isTokenExpired(String token){
        Claims claims = getClaims(token);
        return claims.getExpiration().before(new Date());
    }

    public String getTokenSubject(String token){
     Claims claims = getClaims(token);
     return claims.getSubject();
    }

    private Claims getClaims(String token){
       return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
