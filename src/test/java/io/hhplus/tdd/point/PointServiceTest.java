package io.hhplus.tdd.point;

import io.hhplus.tdd.constant.Id;
import io.hhplus.tdd.constant.UserValidator;
import io.hhplus.tdd.exception.RestApiException;
import io.hhplus.tdd.exception.UserPointErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointService sut;

    @Mock
    private UserPointRepository userPointRepository;
    @Mock
    private PointHistoryRepository pointHistoryRepository;
    @Mock
    private UserValidator userValidator;

    private final Id<UserPoint, Long> userId = new Id<>(UserPoint.class, 1L);
    private final long point = 1000L;

    private final long historyPointId = 1L;
    private final long amount = 1000L;
    private final long invalidAmount = -1000L;
    private final long bigAmount = 1_000_000_000;
    private final TransactionType typeCharge = TransactionType.CHARGE;
    private final TransactionType typeUse = TransactionType.USE;

    @Test
    void 유저ID를이용해UserPoint조회하기_성공() {
        // given
        UserPoint expectedUserPoint = createUserPoint();
        when(userPointRepository.findById(userId.value())).thenReturn(expectedUserPoint);

        // when
        UserPoint result = sut.searchUserPoint(userId);

        // then
        assertThat(result.id()).isEqualTo(userId.value());
        assertThat(result.point()).isEqualTo(point);

        verify(userPointRepository, times(1)).findById(userId.value());
    }

    @Test
    void 유저ID를이용해PointHistory조회하기_성공() {
        // given
        List<PointHistory> expectedPointHistoryList = createPointHistoryList();
        when(pointHistoryRepository.findAllByUserId(userId.value())).thenReturn(expectedPointHistoryList);

        // when
        List<PointHistory> result = sut.searchUserPointHistory(userId);

        // then
        assertThat(result).isEqualTo(expectedPointHistoryList);

        verify(pointHistoryRepository, times(1)).findAllByUserId(userId.value());
    }

    @Test
    void 포인트AMOUNT를음수로받았을때_에러발생() {
        // given
        UserPoint expectedUserPoint = createUserPoint();
        when(userPointRepository.findById(userId.value())).thenReturn(expectedUserPoint);

        // when
        RestApiException result = assertThrows(RestApiException.class, () -> {
            sut.useUserPoint(userId, invalidAmount);
        });

        // then
        assertThat(result.getErrorCode()).isEqualTo(UserPointErrorCode.INVALID_AMOUNT);
    }

    @Test
    void 포인트충전_성공() {
        // given
        UserPoint expectedSearchedUserPoint = createUserPoint();
        when(userPointRepository.findById(userId.value()))
                .thenReturn(expectedSearchedUserPoint);

        UserPoint expectedChargedUserPoint = addPoint(expectedSearchedUserPoint, amount);
        when(userPointRepository.saveOrUpdate(userId.value(), expectedSearchedUserPoint.point() + amount))
                .thenReturn(expectedChargedUserPoint);

        // when
        UserPoint result = sut.chargeUserPoint(userId, amount);

        // then
        assertThat(result).isEqualTo(expectedChargedUserPoint);
        assertThat(result.point()).isEqualTo(expectedChargedUserPoint.point());

        verify(userPointRepository, times(1)).findById(userId.value());
        verify(userPointRepository, times(1)).saveOrUpdate(userId.value(), expectedSearchedUserPoint.point() + amount);
    }


    @Test
    void 포인트사용하기_성공() {
        // given
        UserPoint expectedSearchedUserPoint = createUserPoint();
        when(userPointRepository.findById(userId.value()))
                .thenReturn(expectedSearchedUserPoint);

        UserPoint expectedUsedUserPoint = addPoint(expectedSearchedUserPoint, -1 * amount);
        when(userPointRepository.saveOrUpdate(userId.value(), expectedSearchedUserPoint.point() - amount))
                .thenReturn(expectedUsedUserPoint);

        // when
        UserPoint result = sut.useUserPoint(userId, amount);

        // then
        assertThat(result).isEqualTo(expectedUsedUserPoint);
        assertThat(result.point()).isEqualTo(expectedUsedUserPoint.point());

        verify(userPointRepository, times(1)).findById(userId.value());
        verify(userPointRepository, times(1)).saveOrUpdate(userId.value(), expectedSearchedUserPoint.point() - amount);
    }

    /*
        충전시 UserPoint에서 최대 잔고를 제한할 수 있다
        사용시 UserPoint에서 잔고 부족에 대한 검증을 할 수 있다
     */
    @Test
    void 포인트충전시최대잔고를초과할때_최대잔고를제한한다() {
        // given
        UserPoint expectedSearchedUserPoint = createUserPoint();
        when(userPointRepository.findById(userId.value()))
                .thenReturn(expectedSearchedUserPoint);

        UserPoint expectedChargedUserPoint = addPoint(expectedSearchedUserPoint, bigAmount);
        when(userPointRepository.saveOrUpdate(userId.value(), expectedSearchedUserPoint.point() + bigAmount))
                .thenReturn(expectedChargedUserPoint);

        // when
        UserPoint result = sut.chargeUserPoint(userId, bigAmount);

        // then
        assertThat(result).isEqualTo(expectedChargedUserPoint);
        assertThat(result.point()).isEqualTo(expectedChargedUserPoint.point());
        assertThat(result.point()).isEqualTo(100_000);

        verify(userPointRepository, times(1)).findById(userId.value());
        verify(userPointRepository, times(1)).saveOrUpdate(userId.value(), expectedSearchedUserPoint.point() + bigAmount);
    }

    @Test
    void 포인트사용시잔액이부족할때_에러발생() {
        // given
        UserPoint userPoint = createUserPoint();

        RestApiException result = assertThrows(RestApiException.class, () -> {
            addPoint(userPoint, -1 * bigAmount);
        });

        assertThat(result.getErrorCode()).isEqualTo(UserPointErrorCode.NOT_ENOUGH_BALANCE);
    }


    private UserPoint addPoint(UserPoint userPoint, long amount) {
        return UserPoint.of(
                userPoint.id(),
                userPoint.point() + amount,
                System.currentTimeMillis()
        );
    }

    private UserPoint createUserPoint() {
        return UserPoint.of(
                userId.value(),
                point,
                System.currentTimeMillis()
        );
    }

    private List<PointHistory> createPointHistoryList() {
        return List.of(
                PointHistory.of(
                        historyPointId,
                        userId.value(),
                        amount,
                        typeCharge,
                        System.currentTimeMillis()
                )
        );
    }

    private PointHistory createPointHistory() {
        return PointHistory.of(
                        historyPointId,
                        userId.value(),
                        amount,
                        typeCharge,
                        System.currentTimeMillis()
        );
    }
}