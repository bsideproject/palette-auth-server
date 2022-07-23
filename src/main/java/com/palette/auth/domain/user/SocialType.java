package com.palette.auth.domain.user;

import com.palette.auth.exception.SocialTypeNotFoundException;

import java.util.Arrays;

public enum SocialType {
    GOOGLE, KAKAO, NAVER;

    public static SocialType of(String input) {
        return Arrays.stream(values())
                .filter(socialType -> socialType.name().equals(input.toUpperCase()))
                .findFirst()
                .orElseThrow(SocialTypeNotFoundException::new);
    }
}
