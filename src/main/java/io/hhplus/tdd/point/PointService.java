package io.hhplus.tdd.point;

import io.hhplus.tdd.constant.Id;
import io.hhplus.tdd.constant.UserValidator;
import io.hhplus.tdd.exception.RestApiException;
import io.hhplus.tdd.exception.UserPointErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Service
public class PointService {

    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final UserValidator userValidator;
    private final ConcurrentHashMap<Long, Lock> userLockMap = new ConcurrentHashMap<>();


    public UserPoint searchUserPoint(Id<UserPoint, Long> userId) {
        userValidator.validateUserExists(userId.value());
        return userPointRepository.findById(userId.value());
    }

    public List<PointHistory> searchUserPointHistory(Id<UserPoint, Long> userId) {
        userValidator.validateUserExists(userId.value());
        return pointHistoryRepository.findAllByUserId(userId.value());
    }

    public UserPoint chargeUserPoint(Id<UserPoint, Long> userId, long amount) {
        Lock lock = userLockMap.computeIfAbsent(userId.value(), k -> new ReentrantLock());
        lock.lock();

        try {
            UserPoint searchUserPoint = searchUserPoint(userId);
            validateAmount(amount);

            // 포인트를 충전한다
            long chargedPoint = searchUserPoint.point() + amount;
            UserPoint chargedUserPoint =
                    userPointRepository.saveOrUpdate(userId.value(), chargedPoint);

            // 포인트 충전 기록을 저장한다
            pointHistoryRepository.save(
                    chargedUserPoint.id(), amount, TransactionType.CHARGE, System.currentTimeMillis()
            );

            return chargedUserPoint;
        } finally {
            lock.unlock();
        }
    }

    public UserPoint useUserPoint(Id<UserPoint, Long> userId, long amount) {
        Lock lock = userLockMap.computeIfAbsent(userId.value(), k -> new ReentrantLock());
        lock.lock();

        try {
            UserPoint searchUserPoint = searchUserPoint(userId);
            validateAmount(amount);

            // 포인트를 사용한다
            long usedPoint = searchUserPoint.point() - amount;
            UserPoint usedUserPoint =
                    userPointRepository.saveOrUpdate(userId.value(), usedPoint);

            // 포인트 사용 기록을 저장한다
            pointHistoryRepository.save(
                    usedUserPoint.id(), amount, TransactionType.USE, System.currentTimeMillis()
            );

            return usedUserPoint;
        } finally {
            lock.unlock();
        }
    }

    private void validateAmount(long amount) {
        if (amount < 0) {
            throw new RestApiException(UserPointErrorCode.INVALID_AMOUNT);
        }
    }
}
