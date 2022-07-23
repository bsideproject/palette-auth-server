package com.palette.auth.infrastructure.oauthManager;

import com.palette.auth.domain.user.SocialType;
import com.palette.auth.dto.NaverUserInfoResponse;
import com.palette.auth.dto.UserInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class NaverOauthManager extends AbstractOauthManager {
    @Value("${naver.client.id}")
    private String clientId;
    @Value("${naver.client.secret}")
    private String clientSecret;
    @Value("${naver.client.redirect-uri}")
    private String redirectUri;
    @Value("${naver.client.grant-type}")
    private String grantType;
    @Value("${naver.url.access-token}")
    private String url;
    @Value("${naver.url.profile}")
    private String profileUrl;

    @Value(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    private String contentType;

    @Override
    public boolean isSameSocialType(SocialType socialType) {
        return socialType == SocialType.NAVER;
    }

    @Override
    protected String getProfileUrl() {
        return this.profileUrl;
    }

    @Override
    protected Class<? extends UserInfoResponse> getResponseType() {
        return NaverUserInfoResponse.class;
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
