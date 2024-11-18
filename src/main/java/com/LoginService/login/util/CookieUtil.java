package com.LoginService.login.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Optional;

public class CookieUtil {

    public static Cookie getCookie(HttpServletRequest request, String cookieName){
        Cookie [] cookies = request.getCookies();
        if(cookies== null || cookies.length == 0) return null;
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst()
                .orElse(null);
    }

    public static Cookie createCookie(String cookieName, String cookieValue, Optional<Integer> maxAge) {
        var cookie =  new Cookie(cookieName, cookieValue);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        maxAge.ifPresent(cookie::setMaxAge);
        cookie.setAttribute("SameSite","Strict");
        return cookie;
    }
}
