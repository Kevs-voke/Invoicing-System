package com.gkev.InvoicingSystem.Utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;public final class CookieHeaderBuilderUtils {

    private CookieHeaderBuilderUtils() {}
    public static HttpHeaders buildCookieHeaders(String token, Duration maxAge) {
        ResponseCookie cookie = ResponseCookie.from("auth_token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .sameSite("None")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }




    public static HttpHeaders buildCookieHeaders(String token) {
        return buildCookieHeaders(token, Duration.ofHours(24));
    }


    public static HttpHeaders clearCookieHeaders() {
        return buildCookieHeaders("", Duration.ZERO);
    }
}