package com.prgrms.offer.domain.article.service;

import com.prgrms.offer.domain.article.model.dto.ArticleBriefViewResponse;
import com.prgrms.offer.domain.article.model.dto.ArticleCreateOrUpdateRequest;
import com.prgrms.offer.domain.article.model.dto.ArticleCreateOrUpdateResponse;
import com.prgrms.offer.domain.article.model.dto.ArticleDetailResponse;
import com.prgrms.offer.domain.article.model.dto.CodeAndName;
import com.prgrms.offer.domain.article.model.dto.CodeAndNameInfosResponse;
import com.prgrms.offer.domain.article.model.entity.Article;
import com.prgrms.offer.domain.article.model.value.Category;
import com.prgrms.offer.domain.article.model.value.ProductStatus;
import com.prgrms.offer.domain.article.model.value.TradeMethod;
import com.prgrms.offer.domain.article.model.value.TradeStatus;
import com.prgrms.offer.domain.article.repository.TemporalArticle;
import com.prgrms.offer.domain.member.model.entity.Member;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class ArticleConverter {
    public ArticleBriefViewResponse toArticleBriefViewResponse(Article article, boolean isLiked){
        return ArticleBriefViewResponse.builder()
                .id(article.getId())
                .mainImageUrl(article.getMainImageUrl())
                .title(article.getTitle())
                .price(article.getPrice())
                .tradeArea(article.getTradeArea())
                .createdDate(article.getCreatedDate())
                .modifiedDate(article.getModifiedDate())
                .isLiked(isLiked)
                .tradeStatus(
                        new CodeAndName(
                                TradeStatus.of(article.getTradeStatusCode()).getCode(),
                                TradeStatus.of(article.getTradeStatusCode()).getName()
                        )
                )
                .build();
    }

    public ArticleBriefViewResponse toArticleBriefViewResponse(TemporalArticle temporalArticle, boolean isLiked){
        return ArticleBriefViewResponse.builder()
                .id(temporalArticle.getId())
                .mainImageUrl(temporalArticle.getMainImageUrl())
                .title(temporalArticle.getTitle())
                .price(temporalArticle.getPrice())
                .tradeArea(temporalArticle.getTradeArea())
                .createdDate(temporalArticle.getCreatedDate())
                .modifiedDate(temporalArticle.getModifiedDate())
                .isLiked(isLiked)
                .tradeStatus(
                        new CodeAndName(
                                TradeStatus.of(temporalArticle.getTradeStatusCode()).getCode(),
                                TradeStatus.of(temporalArticle.getTradeStatusCode()).getName()
                        )
                )
                .build();
    }

    public CodeAndNameInfosResponse toCodeAndNameInfosResponse() {
        var response = new CodeAndNameInfosResponse();

        for(var category : Category.getAllCategory()){
            var codeAndName = new CodeAndName(category.getCode(), category.getName());
            response.getCategories().add(codeAndName);
        }

        for(var productStatus : ProductStatus.getAllProductStatus()){
            var codeAndName = new CodeAndName(productStatus.getCode(), productStatus.getName());
            response.getProductStatus().add(codeAndName);
        }

        for(var tradeMethod : TradeMethod.getAllTradeMethod()){
            var codeAndName = new CodeAndName(tradeMethod.getCode(), tradeMethod.getName());
            response.getTradeMethod().add(codeAndName);
        }

        for(var tradeStatus : TradeStatus.getAllTradeStatus()){
            var codeAndName = new CodeAndName(tradeStatus.getCode(), tradeStatus.getName());
            response.getTradeStatus().add(codeAndName);
        }

        return response;
    }

    public Article toEntity(ArticleCreateOrUpdateRequest request, Member writer) {
        return Article.builder(writer, request.getTitle(), request.getContent(), request.getPrice())
                .likeCount(0)
                .categoryCode(Category.of(request.getCategoryCode()).getCode())
                .productStatusCode(ProductStatus.of(request.getProductStatusCode()).getCode())
                .tradeArea(request.getTradeArea())
                .tradeMethodCode(TradeMethod.of(request.getTradeMethodCode()).getCode())
                .tradeStatusCode(TradeStatus.ON_SALE.getCode())
                .mainImageUrl(request.getImageUrls() == null || request.getImageUrls().isEmpty() ? null : request.getImageUrls().get(0))
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();
    }

    public ArticleCreateOrUpdateResponse toArticleCreateOrUpdateResponse(Article article) {
        return new ArticleCreateOrUpdateResponse(
                article.getId(),
                article.getCreatedDate(),
                article.getModifiedDate()
        );
    }

    public ArticleDetailResponse toArticleDetailResponse(Article article, boolean isLiked) {
        Member writer = article.getWriter();

        var articleDto = ArticleDetailResponse.ArticleDto.builder()
                .id(article.getId())
                .author(
                        ArticleDetailResponse.AuthorDetail.builder()
                        .id(writer.getId())
                        .offerLevel(writer.getOfferLevel())
                        .nickname(writer.getNickname())
                        .profileImageUrl(writer.getProfileImageUrl())
                        .build()
                )
                .title(article.getTitle())
                .content(article.getContent())
                .category(
                        new CodeAndName(
                                Category.of(article.getCategoryCode()).getCode(),
                                Category.of(article.getCategoryCode()).getName()
                        )
                )
                .tradeStatus(
                        new CodeAndName(
                                TradeStatus.of(article.getTradeStatusCode()).getCode(),
                                TradeStatus.of(article.getTradeStatusCode()).getName()
                        )
                )
                .productStatus(
                        new CodeAndName(
                                ProductStatus.of(article.getProductStatusCode()).getCode(),
                                ProductStatus.of(article.getProductStatusCode()).getName()
                        )
                )
                .tradeArea(article.getTradeArea())
                .tradeMethod(
                        new CodeAndName(
                                TradeMethod.of(article.getTradeMethodCode()).getCode(),
                                TradeMethod.of(article.getTradeMethodCode()).getName()
                        )
                )
                .price(article.getPrice())
                .mainImageUrl(article.getMainImageUrl())
                .createdDate(article.getCreatedDate())
                .modifiedDate(article.getModifiedDate())
                .likeCount(article.getLikeCount())
                .isLiked(isLiked)
                .build();

        return new ArticleDetailResponse(articleDto);
    }
}
