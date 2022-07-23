package com.palette.auth.infrastructure.jwtTokenProvider;

public interface JwtTokenInfo {
    String getSecretKey();
    Long getValidityInMilliseconds();
    Long getValidityInSeconds();
    boolean supports(JwtTokenType jwtTokenType);
}
