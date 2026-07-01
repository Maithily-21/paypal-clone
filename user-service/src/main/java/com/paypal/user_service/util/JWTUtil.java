package com.paypal.user_service.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JWTUtil {
    //secret key must be of 32 bytes
    private static final String SECRET = "secret123secret123secret123";

    private Key getSignKey(){
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }
    public String extractEmail(String token){
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token , String username){
        try{
            extractEmail(token); //if parsing succeeds , token is valid
            return true;
        } catch (Exception e){
            return false;
        }
    }


    // Here we have assumed that username is same as email so we used same method
    public String extractUsername(String token){
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String generateToken(Map<String,Object> claims , String email ){
        return Jwts.builder().setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact(); // build
    }
}
