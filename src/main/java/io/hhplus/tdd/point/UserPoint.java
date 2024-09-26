package io.hhplus.tdd.point;

import io.hhplus.tdd.exception.RestApiException;
import io.hhplus.tdd.exception.UserPointErrorCode;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public UserPoint {
        point = validate(point);
    }

    public long validate(long point) {
        if (point < 0) {
            throw new RestApiException(UserPointErrorCode.NOT_ENOUGH_BALANCE);
        }
        return Math.min(point, 100_000);
    }

    public static UserPoint of(long id, long point, long updateMillis) {
        return new UserPoint(id, point, updateMillis);
    }

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }
}
