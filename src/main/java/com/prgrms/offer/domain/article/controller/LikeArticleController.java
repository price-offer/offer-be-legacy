package com.prgrms.offer.domain.article.controller;

import com.prgrms.offer.authentication.aop.MemberOnly;
import com.prgrms.offer.authentication.presentation.AuthenticationPrincipal;
import com.prgrms.offer.authentication.presentation.LoginMember;
import com.prgrms.offer.common.ApiResponse;
import com.prgrms.offer.common.message.ResponseMessage;
import com.prgrms.offer.domain.article.model.dto.LikeArticleStatusResponse;
import com.prgrms.offer.domain.article.service.LikeArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/articles", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "게시글 좋아요(찜하기)")
public class LikeArticleController {

    private final LikeArticleService likeArticleService;

    @ApiOperation("게시글 좋아요 상태 변경")
    @PutMapping(value = "/{articleId}/like")
    @MemberOnly
    public ResponseEntity<ApiResponse> switchLikeArticleStatus(
            @PathVariable Long articleId,
            @AuthenticationPrincipal LoginMember loginMember
    ){

        LikeArticleStatusResponse response = likeArticleService.switchLikeStatus(articleId, loginMember);

        return ResponseEntity.ok(
                ApiResponse.of(ResponseMessage.SUCCESS, response)
        );
    }
}
