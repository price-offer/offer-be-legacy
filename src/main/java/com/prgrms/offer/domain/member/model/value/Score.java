package com.prgrms.offer.domain.member.model.value;

import com.prgrms.offer.common.message.ResponseMessage;
import com.prgrms.offer.core.error.exception.BusinessException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Score {

    GOOD(2),
    NOT_BAD(1),
    BAD(-1),
    ;

    private final int value;

    public static Score of(int value) {
        return Arrays.stream(Score.values())
                .filter(v -> v.value == value)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ResponseMessage.SCORE_NOT_FOUND));
    }
}
