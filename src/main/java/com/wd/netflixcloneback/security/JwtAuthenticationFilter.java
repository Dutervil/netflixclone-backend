package com.wd.netflixcloneback.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
     private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt = extractJwtToken(request);

        if (jwt != null) {
            String username = jwtUtil.getUsernameFromToken(jwt);

            if (shouldProcessAuthentication(username)) {
                processAuthentication(request, jwt, username);
            }
        }
          filterChain.doFilter(request, response);
    }


    private String extractJwtToken(HttpServletRequest request) {
       final String authorizationHeader = request.getHeader("Authorization");
       final String requestUri = request.getRequestURI();

       if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
           return authorizationHeader.substring(7);
       }
       else if ((requestUri.contains("/api/files/video/") ||
               requestUri.contains("/api/files/image/")) &&
               request.getParameter("token") != null) {
                 return request.getParameter("token");
       }
       return null;
    }

    private boolean shouldProcessAuthentication(String username) {
        return username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void processAuthentication(HttpServletRequest request, String jwt, String username) {

        if (jwtUtil.validateToken(jwt)) {
            UserDetails userDetails = createUserDetailFromJwt(jwt,username);
            setAuthenticationInContext(request,userDetails);
        }
    }



    private UserDetails createUserDetailFromJwt(String jwt, String username) {
        String role= jwtUtil.getRoleFromToken(jwt);
        return User.builder().username(username)
                .password("[PROTECTED]")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+role)))
                .build();
    }

    private void setAuthenticationInContext(HttpServletRequest request, UserDetails userDetails) {
     UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
     authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
     SecurityContextHolder.getContext().setAuthentication(authentication);

    }

}
