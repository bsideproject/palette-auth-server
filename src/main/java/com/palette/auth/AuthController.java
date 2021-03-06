package com.palette.auth;

import com.palette.auth.dto.GoogleUserInfoResponse;
import com.palette.auth.dto.LoginRequest;
import com.palette.auth.dto.TokenResponse;
import com.palette.auth.infrastructure.jwtTokenProvider.JwtRefreshTokenInfo;
import com.palette.auth.infrastructure.jwtTokenProvider.JwtTokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthController {

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "PTOKEN_REFRESH";
    private final AuthService authService;
    private final JwtRefreshTokenInfo jwtRefreshTokenInfo;
    WebClient webClient = WebClient.create();
    @Value("${cookie.properties.domain:}")
    private String cookieDomainValue;

    public AuthController(AuthService authService, JwtRefreshTokenInfo jwtRefreshTokenInfo) {
        this.authService = authService;
        this.jwtRefreshTokenInfo = jwtRefreshTokenInfo;
    }

    @GetMapping("/google/callback")
    public ResponseEntity getGoogleCallback(@RequestParam String code) {
        System.out.println(code);
        webClient.post().uri("http://localhost:8080/login/GOOGLE").bodyValue(code).exchangeToMono(response -> {
            System.out.println(response);
            return response.bodyToMono(GoogleUserInfoResponse.class);
        });

        return ResponseEntity.ok("Hello");
    }

    @PostMapping("/login/{socialType}")
    public ResponseEntity<TokenResponse> login(@PathVariable String socialType, @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        TokenResponse tokenResponse = authService.createAccessToken(socialType, loginRequest);
        String email = authService.getEmailFromToken(tokenResponse.getAccessToken(), JwtTokenType.ACCESS_TOKEN);
        ResponseCookie responseCookie = createRefreshTokenCookie(email);
        response.addHeader(SET_COOKIE, responseCookie.toString());
        return ResponseEntity.ok(tokenResponse);
    }

    private ResponseCookie createRefreshTokenCookie(String email) {
        String refreshToken = authService.createRefreshToken(email);
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken).sameSite("Lax").secure(true).httpOnly(true).path("/").maxAge(jwtRefreshTokenInfo.getValidityInSeconds().intValue()).domain(cookieDomainValue).build();
    }
}
