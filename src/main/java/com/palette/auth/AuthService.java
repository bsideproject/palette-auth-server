package com.palette.auth;

import com.palette.auth.domain.token.RefreshToken;
import com.palette.auth.domain.token.RefreshTokenRepository;
import com.palette.auth.domain.user.SocialType;
import com.palette.auth.domain.user.User;
import com.palette.auth.domain.user.UserRepository;
import com.palette.auth.dto.LoginRequest;
import com.palette.auth.dto.TokenResponse;
import com.palette.auth.exception.TokenNotValidException;
import com.palette.auth.infrastructure.jwtTokenProvider.JwtTokenProvider;
import com.palette.auth.infrastructure.jwtTokenProvider.JwtTokenType;
import com.palette.auth.infrastructure.oauthManager.OauthManager;
import com.palette.auth.infrastructure.oauthManager.OauthManagers;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Service
//@Transactional(readOnly = true)
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final OauthManagers oauthManagers;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(JwtTokenProvider jwtTokenProvider, OauthManagers oauthManagers, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.oauthManagers = oauthManagers;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String getEmailFromToken(String accessToken, JwtTokenType tokenType) {
        return jwtTokenProvider.getEmailFromPayLoad(accessToken, tokenType);
    }

    public TokenResponse createAccessToken(String socialType, LoginRequest loginRequest) {
        SocialType socialLoginType = SocialType.of(socialType);
        OauthManager oauthManager = oauthManagers.findOauthManagerBySocialType(socialLoginType);
        User userInfo = oauthManager.getUserInfo(loginRequest.getCode());
        Optional<User> user = userRepository.findByEmail(userInfo.getEmail());
        if (user.isPresent()) {
            if (user.get().addSocialType(socialLoginType)) {
                userRepository.save(user.get());
            }
            return TokenResponse.of(jwtTokenProvider.createAccessToken(user.get().getEmail()));
        }
        User savedUser = userRepository.save(userInfo);
        return TokenResponse.of(jwtTokenProvider.createAccessToken(savedUser.getEmail()));
    }

    public void validateAccessToken(String accessToken) {
        jwtTokenProvider.validateToken(accessToken, JwtTokenType.ACCESS_TOKEN);
    }

    public TokenResponse renewAccessToken(String email) {
        return TokenResponse.of(jwtTokenProvider.createAccessToken(email));
    }

    public String createRefreshToken(String email) {
        String refreshTokenValue = jwtTokenProvider.createRefreshToken(email);
        Long timeToLive = jwtTokenProvider.getTimeToLiveInMilliseconds(JwtTokenType.REFRESH_TOKEN);
        RefreshToken savedRefreshToken = refreshTokenRepository.save(new RefreshToken(email, refreshTokenValue, new Date(new Date().getTime() + timeToLive)));
        return savedRefreshToken.getTokenValue();
    }

    @Transactional
    public void removeRefreshToken(String refreshToken) {
        try {
            validateRefreshToken(refreshToken);
        } catch (HttpClientErrorException.Unauthorized e) {
            return;
        }
        String email = getEmailFromToken(refreshToken, JwtTokenType.REFRESH_TOKEN);
        refreshTokenRepository.deleteByEmailAndTokenValue(email, refreshToken);
    }

    public void validateRefreshToken(String refreshToken) {
        jwtTokenProvider.validateToken(refreshToken, JwtTokenType.REFRESH_TOKEN);
        validateStoredRefreshToken(refreshToken);
    }

    private void validateStoredRefreshToken(String refreshToken) {
        RefreshToken storedRefreshToken = refreshTokenRepository.findByTokenValue(refreshToken)
                .orElseThrow(TokenNotValidException::new);
        String email = jwtTokenProvider.getEmailFromPayLoad(refreshToken, JwtTokenType.REFRESH_TOKEN);
        if (!storedRefreshToken.getEmail().equals(email)) {
            throw new TokenNotValidException();
        }
    }
}
