package io.hhplus.tdd.constant;

import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserValidator {

    private final UserPointRepository userPointRepository;

    // 현재 userId를 통한 '유저 조회'는 유의미한 기능을 하지 않음
    // 무조건 해당 유저가 존재하게끔 반환값이 오기 때문
    // 따라서 검증 메서드만 생성해두었다, 별다른 에러를 발생시킬 수 없다.
    public boolean validateUserExists(long userId) {
        UserPoint userPoint = userPointRepository.findById(userId);
        return true;
    }
}
