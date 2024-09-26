package io.hhplus.tdd.constant;

import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.UserPointRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @InjectMocks
    private UserValidator sut;

    @Mock
    private UserPointRepository userPointRepository;

    private final Id<UserPoint, Long> userId = new Id<>(UserPoint.class, 1L);

    @Test
    void 존재하지않는유저조회하기_에러가발생하지않음() {
        // given
        doReturn(UserPoint.empty(userId.value())).when(userPointRepository).findById(userId.value());

        // when
        boolean result = sut.validateUserExists(userId.value());

        // then
        assertThat(result).isEqualTo(true);
    }
}