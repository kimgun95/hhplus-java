package io.hhplus.tdd.constant;

import io.hhplus.tdd.exception.IdErrorCode;
import io.hhplus.tdd.exception.RestApiException;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class IdTest {

    @Test
    void 유저의ID를음수로받았을때_에러발생() {
        // given, when
        RestApiException result = assertThrows(RestApiException.class, () -> {
            new Id<>(UserPoint.class, -1L);
        });

        // then
        assertThat(result.getErrorCode()).isEqualTo(IdErrorCode.INVALID_ID);
    }
}