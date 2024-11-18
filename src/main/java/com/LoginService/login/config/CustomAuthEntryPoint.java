package com.LoginService.login.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        // Set the HTTP status to 401 (Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Set the response content type
        response.setContentType("application/json");

        // Prepare json object
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("code", HttpServletResponse.SC_UNAUTHORIZED);
        jsonResponse.put("message", "Authentication is required to access this resource.");

        // To string
        String jsonString = jsonResponse.toString();

        response.getWriter().write(jsonString);

    }
}
