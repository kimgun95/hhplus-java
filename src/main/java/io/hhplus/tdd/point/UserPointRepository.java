package io.hhplus.tdd.point;

public interface UserPointRepository {
    UserPoint findById(long id);
    UserPoint saveOrUpdate(long id, long amount);
}
