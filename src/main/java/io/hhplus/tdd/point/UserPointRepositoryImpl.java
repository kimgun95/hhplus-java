package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserPointRepositoryImpl implements UserPointRepository{

    private final UserPointTable userPointTable;

    @Override
    public UserPoint findById(long id) {
        return userPointTable.selectById(id);
    }

    @Override
    public UserPoint saveOrUpdate(long id, long amount) {
        return userPointTable.insertOrUpdate(id, amount);
    }
}
