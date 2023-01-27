package com.prgrms.offer.domain.article.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.prgrms.offer.domain.article.model.dto.CodeAndName;
import com.prgrms.offer.domain.article.model.entity.Article;
import com.prgrms.offer.domain.article.model.value.Category;
import com.prgrms.offer.domain.article.model.value.ProductStatus;
import com.prgrms.offer.domain.article.model.value.TradeMethod;
import com.prgrms.offer.domain.article.model.value.TradeStatus;
import com.prgrms.offer.domain.member.service.response.MemberProfileResponse;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ArticleResponse {

    private final Long id;
    private final MemberProfileResponse author;
    private final String title;
    private final String content;
    private final CodeAndName category;
    private final CodeAndName tradeStatus;
    private final CodeAndName productStatus;
    private final CodeAndName tradeMethod;
    private final String tradeArea;
    private final int price;
    private final String mainImageUrl;
    private final int likeCount;
    private final boolean liked;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime modifiedDate;

    public static ArticleResponse of(Article article, boolean liked) {
        return new ArticleResponse(
                article.getId(),
                MemberProfileResponse.from(article.getWriter()),
                article.getTitle(),
                article.getContent(),
                new CodeAndName(
                        Category.of(article.getCategoryCode()).getCode(),
                        Category.of(article.getCategoryCode()).getName()
                ),
                new CodeAndName(
                        TradeStatus.of(article.getTradeStatusCode()).getCode(),
                        TradeStatus.of(article.getTradeStatusCode()).getName()
                ),
                new CodeAndName(
                        ProductStatus.of(article.getProductStatusCode()).getCode(),
                        ProductStatus.of(article.getProductStatusCode()).getName()
                ),
                new CodeAndName(
                        TradeMethod.of(article.getTradeMethodCode()).getCode(),
                        TradeMethod.of(article.getTradeMethodCode()).getName()
                ),
                article.getTradeArea(),
                article.getPrice(),
                article.getMainImageUrl(),
                article.getLikeCount(),
                liked,
                article.getCreatedDate(),
                article.getModifiedDate()
                );
    }
}
