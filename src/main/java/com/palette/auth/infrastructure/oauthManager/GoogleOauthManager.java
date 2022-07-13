package com.palette.auth.infrastructure.oauthManager;

import com.palette.auth.domain.user.SocialType;
import com.palette.auth.dto.GoogleTokenRequest;
import com.palette.auth.dto.GoogleUserInfoResponse;
import com.palette.auth.dto.OauthTokenRequest;
import com.palette.auth.dto.UserInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GoogleOauthManager extends AbstractOauthManager {
    @Value("${google.client.id}")
    private String clientId;
    @Value("${google.client.secret}")
    private String clientSecret;
    @Value("${google.client.redirect-uri}")
    private String redirectUri;
    @Value("${google.client.grant-type}")
    private String grantType;
    @Value("${google.url.access-token}")
    private String url;
    @Value("${google.url.profile}")
    private String profileUrl;

    @Override
    public boolean isSameSocialType(SocialType socialType) {
        return socialType == SocialType.GOOGLE;
    }

    @Override
    protected String getProfileUrl() {
        return this.profileUrl;
    }

    @Override
    protected Class<? extends UserInfoResponse> getResponseType() {
        return GoogleUserInfoResponse.class;
    }

    @Override
    protected OauthTokenRequest getOauthTokenRequest(String code) {
        return GoogleTokenRequest.builder()
                .code(code)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(redirectUri)
                .grantType(grantType)
                .build();
    }

    @Override
    protected String getUrl() {
        return url;
    }
}
