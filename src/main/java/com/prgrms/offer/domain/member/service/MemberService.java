package com.prgrms.offer.domain.member.service;

import com.prgrms.offer.common.message.ResponseMessage;
import com.prgrms.offer.common.utils.ImageUploader;
import com.prgrms.offer.core.config.PropertyProvider;
import com.prgrms.offer.core.error.exception.BusinessException;
import com.prgrms.offer.domain.article.model.value.TradeStatus;
import com.prgrms.offer.domain.article.repository.ArticleRepository;
import com.prgrms.offer.domain.article.repository.LikeArticleRepository;
import com.prgrms.offer.domain.member.model.dto.MemberProfile;
import com.prgrms.offer.domain.member.model.dto.MyProfile;
import com.prgrms.offer.domain.member.model.dto.ProfileEdit;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.member.repository.MemberRepository;
import com.prgrms.offer.domain.member.service.response.ActivityResponse;
import com.prgrms.offer.domain.member.service.response.DuplicationResponse;
import com.prgrms.offer.domain.member.service.response.MemberProfileResponse;
import com.prgrms.offer.domain.member.service.response.MyActivityResponse;
import com.prgrms.offer.domain.offer.repository.OfferRepository;
import com.prgrms.offer.domain.review.repository.ReviewRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberConverter memberConverter;
    private final ImageUploader s3ImageUploader;
    private final ArticleRepository articleRepository;
    private final ReviewRepository reviewRepository;
    private final OfferRepository offerRepository;
    private final LikeArticleRepository likeArticleRepository;

    private final PropertyProvider propertyProvider;

    public String getProfileImageUrl(MultipartFile image) throws IOException {
        return s3ImageUploader.upload(image, propertyProvider.getPROFILE_IMG_DIR());
    }

    public void editProfile(Long memberId, ProfileEdit request) {
        Member findMember = memberRepository.getById(memberId);

        findMember.changeNickname(request.getNickname());
        findMember.changeProfileImageUrl(request.getProfileImageUrl());
    }

    @Transactional(readOnly = true)
    public MemberProfileResponse getProfile(Long memberId) {
        Member findMember = memberRepository.getById(memberId);
        return MemberProfileResponse.from(findMember);
    }

    @Transactional(readOnly = true)
    public MemberProfile getOthersProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ResponseMessage.MEMBER_NOT_FOUND));
        long sellingArticleCount = articleRepository.countArticlesByWriter(member);
        long reviewCount = reviewRepository.countReviewsByReviewee(member);

        return memberConverter.toMemberProfile(member, sellingArticleCount, reviewCount);
    }

    @Transactional(readOnly = true)
    public MyProfile getMyProfile(Long memberId) {
        Member member = memberRepository.getById(memberId);

        long sellingArticleCount = articleRepository.countArticlesByWriter(member);
        long reviewCount = reviewRepository.countReviewsByReviewee(member);
        long likeArticleCount = likeArticleRepository.countLikeArticlesByMember(member);
        long offerCount = offerRepository.countOffersByOfferer(member);

        return memberConverter.toMyProfile(member, sellingArticleCount, likeArticleCount, offerCount, reviewCount);
    }

    @Transactional(readOnly = true)
    public MyActivityResponse getMyActivity(Long memberId) {
        Member member = memberRepository.getById(memberId);
        long sellingArticleCount = articleRepository.countByWriterAndTradeStatusCode(member, TradeStatus.ON_SALE.getCode());
        long likeArticleCount = likeArticleRepository.countLikeArticlesByMember(member);
        long soldArticleCount = articleRepository.countByWriterAndTradeStatusCode(member,
                TradeStatus.COMPLETED.getCode());
        long reviewCount = reviewRepository.countReviewsByReviewee(member);
        return MyActivityResponse.of(sellingArticleCount, likeArticleCount, soldArticleCount, reviewCount);
    }

    @Transactional(readOnly = true)
    public ActivityResponse getActivity(Long memberId) {
        Member member = memberRepository.getById(memberId);
        long sellingArticleCount = articleRepository.countByWriterAndTradeStatusCode(member, TradeStatus.ON_SALE.getCode());
        long soldArticleCount = articleRepository.countByWriterAndTradeStatusCode(member,
                TradeStatus.COMPLETED.getCode());
        long reviewCount = reviewRepository.countReviewsByReviewee(member);
        return ActivityResponse.of(sellingArticleCount, soldArticleCount, reviewCount);
    }

    public DuplicationResponse isDuplicateNickname(String nickname) {
        return new DuplicationResponse(memberRepository.existsByNickname(nickname));
    }
}
