package com.paypal.user_service.util;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
@Component
public class JWTrequestFilter extends OncePerRequestFilter{
    private final JWTUtil jwtUtil;
    protected JWTrequestFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    //filteration method , whenever a http request is called this method will invoke
    @Override
    protected void doFilterInternal(HttpServletRequest request , HttpServletResponse response , FilterChain chain) throws ServletException, IOException {
        //getting authorization header from http request
        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null; // jwt token
        //Checking if authorizationHeader starts with Bearer
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            jwt = authorizationHeader.substring(7);
            try{
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e){
                //log exception
            }
        }
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){

        }
        
    }
}
