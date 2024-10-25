package com.artaxer.tinybank.security;

import com.artaxer.tinybank.model.UserAccount;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtManager {
    private final String SECRET = "This15" + "is sample" + "secret for tiny bank";
    private final Algorithm algorithm= Algorithm.HMAC512(SECRET);
    public String generateToken(UserAccount userAccount) {
        Date expiryDate = new Date(new Date().getTime() + 86400000L);
        return JWT.create().withIssuer("tinybank.com")
                .withSubject(userAccount.getUsername())
                .withExpiresAt(expiryDate)
                .sign(algorithm);
    }
    public String getUsernameFromJWT(String token) {
        try {
            return JWT.require(algorithm).build().verify(token).getSubject();
        }catch (Exception e){
            throw e;
        }
    }
}
