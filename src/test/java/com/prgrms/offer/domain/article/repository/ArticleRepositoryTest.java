package com.prgrms.offer.domain.article.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.prgrms.offer.core.config.JpaConfig;
import com.prgrms.offer.core.config.QueryDSLConfig;
import com.prgrms.offer.domain.article.model.entity.Article;
import com.prgrms.offer.domain.article.model.value.TradeStatus;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.member.model.entity.Member.OAuthType;
import com.prgrms.offer.domain.member.repository.MemberRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({JpaConfig.class, QueryDSLConfig.class})
class ArticleRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    @DisplayName("데이터 저장 시, 생성 시간도 저장한다")
    void saveDateTime() {
        Member member = memberRepository.save(Member.builder().
                nickname("행복한 냉장고 3호")
                .oauthId(1L)
                .oauthType(OAuthType.KAKAO)
                .build());
        Article article = articleRepository.save(Article.builder()
                .writer(member)
                .title("냉장고 팝니다")
                .content("안쓰는 냉장고 팔아요")
                .price(10000)
                .build());

        LocalDateTime afterSave = LocalDateTime.now();
        assertThat(article.getCreatedDate()).isBeforeOrEqualTo(afterSave);
    }

    @Test
    @DisplayName("작성자와 판매 상태를 입력받아 일치하는 게시글의 수를 반환한다")
    void countByWriter() {
        Member member = memberRepository.save(Member.builder().
                nickname("행복한 냉장고 3호")
                .oauthId(1L)
                .oauthType(OAuthType.KAKAO)
                .build());

        articleRepository.save(Article.builder()
                .writer(member)
                .title("냉장고 팝니다")
                .content("안쓰는 냉장고 팔아요")
                .tradeStatusCode(TradeStatus.COMPLETED.getCode())
                .price(10000)
                .build());

        articleRepository.save(Article.builder()
                .writer(member)
                .title("냉장고 팝니다")
                .content("안쓰는 냉장고 팔아요")
                .tradeStatusCode(TradeStatus.ON_SALE.getCode())
                .price(10000)
                .build());

        articleRepository.save(Article.builder()
                .writer(member)
                .title("냉장고 팝니다")
                .content("안쓰는 냉장고 팔아요")
                .tradeStatusCode(TradeStatus.ON_SALE.getCode())
                .price(10000)
                .build());

        long onSaleCount = articleRepository.countByWriterAndTradeStatusCode(member, TradeStatus.ON_SALE.getCode());
        long onCompleteCount = articleRepository.countByWriterAndTradeStatusCode(member, TradeStatus.COMPLETED.getCode());
        assertAll(
                () -> assertThat(onSaleCount).isEqualTo(2),
                () -> assertThat(onCompleteCount).isEqualTo(1)
        );
    }
}
