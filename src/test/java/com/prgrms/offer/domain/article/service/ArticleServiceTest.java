package com.prgrms.offer.domain.article.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.offer.authentication.presentation.LoginMember;
import com.prgrms.offer.authentication.presentation.LoginMember.Authority;
import com.prgrms.offer.domain.article.model.entity.Article;
import com.prgrms.offer.domain.article.model.entity.LikeArticle;
import com.prgrms.offer.domain.article.model.value.Category;
import com.prgrms.offer.domain.article.model.value.ProductStatus;
import com.prgrms.offer.domain.article.model.value.TradeMethod;
import com.prgrms.offer.domain.article.model.value.TradeStatus;
import com.prgrms.offer.domain.article.repository.ArticleRepository;
import com.prgrms.offer.domain.article.repository.LikeArticleRepository;
import com.prgrms.offer.domain.article.service.response.ArticleResponse;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.member.model.entity.Member.OAuthType;
import com.prgrms.offer.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ArticleServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private LikeArticleRepository likeArticleRepository;

    @Autowired
    private ArticleService articleService;

    @Test
    @DisplayName("로그인 사용자의 게시글 단건 조회 응답을 반환한다. 관심 게시글의 liked 필드는 true이다")
    void getArticleForAuthenticatedMember() {
        // given
        Member member = memberRepository.save(Member.builder()
                .oauthId(1L)
                .oauthType(OAuthType.KAKAO)
                .nickname("행복한 냉장고 3호")
                .build());
        Article article = articleRepository.save(Article
                .builder(member, "냉장고 팝니다.", "중고 냉장고입니다.", 10000)
                        .categoryCode(Category.HOME_APPLIANCES.getCode())
                        .tradeStatusCode(TradeStatus.ON_SALE.getCode())
                        .tradeMethodCode(TradeMethod.DELIVERY.getCode())
                        .productStatusCode(ProductStatus.OLD.getCode())
                        .tradeArea("송파구 잠실동")
                .build());
        likeArticleRepository.save(LikeArticle.builder(member, article).build());

        //when
        ArticleResponse expected = ArticleResponse.of(article, true);
        ArticleResponse articleResponse = articleService.findArticle(article.getId(), new LoginMember(member.getId(), Authority.MEMBER));

        //then
        assertThat(articleResponse).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 사용자의 게시글 단건 조회 응답을 반환한다. 관심 게시글의 liked 필드는 항상 false이다")
    void getArticleForAnonymousMember() {
        // given
        Member member = memberRepository.save(Member.builder()
                .oauthId(1L)
                .oauthType(OAuthType.KAKAO)
                .nickname("행복한 냉장고 3호")
                .build());
        Article article = articleRepository.save(Article
                .builder(member, "냉장고 팝니다.", "중고 냉장고입니다.", 10000)
                .categoryCode(Category.HOME_APPLIANCES.getCode())
                .tradeStatusCode(TradeStatus.ON_SALE.getCode())
                .tradeMethodCode(TradeMethod.DELIVERY.getCode())
                .productStatusCode(ProductStatus.OLD.getCode())
                .tradeArea("송파구 잠실동")
                .build());

        //when
        ArticleResponse expected = ArticleResponse.of(article, false);
        ArticleResponse articleResponse = articleService.findArticle(article.getId(), new LoginMember(Authority.ANONYMOUS));

        //then
        assertThat(articleResponse).usingRecursiveComparison().isEqualTo(expected);
    }
}
