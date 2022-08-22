package com.prgrms.offer.domain.offer.model.dto;

import com.prgrms.offer.common.message.DtoValidationMessage;
import javax.validation.constraints.Min;
import lombok.Getter;

@Getter
public class OfferCreateRequest {
    @Min(value = 0, message = DtoValidationMessage.INVALID_OFFER_PRICE)
    private int price;
}
