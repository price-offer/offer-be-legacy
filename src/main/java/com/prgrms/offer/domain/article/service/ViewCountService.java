package com.prgrms.offer.domain.article.service;

import com.prgrms.offer.common.message.ResponseMessage;
import com.prgrms.offer.core.error.exception.BusinessException;
import com.prgrms.offer.domain.article.model.dto.ViewCountResponse;
import com.prgrms.offer.domain.article.model.entity.ViewCount;
import com.prgrms.offer.domain.article.repository.ViewCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ViewCountService {
    private final ViewCountRepository viewCountRepository;

    @Transactional
    public ViewCountResponse addAndGetViewCount(Long articleId) {
        ViewCount viewCount = viewCountRepository.findByArticleId(articleId)
                .orElseThrow(() -> new BusinessException(ResponseMessage.ARTICLE_NOT_FOUND));

        viewCount.addViewCount();

        return new ViewCountResponse(articleId, viewCount.getViewCount());
    }
}
