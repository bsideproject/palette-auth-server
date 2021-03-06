package com.palette.auth.infrastructure.jwtTokenProvider;

public interface IJwtTokenInfo {
    String getSecretKey();
    Long getValidityInMilliseconds();
    Long getValidityInSeconds();
    boolean supports(JwtTokenType jwtTokenType);
}
