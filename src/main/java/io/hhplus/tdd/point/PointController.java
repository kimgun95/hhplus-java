package io.hhplus.tdd.point;

import io.hhplus.tdd.constant.Id;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private final PointService pointService;

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public ResponseEntity<UserPoint> point(
            @PathVariable long id
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(pointService.searchUserPoint(new Id<>(UserPoint.class, id)));
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public ResponseEntity<List<PointHistory>> history(
            @PathVariable long id
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(pointService.searchUserPointHistory(new Id<>(UserPoint.class, id)));
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public ResponseEntity<UserPoint> charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(pointService.chargeUserPoint(new Id<>(UserPoint.class, id), amount));
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public ResponseEntity<UserPoint> use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(pointService.useUserPoint(new Id<>(UserPoint.class, id), amount));
    }
}
