package com.prgrms.offer.domain.review.service;

import com.prgrms.offer.common.message.ResponseMessage;
import com.prgrms.offer.core.error.exception.BusinessException;
import com.prgrms.offer.core.jwt.JwtAuthentication;
import com.prgrms.offer.domain.article.model.entity.Article;
import com.prgrms.offer.domain.article.model.value.TradeStatus;
import com.prgrms.offer.domain.article.repository.ArticleRepository;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.member.repository.MemberRepository;
import com.prgrms.offer.domain.offer.model.entity.Offer;
import com.prgrms.offer.domain.offer.repository.OfferRepository;
import com.prgrms.offer.domain.review.model.dto.ReviewCreateRequest;
import com.prgrms.offer.domain.review.model.dto.ReviewCreateResponse;
import com.prgrms.offer.domain.review.model.dto.ReviewResponse;
import com.prgrms.offer.domain.review.model.entity.Review;
import com.prgrms.offer.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OfferRepository offerRepository;
    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;

    private final ReviewConverter converter;

    private final String BUYER = "buyer";
    private final String SELLER = "seller";

    @Transactional
    public ReviewCreateResponse createReviewToBuyer(Long offerId, Long revieweeId, ReviewCreateRequest request, JwtAuthentication authentication) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new BusinessException(ResponseMessage.OFFER_NOT_FOUND));

        if (!offer.getIsSelected()) {
            throw new BusinessException(ResponseMessage.NOT_SELECTED_OFFER);
        }

        if (!TradeStatus.isCompleted(offer.getArticle().getTradeStatusCode())) {
            throw new BusinessException(ResponseMessage.NOT_COMPLETED_TRADE);
        }

        Member reviewer = memberRepository.findByPrincipal(authentication.loginId)
                .orElseThrow(() -> new BusinessException(ResponseMessage.MEMBER_NOT_FOUND));

        if (reviewer.getId().longValue() == revieweeId.longValue()) {
            throw new BusinessException(ResponseMessage.INVALID_REVIEWEE);
        }

        if (reviewRepository.existsByReviewerAndArticle(reviewer, offer.getArticle())) {
            throw new BusinessException(ResponseMessage.ALREADY_REVIEWED);
        }

        Member reviewee = memberRepository.findById(revieweeId)
                .orElseThrow(() -> new BusinessException(ResponseMessage.MEMBER_NOT_FOUND));

        updateOfferScore(reviewee, request.getScore());

        Review review = converter.toEntity(reviewee, reviewer, offer.getArticle(), request.getScore(), request.getContent(), true);
        Review reviewEntity = reviewRepository.save(review);

        return converter.toReviewCreateResponse(reviewEntity);
    }

    @Transactional
    public ReviewCreateResponse createReviewToSeller(Long articleId, Long revieweeId, ReviewCreateRequest request, JwtAuthentication authentication) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BusinessException(ResponseMessage.ARTICLE_NOT_FOUND));

        if (!article.validateWriterByWriterId(revieweeId)) {
            throw new BusinessException(ResponseMessage.INVALID_REVIEWEE);
        }

        if (!TradeStatus.isCompleted(article.getTradeStatusCode())) {
            throw new BusinessException(ResponseMessage.NOT_COMPLETED_TRADE);
        }

        Member reviewer = memberRepository.findByPrincipal(authentication.loginId)
                .orElseThrow(() -> new BusinessException(ResponseMessage.MEMBER_NOT_FOUND));

        if (reviewer.getId().longValue() == revieweeId.longValue()) {
            throw new BusinessException(ResponseMessage.INVALID_REVIEWEE);
        }

        if (reviewRepository.existsByReviewerAndArticle(reviewer, article)) {
            throw new BusinessException(ResponseMessage.ALREADY_REVIEWED);
        }

        Member reviewee = memberRepository.findById(revieweeId)
                .orElseThrow(() -> new BusinessException(ResponseMessage.MEMBER_NOT_FOUND));

        updateOfferScore(reviewee, request.getScore());
        
        Review review = converter.toEntity(reviewee, reviewer, article, request.getScore(), request.getContent(), false);
        Review reviewEntity = reviewRepository.save(review);

        return converter.toReviewCreateResponse(reviewEntity);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> findAllByRole(Pageable pageable, Long memberId, String role) {
        boolean isRevieweeBuyer = getRevieweeRoleIsBuyerOrElseThrow(role);

        Page<Review> reviewPage = reviewRepository.findAllByRevieweeIdAndIsRevieweeBuyer(pageable, memberId, isRevieweeBuyer);

        return reviewPage.map(r -> converter.toReviewResponse(r));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    void updateOfferScore(Member reviewee, int curScore) {
        //TODO: 오퍼레벨 계산
    }

    private boolean getRevieweeRoleIsBuyerOrElseThrow(String role) {
        if (role.equals(BUYER)) {
            return true;
        } else if (role.equals(SELLER)) {
            return false;
        } else {
            throw new BusinessException(ResponseMessage.INVALID_ROLE);
        }
    }
}
