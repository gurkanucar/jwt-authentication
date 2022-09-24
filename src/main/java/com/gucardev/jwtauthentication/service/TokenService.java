package com.gucardev.jwtauthentication.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gucardev.jwtauthentication.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class TokenService {

    @Value("${jwt-variables.KEY}")
    private String KEY;

    @Value("${jwt-variables.ISSUER}")
    private String ISSUER;

    @Value("${jwt-variables.EXPIRES_ACCESS_TOKEN_MINUTE}")
    private Integer EXPIRES_ACCESS_TOKEN_MINUTE;


    public String generateToken(UserDetails userDetails) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + (EXPIRES_ACCESS_TOKEN_MINUTE * 60 * 1000)))
                .withIssuer(ISSUER)
                .sign(Algorithm.HMAC256(KEY.getBytes()));
    }

    ////////////////VERIFYYYYYY???????????
    public DecodedJWT verifyJWT(String token) {
        Algorithm algorithm = Algorithm.HMAC256(KEY.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).acceptExpiresAt(EXPIRES_ACCESS_TOKEN_MINUTE * 60).build();
        try {
            return verifier.verify(token);
        } catch (Exception e) {
            throw new GeneralException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}
