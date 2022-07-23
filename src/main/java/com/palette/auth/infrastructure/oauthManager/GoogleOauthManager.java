package com.palette.auth.infrastructure.oauthManager;

import com.palette.auth.domain.user.SocialType;
import com.palette.auth.dto.GoogleUserInfoResponse;
import com.palette.auth.dto.UserInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

    @Value(MediaType.APPLICATION_JSON_VALUE)
    private String contentType;

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
    protected String getUrl() {
        return url;
    }

    @Override
    protected MultiValueMap<String, String> getOauthTokenRequestBody(String code) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("code", code);
        map.add("client_secret", clientSecret);
        map.add("grant_type", grantType);
        map.add("redirect_uri", redirectUri);
        return map;
    }
}
