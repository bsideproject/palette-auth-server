package com.palette.auth.infrastructure.oauthManager;

import com.palette.auth.domain.user.SocialType;
import com.palette.auth.exception.SocialTypeNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class OauthManagers {
    private final Collection<OauthManager> IOauthManagerList;

    public OauthManagers(Collection<OauthManager> IOauthManagerList) {
        this.IOauthManagerList = IOauthManagerList;
    }

    public OauthManager findOauthManagerBySocialType(SocialType socialType) {
        return IOauthManagerList.stream()
                .filter(IOauthManager -> IOauthManager.isSameSocialType(socialType))
                .findFirst()
                .orElseThrow(SocialTypeNotFoundException::new);
    }
}
