package com.palette.auth.exception;

import com.palette.auth.exception.common.BaseException;
import com.palette.auth.exception.common.ErrorType;
import org.springframework.http.HttpStatus;

public class SocialTypeNotFoundException extends BaseException {
    public SocialTypeNotFoundException(){
        super(HttpStatus.NOT_FOUND, ErrorType.A007);
    }
}
