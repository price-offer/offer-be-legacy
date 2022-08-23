package com.prgrms.offer.domain.member.service;

import com.prgrms.offer.common.message.ResponseMessage;
import com.prgrms.offer.common.utils.ImageUploader;
import com.prgrms.offer.core.config.PropertyProvider;
import com.prgrms.offer.core.error.exception.BusinessException;
import com.prgrms.offer.domain.article.repository.ArticleRepository;
import com.prgrms.offer.domain.article.repository.LikeArticleRepository;
import com.prgrms.offer.domain.member.model.dto.MemberProfile;
import com.prgrms.offer.domain.member.model.dto.MemberResponse;
import com.prgrms.offer.domain.member.model.dto.MyProfile;
import com.prgrms.offer.domain.member.model.dto.ProfileEdit;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.member.repository.MemberRepository;
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

    public MemberResponse editProfile(Long memberId, ProfileEdit request) {
        Member findMember = memberRepository.getById(memberId);

        findMember.changeNickname(request.getNickname());
        findMember.changeAddress(request.getAddress());
        findMember.changeProfileImageUrl(request.getProfileImageUrl());

        return memberConverter.toMemberResponse(findMember);
    }

    @Transactional(readOnly = true)
    public MemberResponse getProfile(Long memberId) {
        Member findMember = memberRepository.getById(memberId);
        return memberConverter.toMemberResponse(findMember);
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
}
