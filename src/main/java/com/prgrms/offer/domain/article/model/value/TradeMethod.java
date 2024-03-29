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
public enum TradeMethod {
    DIRECT("직거래", 2),
    DELIVERY("택배거래", 4),
    BOTH("상관없음", 8),

    ;

    private final String name;
    private final int code;

    public static TradeMethod of(int code) {
        return Arrays.stream(TradeMethod.values())
                .filter(v -> v.code == code)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ResponseMessage.TRADE_METHOD_NOT_FOUND));
    }

    public static TradeMethod of(String name) {
        return Arrays.stream(TradeMethod.values())
                .filter(v -> v.name.equals(name))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ResponseMessage.TRADE_METHOD_NOT_FOUND));
    }

    public static List<TradeMethod> getAllTradeMethod() {
        final List<TradeMethod> result = new ArrayList<>();

        for(var tradeMethod : TradeMethod.values()){
            result.add(tradeMethod);
        }

        return result;
    }
}
