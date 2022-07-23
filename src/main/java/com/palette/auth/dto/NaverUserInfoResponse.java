package com.palette.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.palette.auth.domain.user.SocialType;
import com.palette.auth.domain.user.User;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class NaverUserInfoResponse implements UserInfoResponse{
    private String email;

    @JsonProperty("response")
    private void getEmail(Map<String,String> response) {
        this.email = response.get("email");
    }
    @Override
    public User toUser() {
        return User.builder()
                .email(email)
                .socialTypes(new HashSet<>(List.of(SocialType.NAVER)))
                .build();
    }
}
