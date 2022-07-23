package com.prgrms.offer.domain.review.service;

import com.prgrms.offer.core.config.PropertyProvider;
import com.prgrms.offer.core.jwt.JwtAuthentication;
import com.prgrms.offer.domain.article.model.entity.Article;
import com.prgrms.offer.domain.article.repository.ArticleRepository;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.member.repository.MemberRepository;
import com.prgrms.offer.domain.offer.repository.OfferRepository;
import com.prgrms.offer.domain.review.model.dto.ReviewResponse;
import com.prgrms.offer.domain.review.model.entity.Review;
import com.prgrms.offer.domain.review.model.value.OfferLevel;
import com.prgrms.offer.domain.review.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ReviewConverter converter;

    @Mock
    private PropertyProvider propertyProvider;

    @Mock
    JwtAuthentication authentication;

    @Mock
    Article article;

    @Mock
    Member member;

    @Mock
    Review review;

    @Mock
    ReviewResponse reviewResponse;

    @Test
    @DisplayName(value = "유저 고유 정보와 articleID로 review를 조회할 수 있다.")
    void findByArticleIdAndReviewerAuthSuccessTest() {
        // given
        given(articleRepository.findById(anyLong())).willReturn(Optional.of(article));
        given(memberRepository.findByPrincipal(any())).willReturn(Optional.of(member));
        given(reviewRepository.findByReviewerAndArticle(any(), any())).willReturn(Optional.of(review));
        given(converter.toReviewResponse(any(), any())).willReturn(reviewResponse);

        // when
        reviewService.findByArticleIdAndReviewerAuth(1L, authentication);

        //then
        then(articleRepository).should(BDDMockito.atLeast(1)).findById(anyLong());
        then(memberRepository).should(BDDMockito.atLeast(1)).findByPrincipal(any());
        then(reviewRepository).should(BDDMockito.atLeast(1)).findByReviewerAndArticle(any(), any());
        then(converter).should(BDDMockito.atLeast(1)).toReviewResponse(any(), any());
    }

    @Test
    @DisplayName(value = "임시 작성")
    void updateOfferScoreSuccessTest() {
        // given
        given(member.evaluateScore(1)).willReturn(1);
        OfferLevel offerLevel = OfferLevel.LEVEL1;
        BDDMockito.doNothing().doNothing().when(member).chageOfferLevel(1);

        // when
        reviewService.updateOfferScore(member, 1);

        // then
        then(member).should(BDDMockito.atLeast(1)).evaluateScore(1);
        then(member).should(BDDMockito.atLeast(0)).chageOfferLevel(1);
    }
}