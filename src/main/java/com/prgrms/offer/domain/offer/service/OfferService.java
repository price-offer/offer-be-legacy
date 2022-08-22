package com.prgrms.offer.domain.offer.service;

import com.prgrms.offer.authentication.presentation.LoginMember;
import com.prgrms.offer.common.message.ResponseMessage;
import com.prgrms.offer.core.config.PropertyProvider;
import com.prgrms.offer.core.error.exception.BusinessException;
import com.prgrms.offer.domain.article.model.entity.Article;
import com.prgrms.offer.domain.article.repository.ArticleRepository;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.member.repository.MemberRepository;
import com.prgrms.offer.domain.offer.model.dto.OfferBriefResponse;
import com.prgrms.offer.domain.offer.model.dto.OfferCreateRequest;
import com.prgrms.offer.domain.offer.model.dto.OfferResponse;
import com.prgrms.offer.domain.offer.model.entity.Offer;
import com.prgrms.offer.domain.offer.repository.OfferRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;
    private final OfferConverter converter;

    private final PropertyProvider propertyProvider;

    @Transactional
    public OfferResponse offer(OfferCreateRequest request, Long articleId, Long memberId) {
        Member offerer = memberRepository.getById(memberId);

        List<Offer> offers = offerRepository.findAllByOffererIdAndArticleId(offerer.getId(), articleId);
        int offerCountOfCurrentMember = offers.size();

        if (offerCountOfCurrentMember >= propertyProvider.getMAX_AVAIL_OFFER_COUNT()) {
            throw new BusinessException(ResponseMessage.EXCEED_OFFER_COUNT);
        }

        if(offerCountOfCurrentMember == 1){
            Offer prevOffer = offers.get(0);

            if(prevOffer.getPrice().intValue() == request.getPrice()){
                throw new BusinessException(ResponseMessage.ALREADY_EXIST_SAME_PRICE_OFFER);
            }
        }

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BusinessException(ResponseMessage.ARTICLE_NOT_FOUND));

        Offer offer = converter.toEntity(article, offerer, request.getPrice());
        Offer offerEntity = offerRepository.save(offer);

        return converter.toOfferResponse(offerEntity, offerCountOfCurrentMember + 1);
    }

    @Transactional(readOnly = true)
    public Page<OfferBriefResponse> findAllByArticleId(Pageable pageable, Long articleId) {
        Page<Offer> offerPages = offerRepository.findAllByArticleId(pageable, articleId);

        return offerPages.map(o -> converter.toOfferOfferBriefResponse(o));
    }

    @Transactional
    public void adopteOffer(Long offerId, Long memberId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new BusinessException(ResponseMessage.OFFER_NOT_FOUND));

        validateWriterOrElseThrow(offer.getArticle(), memberId);

        if (offerRepository.existsByArticleAndIsSelected(offer.getArticle(), true)) {
            throw new BusinessException(ResponseMessage.EXISTS_ALREADY_SELECTED_OFFER);
        }

        offer.selectOffer();
    }

    private void validateWriterOrElseThrow(Article article, Long memberId) {
        if (!article.validateWriterByPrincipal(memberId)) {
            throw new BusinessException(ResponseMessage.PERMISSION_DENIED);
        }
    }

    @Transactional(readOnly = true)
    public int findOfferCountOfCurrentMember(LoginMember loginMember, Long articleId) {
        if(loginMember.isAnonymous()) {
            return 0;
        }

        Member currentMember = memberRepository.getById(loginMember.getId());

        final int offerCountOfCurrentMember = (int) offerRepository.countByOffererIdAndArticleId(currentMember.getId(), articleId).longValue();

        return offerCountOfCurrentMember;
    }
}
