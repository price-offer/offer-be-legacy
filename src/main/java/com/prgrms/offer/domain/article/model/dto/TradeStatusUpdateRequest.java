package com.prgrms.offer.domain.article.model.dto;

import com.prgrms.offer.common.message.DtoValidationMessage;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TradeStatusUpdateRequest {
    @NotNull(message = DtoValidationMessage.INVALID_CODE)
    private Integer code;
}
