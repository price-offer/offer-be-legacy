package com.prgrms.offer.domain.article.repository;

import com.prgrms.offer.domain.article.model.entity.ViewCount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ViewCountRepository extends JpaRepository<ViewCount, Long>, CustomizedArticleRepository {
    Optional<ViewCount> findByArticleId(Long articleId);
}
