package com.palette.auth.exception.common;

import com.palette.auth.exception.OauthApiFailedException;
import com.palette.auth.exception.SocialTypeNotFoundException;
import com.palette.auth.exception.TokenExpirationException;
import com.palette.auth.exception.TokenNotValidException;
import com.palette.auth.exception.UserProfileLoadFailedException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {
    A001("A001", "토큰이 유효하지 않습니다.", TokenNotValidException.class),
    A002("A002", "만료된 토큰입니다.", TokenExpirationException.class),
//    A003("A003", "작성자가 아니므로 권한이 없습니다.", NotAuthorException.class),
    A004("A004", "AccessToken을 받아오는데 실패했습니다.", OauthApiFailedException.class),
    A005("A005", "유저정보를 불러오는데 실패했습니다.", UserProfileLoadFailedException.class),
    A007("A007", "존재하지 않는 소셜 로그인 방식입니다.", SocialTypeNotFoundException.class);
//    A008("A008", "액세스 토큰 재발급이 필요합니다.", AccessTokenRenewalException.class),

    private final String code;
    private final String message;
    private final Class<? extends BaseException> classType;
}
