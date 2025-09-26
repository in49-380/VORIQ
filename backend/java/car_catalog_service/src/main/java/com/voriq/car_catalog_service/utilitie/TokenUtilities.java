package com.voriq.car_catalog_service.utilitie;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;

public class TokenUtilities {

    public static String extractTokenFromRequest(HttpServletRequest request) {
        return extractTokenFromHeader(request.getHeader(HttpHeaders.AUTHORIZATION));
    }

    public static String extractTokenFromRequest(NativeWebRequest webRequest) {
        return extractTokenFromHeader(webRequest.getHeader(HttpHeaders.AUTHORIZATION));
    }

    private static String extractTokenFromHeader(String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (StringUtils.hasText(token)) {
                return token;
            }
        }
        return null;
    }
}
