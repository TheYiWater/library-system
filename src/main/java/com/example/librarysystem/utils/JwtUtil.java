package com.example.librarysystem.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET = "library-system-secret-key-2024";
    private static final long EXPIRE = 1000 * 60 * 60 * 2; // 2 小时过期

    public static String generate(Long userId, String username, String role) {
        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE))
                .sign(Algorithm.HMAC256(SECRET));
    }

    public static DecodedJWT verify(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public static Long getUserId(String token) {
        DecodedJWT jwt = verify(token);
        return jwt.getClaim("userId").asLong();
    }

    public static String getRole(String token) {
        DecodedJWT jwt = verify(token);
        return jwt.getClaim("role").asString();
    }
}