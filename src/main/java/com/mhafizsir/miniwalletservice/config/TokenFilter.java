package com.mhafizsir.miniwalletservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhafizsir.miniwalletservice.entity.User;
import com.mhafizsir.miniwalletservice.payload.GeneralResponse;
import com.mhafizsir.miniwalletservice.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public TokenFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (shouldSkip(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Token ")) {
            sendUnauthorizedResponse(response);
            return;
        }

        token = token.replace("Token ", "");
        validateToken(token, response);

        filterChain.doFilter(request, response);
    }

    private void validateToken(String token, HttpServletResponse response) {

        Optional<User> userOptional = userRepository.findByToken(token);
        if(userOptional.isEmpty()){
            sendUnauthorizedResponse(response);
            return;
        }

        User user = userOptional.get();
        if(user.getTokenExpiredAt().isBefore(LocalDateTime.now())){
            sendUnauthorizedResponse(response);
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    private boolean shouldSkip(String servletPath) {
        return servletPath.startsWith("/v1/init");
    }

    private void sendUnauthorizedResponse(HttpServletResponse response) {

        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(new GeneralResponse<>("Fail to authorize request")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
