package com.prgrms.offer.domain.article.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class ArticleWithOfferBriefViewResponse {
    private final Long id;

    private final String mainImageUrl;

    private final String title;

    private final int price;

    private final String tradeArea;

    private final CodeAndName tradeStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime modifiedDate;

    private final Boolean isLiked;

    private final OfferDto offer;

    @Getter
    @RequiredArgsConstructor
    public static class OfferDto{
        private final int price;

        private final Boolean isSelected;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        private final LocalDateTime createdDate;
    }
}
