package com.palette.auth.dto;

import com.palette.auth.domain.user.User;

public interface UserInfoResponse {
    User toUser();
}
