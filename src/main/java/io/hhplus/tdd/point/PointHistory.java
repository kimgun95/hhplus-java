package io.hhplus.tdd.point;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {

    public static PointHistory of(
            long id,
            long userId,
            long amount,
            TransactionType type,
            long updateMillis
    ) {
        return new PointHistory(id, userId, amount, type, updateMillis);
    }
}
