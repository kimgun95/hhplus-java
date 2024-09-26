package io.hhplus.tdd.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserPointErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다"),
    NOT_ENOUGH_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족합니다"),
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "유효하지 않은 사용량입니다"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
