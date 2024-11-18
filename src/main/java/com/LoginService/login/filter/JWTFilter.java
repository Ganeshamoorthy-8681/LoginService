package com.LoginService.login.filter;

import com.LoginService.login.service.JWTService;
import com.LoginService.login.util.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component

public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Override
    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain ) throws ServletException, IOException {
       Cookie cookie =  CookieUtil.getCookie(request, "JWT");
       if(cookie!= null) {
           try {
               if(jwtService.validateToken(cookie.getValue())) {
                   UsernamePasswordAuthenticationToken authToken = jwtService.getAuthentication();
                   SecurityContextHolder.getContext().setAuthentication(authToken);
               }
           }
           catch (ExpiredJwtException e){
               System.out.println("JWT Token Expired");
//               // Set the HTTP status to 401 (Unauthorized)
//               response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//
//               // Set the response content type
//               response.setContentType("application/json");
//
//               // Prepare Json object
//               JSONObject jsonResponse = new JSONObject();
//               jsonResponse.put("code", HttpServletResponse.SC_UNAUTHORIZED);
//               jsonResponse.put("message", "JWT Token Expired. Please refresh token");
//
//                // To string
//               String jsonString = jsonResponse.toString();
//              response.getWriter().write(jsonString);
           }
           catch ( MalformedJwtException e ) {
               System.out.println("Malformed JWT passed");
           }

           catch (Exception e) {
               System.out.println(e.getMessage());
           }

       }
        filterChain.doFilter(request,response);
    }
}
