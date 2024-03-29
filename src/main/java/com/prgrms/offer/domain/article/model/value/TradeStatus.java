package com.prgrms.offer.domain.article.model.value;

import com.prgrms.offer.common.message.ResponseMessage;
import com.prgrms.offer.core.error.exception.BusinessException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TradeStatus {
    RESERVING("예약중", 2),
    ON_SALE("판매중", 4),
    COMPLETED("거래완료", 8),

    ;

    private final String name;
    private final int code;

    public static TradeStatus of(int code) {
        return Arrays.stream(TradeStatus.values())
                .filter(v -> v.code == code)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ResponseMessage.TRADE_STATUS_NOT_FOUND));
    }

    public static TradeStatus of(String name) {
        return Arrays.stream(TradeStatus.values())
                .filter(v -> v.name.equals(name))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ResponseMessage.TRADE_STATUS_NOT_FOUND));
    }

    public static List<TradeStatus> getAllTradeStatus() {
        final List<TradeStatus> result = new ArrayList<>();

        for(var tradeStatus : TradeStatus.values()){
            result.add(tradeStatus);
        }

        return result;
    }

    public static boolean isCompleted(int code){
        return COMPLETED.getCode() == code;
    }
}
