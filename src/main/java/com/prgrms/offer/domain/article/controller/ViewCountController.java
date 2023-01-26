package com.prgrms.offer.domain.article.controller;

import com.prgrms.offer.domain.article.model.dto.ViewCountResponse;
import com.prgrms.offer.domain.article.service.ViewCountService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/viewCounts", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "게시글 조회수 (ViewCounts)")
public class ViewCountController {

    private final ViewCountService viewCountService;

    @GetMapping(value = "/{articleId}")
    public ResponseEntity<ViewCountResponse> getByArticleId(@PathVariable Long articleId) {
        ViewCountResponse viewCountResponse = viewCountService.addAndGetViewCount(articleId);
        return ResponseEntity.ok(viewCountResponse);
    }

}
