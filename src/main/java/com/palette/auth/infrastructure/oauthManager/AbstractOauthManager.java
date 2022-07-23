package com.palette.auth.infrastructure.oauthManager;

import com.palette.auth.domain.user.User;
import com.palette.auth.dto.OauthTokenResponse;
import com.palette.auth.dto.UserInfoResponse;
import com.palette.auth.exception.OauthApiFailedException;
import com.palette.auth.exception.UserProfileLoadFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;


public abstract class AbstractOauthManager implements OauthManager {
    private final WebClient webClient = WebClient.create();

    protected abstract MultiValueMap<String, String> getOauthTokenRequestBody(String code);

    protected abstract String getUrl();

    protected abstract String getProfileUrl();

    protected abstract Class<? extends UserInfoResponse> getResponseType();

    @Override
    public User getUserInfo(String code) {
        OauthTokenResponse oauthTokenResponse = getAccessToken(code);
        System.out.println(oauthTokenResponse.getAccessToken());
        Mono<UserInfoResponse> userInfoResponseMono = webClient
                .get()
                .uri(getProfileUrl())
                .headers(headers -> headers.setBearerAuth(oauthTokenResponse.getAccessToken()))
                .exchangeToMono(response -> {
                    if (!response.statusCode().equals(HttpStatus.OK)) {
                        throw new UserProfileLoadFailedException();
                    }
                    return response.bodyToMono(getResponseType());
                });
        UserInfoResponse userInfoResponse = userInfoResponseMono.block();
        validateNotNull(userInfoResponse, UserProfileLoadFailedException::new);
        return userInfoResponse.toUser();
    }

    private OauthTokenResponse getAccessToken(String code) {
        MultiValueMap<String, String> formData = getOauthTokenRequestBody(code);
        Mono<OauthTokenResponse> oauthTokenResponseMono = webClient
                .post()
                .uri(getUrl())
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .exchangeToMono(response -> {
                    if (!response.statusCode().equals(HttpStatus.OK)) {
                        throw new OauthApiFailedException();
                    }
                    return response.bodyToMono(OauthTokenResponse.class);
                });
        OauthTokenResponse oauthTokenResponse = oauthTokenResponseMono.block();
        validateNotNull(oauthTokenResponse, OauthApiFailedException::new);
        String accessToken = oauthTokenResponse.getAccessToken();
        validateNotNull(accessToken, OauthApiFailedException::new);
        return oauthTokenResponse;
    }

    private void validateNotNull(Object object, Supplier<RuntimeException> exceptionSupplier) {
        if (object == null) {
            throw exceptionSupplier.get();
        }
    }
}
