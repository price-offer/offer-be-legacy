package com.prgrms.offer.domain.member.service.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MyActivityResponse {

    private final long sellingArticleCount;
    private final long likedArticleCount;
    private final long soldArticleCount;
    private final long reviewCount;

    public static MyActivityResponse of(long sellingArticleCount, long likedArticleCount, long soldArticleCount,
                                        long reviewCount) {
        return new MyActivityResponse(sellingArticleCount, likedArticleCount, soldArticleCount, reviewCount);
    }
}
