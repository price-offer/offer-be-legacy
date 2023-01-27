package com.prgrms.offer.documentation.article;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.offer.documentation.ApiDocumentationTest;
import com.prgrms.offer.domain.article.controller.ArticleController;
import com.prgrms.offer.domain.article.model.entity.Article;
import com.prgrms.offer.domain.article.model.value.Category;
import com.prgrms.offer.domain.article.model.value.ProductStatus;
import com.prgrms.offer.domain.article.model.value.TradeMethod;
import com.prgrms.offer.domain.article.model.value.TradeStatus;
import com.prgrms.offer.domain.article.service.response.ArticleResponse;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.member.model.entity.Member.OAuthType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ArticleController.class)
public class ArticleDocumentationTest extends ApiDocumentationTest {

    @Test
    @DisplayName("게시글 단건 조회")
    void getArticle() throws Exception {
        given(articleService.findArticle(eq(1L), any()))
                .willReturn(
                        ArticleResponse.of(Article.builder(Member.builder()
                                                .id(1L)
                                                .oauthId(1L).oauthType(OAuthType.KAKAO).nickname("행복한 냉장고 3호")
                                                .profileImageUrl("http://test.jpg").build(),
                                        "냉장고 팝니다.",
                                        "중고 냉장고 팔아요 사용한 지 1년 됐어요.", 100000)
                                .id(1L)
                                .categoryCode(Category.HOME_APPLIANCES.getCode())
                                .productStatusCode(ProductStatus.OLD.getCode())
                                .tradeStatusCode(TradeStatus.ON_SALE.getCode())
                                .tradeMethodCode(TradeMethod.DELIVERY.getCode())
                                .tradeArea("송파구 잠실동")
                                .mainImageUrl("http://test.jpg")
                                .likeCount(3)
                                .createdDate(LocalDateTime.now())
                                .modifiedDate(LocalDateTime.now())
                                .build(), false
                        )
                );

        ResultActions result = mockMvc.perform(
                get("/api/articles/{articleId}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("articles/get-article",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("articleId").description("게시글 아이디")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 아이디"),
                                subsectionWithPath("author").description("작성자 프로필"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 상세 내용"),
                                subsectionWithPath("category").description("게시글 카테고리"),
                                subsectionWithPath("tradeStatus").description("거래 상태"),
                                subsectionWithPath("productStatus").description("상품 상태"),
                                subsectionWithPath("tradeMethod").description("거래 방법"),
                                fieldWithPath("tradeArea").type(JsonFieldType.STRING).description("거래 지역"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("상품 가격"),
                                fieldWithPath("mainImageUrl").type(JsonFieldType.STRING).description("게시글 메인 이미지 URL"),
                                fieldWithPath("likeCount").type(JsonFieldType.NUMBER)
                                        .description("이 게시글에 관심을 갖고 있는 사용자 수"),
                                fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("게시글 조회수"),
                                fieldWithPath("liked").type(JsonFieldType.BOOLEAN)
                                        .description("관심 게시글 여부(비로그인 사용자의 경우 항상 false)"),
                                fieldWithPath("createdDate").type(JsonFieldType.STRING).attributes(getDateFormat())
                                        .description("게시글 생성 시간"),
                                fieldWithPath("modifiedDate").type(JsonFieldType.STRING).attributes(getDateFormat())
                                        .description("마지막 수정 시간")
                        )
                ));
    }
}
