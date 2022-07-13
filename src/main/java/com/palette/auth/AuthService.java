package com.palette.auth;

import com.palette.auth.domain.token.RefreshToken;
import com.palette.auth.domain.token.RefreshTokenRepository;
import com.palette.auth.domain.user.SocialType;
import com.palette.auth.domain.user.User;
import com.palette.auth.domain.user.UserRepository;
import com.palette.auth.dto.LoginRequest;
import com.palette.auth.dto.TokenResponse;
import com.palette.auth.infrastructure.jwtTokenProvider.JwtTokenProvider;
import com.palette.auth.infrastructure.jwtTokenProvider.JwtTokenType;
import com.palette.auth.infrastructure.oauthManager.IOauthManager;
import com.palette.auth.infrastructure.oauthManager.OauthManagers;
import org.springframework.stereotype.Service;

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

    public TokenResponse createAccessToken(String socialType, LoginRequest loginRequest) {
        SocialType socialLoginType = SocialType.of(socialType);
        IOauthManager oauthManager = oauthManagers.findOauthManagerBySocialType(socialLoginType);
        User userInfo = oauthManager.getUserInfo(loginRequest.getCode());
        Optional<User> user = userRepository.findByEmail(userInfo.getEmail());
        if (user.isPresent()) {
            if (!user.get().addSocialType(socialLoginType)) {
                userRepository.save(user.get());
            }
            return TokenResponse.of(jwtTokenProvider.createAccessToken(user.get().getEmail()));
        }
        User savedUser = userRepository.save(userInfo);
        return TokenResponse.of(jwtTokenProvider.createAccessToken(savedUser.getEmail()));
    }

    public String createRefreshToken(String email) {
        // db에 이미 email에 해당하는 refreshToken이 있다면 삭제하고 새로 생성한다.
        Optional<RefreshToken> storedRefreshToken = refreshTokenRepository.findByEmail(email);
        storedRefreshToken.ifPresent(refreshTokenRepository::delete);

        String refreshTokenValue = jwtTokenProvider.createRefreshToken(email);
        Long timeToLive = jwtTokenProvider.getTimeToLiveInMilliseconds(JwtTokenType.REFRESH_TOKEN);
        RefreshToken savedRefreshToken = refreshTokenRepository.save(new RefreshToken(email, refreshTokenValue, new Date(new Date().getTime() + timeToLive)));
        return savedRefreshToken.getTokenValue();
    }

    public String getEmailFromToken(String accessToken, JwtTokenType tokenType) {
        return jwtTokenProvider.getEmailFromPayLoad(accessToken, tokenType);
    }
}
