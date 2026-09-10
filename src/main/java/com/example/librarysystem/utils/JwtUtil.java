package com.example.librarysystem.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtUtil {

    private static final String SECRET = "library-system-secret-key";
    private static final long EXPIRE = 7 * 24 * 60 * 60 * 1000L; // 7天

    /**
     * 生成 Token
     */
    public static String generate(Long userId, String username, Integer role) {
        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withClaim("role", role)
                .withIssuedAt(new java.util.Date())
                .withExpiresAt(new java.util.Date(System.currentTimeMillis() + EXPIRE))
                .sign(Algorithm.HMAC256(SECRET));
    }

    /**
     * 验证并解析 Token
     */
    public static DecodedJWT verify(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return JWT.require(algorithm).build().verify(token);
    }

    /**
     * 从 Token 获取 userId
     */
    public static Long getUserId(String token) {
        DecodedJWT jwt = verify(token);
        return jwt.getClaim("userId").asLong();
    }

    /**
     * 从 Token 获取 role
     */
    public static Integer getRole(String token) {
        DecodedJWT jwt = verify(token);
        Object roleObj = jwt.getClaim("role").as(Object.class);
        if (roleObj instanceof Integer) {
            return (Integer) roleObj;
        } else if (roleObj instanceof String) {
            return "ADMIN".equals(roleObj) ? 1 : 0;
        }
        return 0;
    }
}