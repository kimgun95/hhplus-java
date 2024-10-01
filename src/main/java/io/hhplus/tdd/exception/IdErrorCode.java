package io.hhplus.tdd.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum IdErrorCode implements ErrorCode{
    INVALID_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 ID입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
