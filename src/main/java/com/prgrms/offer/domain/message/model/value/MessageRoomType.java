package com.prgrms.offer.domain.message.model.value;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MessageRoomType {

    PURCHASE("purchase"),
    SALE("sale"),
    ;

    private final String type;

    public static String of(Boolean isBuyer) {
        return isBuyer ? PURCHASE.getType() : SALE.getType();
    }

}
