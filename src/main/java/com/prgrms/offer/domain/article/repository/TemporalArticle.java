package com.prgrms.offer.domain.article.repository;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TemporalArticle {
    private Long id;

    private String mainImageUrl;

    private String title;

    private int price;

    private String tradeArea;

    private int tradeStatusCode;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;
}
