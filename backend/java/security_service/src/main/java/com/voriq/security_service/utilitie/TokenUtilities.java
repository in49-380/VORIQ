package com.voriq.security_service.utilitie;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.UUID;

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

    public static String getMaskedUuid(UUID uuid) {
        return getMaskedUuid(uuid.toString());
    }

    public static String getMaskedUuid(String uuid) {
        String mask = "********";
        if (uuid == null) return mask;
        String uuidString = uuid;
        return mask + uuidString.substring(uuidString.length() - 3);
    }

    public static boolean isUuid(String s) {
        if (s == null) return false;
        try {
            UUID.fromString(s);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
