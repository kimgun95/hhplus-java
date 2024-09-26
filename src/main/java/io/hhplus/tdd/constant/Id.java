package io.hhplus.tdd.constant;

import io.hhplus.tdd.exception.IdErrorCode;
import io.hhplus.tdd.exception.RestApiException;

import java.util.Objects;

public record Id<R, V>(
        Class<R> reference,
        V value
) {

    public Id(Class<R> reference, V value) {
        validate(value);
        this.reference = reference;
        this.value = value;
    }

    public void validate(V value) {
        if (value instanceof Number && ((Number) value).doubleValue() < 0) {
            throw new RestApiException(IdErrorCode.INVALID_ID);
        }
    }

    public V value() {
        return value;
    }

    @Override
    public String toString() {
        return "ID{" +
                "reference=" + reference +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Id<?, ?> id = (Id<?, ?>) o;
        return Objects.equals(reference, id.reference) &&
                Objects.equals(value, id.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference, value);
    }
}

