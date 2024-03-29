package com.prgrms.offer.domain.search.service;

import static com.prgrms.offer.common.page.CollectionToPage.toPage;

import com.prgrms.offer.authentication.presentation.LoginMember;
import com.prgrms.offer.domain.article.model.dto.ArticleBriefViewResponse;
import com.prgrms.offer.domain.article.model.entity.Article;
import com.prgrms.offer.domain.article.repository.ArticleRepository;
import com.prgrms.offer.domain.article.repository.LikeArticleRepository;
import com.prgrms.offer.domain.article.service.ArticleConverter;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.member.repository.MemberRepository;
import com.prgrms.offer.domain.search.model.dto.SearchFilterRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ArticleSearchService {

    private final ArticleConverter articleConverter;
    private final ArticleRepository articleRepository;
    private final LikeArticleRepository likeArticleRepository;
    private final MemberRepository memberRepository;

    private final Integer[] tradeStatusCodeArray = {2, 4};

    @Transactional(readOnly = true)
    public Page<ArticleBriefViewResponse> findByTitle(
            String title,
            Pageable pageable,
            LoginMember loginMember) {

        if (loginMember.isAnonymous()) {
            Page<ArticleBriefViewResponse> articleBriefViewResponses = articleRepository.findByTitleIgnoreCaseContainsAndTradeStatusCodeIn(
                title, tradeStatusCodeArray , pageable).map(
                article -> articleConverter.toArticleBriefViewResponse(article, false)
            );
            return articleBriefViewResponses;
        }

        Member currentMember = memberRepository.getById(loginMember.getId());

        Page<ArticleBriefViewResponse> titles = articleRepository.findByTitleIgnoreCaseContainsAndTradeStatusCodeIn(
            title, tradeStatusCodeArray, pageable).map(
            article -> makeBriefViewResponseWithLikeInfo(article, currentMember)
        );
        return titles;
    }

    public Page<ArticleBriefViewResponse> findByFilter(
            SearchFilterRequest searchFilterRequest,
            Pageable pageable,
            LoginMember loginMember) {

        List<Article> articleList = articleRepository.findByOnSaleOrBookedInAndFilter(
            searchFilterRequest, pageable);

        long numContents = articleRepository.countAllByOnSaleOrBookedInAndFilter(searchFilterRequest);

        if (loginMember.isAnonymous()) {
            return (Page<ArticleBriefViewResponse>) toPage(articleList.stream().map(
                article -> articleConverter.toArticleBriefViewResponse(article, false)).collect(
                Collectors.toList()), pageable, numContents);
        }

        Member currentMember = memberRepository.getById(loginMember.getId());

        return (Page<ArticleBriefViewResponse>) toPage(articleList.stream().map(
                article -> makeBriefViewResponseWithLikeInfo(article, currentMember))
            .collect(Collectors.toList()), pageable, numContents);
    }

    private ArticleBriefViewResponse makeBriefViewResponseWithLikeInfo(Article article,
        Member currentMember) {
        boolean isLiked = likeArticleRepository.existsByMemberAndArticle(currentMember, article);

        return articleConverter.toArticleBriefViewResponse(article, isLiked);
    }

}
