package com.prgrms.offer.domain.article.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ViewCountResponse {
    private final long articleId;
    private final long viewCount;
}
