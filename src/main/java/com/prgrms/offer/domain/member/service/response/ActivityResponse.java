package com.prgrms.offer.domain.member.service.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ActivityResponse {

    private final long sellingArticleCount;
    private final long soldArticleCount;
    private final long reviewCount;

    public static ActivityResponse of(long sellingArticleCount, long soldArticleCount, long reviewCount) {
        return new ActivityResponse(sellingArticleCount, soldArticleCount, reviewCount);
    }
}
