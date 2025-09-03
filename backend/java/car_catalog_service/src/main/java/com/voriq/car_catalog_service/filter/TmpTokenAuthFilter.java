package com.voriq.car_catalog_service.filter;

import com.voriq.car_catalog_service.exception_handler.exception.TokenAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.util.List;

import static com.voriq.car_catalog_service.utilitie.TokenUtilities.extractTokenFromRequest;

@Component
@RequiredArgsConstructor
public class TmpTokenAuthFilter extends OncePerRequestFilter {

    @Value("${tmp-token.prefix}")
    private String prefix;

    private final StringRedisTemplate redisTemplate;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractTokenFromRequest(request);

        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = getNameByToken(token);

        if (!StringUtils.hasText(username)) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, new TokenAuthenticationException("User is not authorized."));
            return;
        }

        UserDetails principal = User.withUsername(username)
                .password("N/A")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    private String getNameByToken(String token) {
        return redisTemplate.opsForValue().get(prefix + token);
    }
}
