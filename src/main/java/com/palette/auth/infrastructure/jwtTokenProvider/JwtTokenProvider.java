package com.palette.auth.infrastructure.jwtTokenProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    private final JwtAccessTokenInfo jwtAccessTokenInfo;
    private final JwtRefreshTokenInfo jwtRefreshTokenInfo;

    public JwtTokenProvider(JwtAccessTokenInfo jwtAccessTokenInfo, JwtRefreshTokenInfo jwtRefreshTokenInfo) {
        this.jwtAccessTokenInfo = jwtAccessTokenInfo;
        this.jwtRefreshTokenInfo = jwtRefreshTokenInfo;
    }

    public String createAccessToken(String email) {
        return createToken(email, jwtAccessTokenInfo.getValidityInMilliseconds(), jwtAccessTokenInfo.getSecretKey());
    }

    public String createRefreshToken(String email) {
        return createToken(email, jwtRefreshTokenInfo.getValidityInMilliseconds(), jwtRefreshTokenInfo.getSecretKey());
    }

    public String getEmailFromPayLoad(String token, JwtTokenType jwtTokenType) {
        Claims claims = getClaims(token, jwtTokenType);
        return claims.get("email", String.class);
    }

    public Long getTimeToLiveInMilliseconds(JwtTokenType jwtTokenType) {
        if (jwtTokenType.equals(JwtTokenType.ACCESS_TOKEN)) {
            return jwtAccessTokenInfo.getValidityInMilliseconds();
        }
        return jwtRefreshTokenInfo.getValidityInMilliseconds();
    }

    public String getSecretKey(JwtTokenType jwtTokenType) {
        if (jwtAccessTokenInfo.supports(jwtTokenType)) {
            return jwtAccessTokenInfo.getSecretKey();
        }
        return jwtRefreshTokenInfo.getSecretKey();
    }

    private String createToken(String email, Long validityTime, String secretKey) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityTime);

        return Jwts.builder()
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private Claims getClaims(String token, JwtTokenType jwtTokenType) {
        return Jwts
                .parser()
                .setSigningKey(getSecretKey(jwtTokenType))
                .parseClaimsJws(token)
                .getBody();
    }
}
