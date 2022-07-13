package com.palette.auth.exception;

import com.palette.auth.exception.common.BaseException;
import com.palette.auth.exception.common.ErrorType;
import org.springframework.http.HttpStatus;

public class UserProfileLoadFailedException extends BaseException {
    public UserProfileLoadFailedException() {
        super(HttpStatus.UNAUTHORIZED, ErrorType.A005);
    }
}
