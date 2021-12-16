package com.prgrms.offer.domain.offer.repository;

import com.prgrms.offer.domain.article.model.entity.Article;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.offer.model.entity.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    @Override
    <S extends Offer> S save(S entity);

    Long countByOffererIdAndArticleId(Long offererId, Long articleId);

    Page<Offer> findAllByArticleId(Pageable pageable, Long articleId);

    Page<Offer> findAllByOffererAndIsSelected(Pageable pageable, Member offerer, boolean isSelected);

    boolean existsByArticleAndIsSelected(Article article, boolean isSelected);

    Optional<Offer> findByArticleAndIsSelected(Article article, boolean isSelected);

    long countOffersByOfferer(Member member);

    @Query("select distinct o from Offer o where o.offerer = :offerer and o.article.tradeStatusCode = :tradeStatusCode")
    Page<Offer> findAllByOffererAndTradeStatusCode(Member offerer, int tradeStatusCode, Pageable pageable);
}
