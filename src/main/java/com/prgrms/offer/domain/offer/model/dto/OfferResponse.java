package com.prgrms.offer.domain.offer.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OfferResponse {
    private final OfferDto offer;

    private final Integer offerCountOfCurrentMember;

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class OfferDto{
        private final Long id;

        private final OffererResponse offerer;

        private final Long articleId;

        private final int price;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        private final LocalDateTime createdDate;

        private final Boolean isSelected;
    }
}
