package com.paypal.user_service.util;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Component
public class JWTrequestFilter extends OncePerRequestFilter{
    private final JWTUtil jwtUtil;
    protected JWTrequestFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    //filteration method , whenever a http request is called this method will invoke
    @Override
    protected void doFilterInternal(HttpServletRequest request , HttpServletResponse response , FilterChain chain) throws ServletException, IOException, java.io.IOException {
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
        //if username is not authenticated,we will send it to  security config
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            //token validation (we will verify both token validation and username)
            if(jwtUtil.validateToken(jwt,username)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username , null ,null );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //setting token in security config to make it authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        //Authenticating role
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            jwt = authorizationHeader.substring(7);
            if(jwt == null || jwt.isBlank()){
                chain.doFilter(request , response);
                return; // skip processing if token is empty
            }
            try{
                username = jwtUtil.extractUsername(jwt);
                //Only extract role if jwt is valid
                String role = jwtUtil.extractRole(jwt);
                // use role for authorities as required
                //role based authentication
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username,null, List.of(new SimpleGrantedAuthority(role))
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                chain.doFilter(request,response);
            } catch(Exception e){
                // error
            }
        } else{
            chain.doFilter(request,response);
            return;
        }

        
    }
}
