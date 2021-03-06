package com.palette.auth.infrastructure.oauthManager;

import com.palette.auth.domain.user.SocialType;
import com.palette.auth.domain.user.User;

public interface IOauthManager {
    User getUserInfo(String code);

    boolean isSameSocialType(SocialType socialType);
}
