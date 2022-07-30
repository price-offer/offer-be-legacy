package com.prgrms.offer.domain.article.service;

import com.prgrms.offer.authentication.presentation.LoginMember;
import com.prgrms.offer.common.message.ResponseMessage;
import com.prgrms.offer.core.error.exception.BusinessException;
import com.prgrms.offer.domain.article.model.dto.LikeArticleStatusResponse;
import com.prgrms.offer.domain.article.model.entity.Article;
import com.prgrms.offer.domain.article.model.entity.LikeArticle;
import com.prgrms.offer.domain.article.repository.ArticleRepository;
import com.prgrms.offer.domain.article.repository.LikeArticleRepository;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class LikeArticleService {

    private final LikeArticleRepository likeArticleRepository;
    private final LikeArticleConverter converter;
    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public LikeArticleStatusResponse switchLikeStatus(Long articleId, LoginMember loginMember){
        Member member = memberRepository.getById(loginMember.getId());

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BusinessException(ResponseMessage.ARTICLE_NOT_FOUND));

        if(likeArticleRepository.existsByMemberAndArticle(member, article)){
            likeArticleRepository.deleteByMemberIdAndArticleId(member.getId(), articleId);

            article.subtractLikeCount();

            return converter.toLikeArticleStatusResponse(false);
        }

        LikeArticle likeArticle = new LikeArticle(member, article);
        likeArticleRepository.save(likeArticle);

        article.addLikeCount();

        return converter.toLikeArticleStatusResponse(true);
    }
}
