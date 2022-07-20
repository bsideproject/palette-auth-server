package com.palette.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.palette.auth.domain.user.SocialType;
import com.palette.auth.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoUserInfoResponse implements UserInfoResponse {

    private String email;

    @JsonProperty("kakao_account")
    private void email(Map<String,Object> kakaoAccount) {
        email = (String)kakaoAccount.get("email");
    }

    @Override
    public User toUser() {
        return User.builder()
                .email(email)
                .socialTypes(new HashSet<>(List.of(SocialType.KAKAO)))
                .build();
    }
}








