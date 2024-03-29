package com.prgrms.offer.domain.article.controller;

import com.prgrms.offer.authentication.aop.MemberOnly;
import com.prgrms.offer.authentication.presentation.AuthenticationPrincipal;
import com.prgrms.offer.authentication.presentation.LoginMember;
import com.prgrms.offer.common.ApiResponse;
import com.prgrms.offer.common.message.ResponseMessage;
import com.prgrms.offer.common.page.PageDto;
import com.prgrms.offer.common.page.PageInfo;
import com.prgrms.offer.core.error.exception.BusinessException;
import com.prgrms.offer.domain.article.model.dto.ArticleBriefViewResponse;
import com.prgrms.offer.domain.article.model.dto.ArticleCreateOrUpdateRequest;
import com.prgrms.offer.domain.article.model.dto.ArticleCreateOrUpdateResponse;
import com.prgrms.offer.domain.article.model.dto.CodeAndNameInfosResponse;
import com.prgrms.offer.domain.article.model.dto.ProductImageUrlsResponse;
import com.prgrms.offer.domain.article.model.dto.TradeStatusUpdateRequest;
import com.prgrms.offer.domain.article.service.ArticleService;
import com.prgrms.offer.domain.article.service.response.ArticleResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/articles", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "게시글 (Article)")
public class ArticleController {

    private final ArticleService articleService;

    @ApiOperation("이미지 -> URL 변환")
    @PostMapping(value = "/imageUrls", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> convertToImageUrls(@ModelAttribute List<MultipartFile> images)
            throws IOException {

        if (images == null || images.isEmpty()) {
            throw new BusinessException(ResponseMessage.INVALID_IMAGE_EXCEPTION);
        }

        List<String> imageUrls = articleService.uploadImage(images);

        Map response = new HashMap<String, List<String>>();
        response.put("imageUrls", imageUrls);

        return ResponseEntity.ok(
                ApiResponse.of(ResponseMessage.SUCCESS, response)
        );
    }

    @ApiOperation("게시글 등록/수정")
    @PutMapping
    @MemberOnly
    public ResponseEntity<ApiResponse> putArticle(
            @Valid @RequestBody ArticleCreateOrUpdateRequest request,
            @AuthenticationPrincipal LoginMember loginMember
    ) {
        ArticleCreateOrUpdateResponse response = articleService.createOrUpdate(request, loginMember.getId());

        return ResponseEntity.ok(
                ApiResponse.of(ResponseMessage.SUCCESS, response)
        );
    }

    @ApiOperation("판매 상태 변경")
    @MemberOnly
    @PatchMapping(value = "/{articleId}/tradeStatus", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> updateTradeStatus(
            @PathVariable Long articleId,
            @Valid @RequestBody TradeStatusUpdateRequest request,
            @AuthenticationPrincipal Long memberId
    ) {
        articleService.updateTradeStatus(articleId, request.getCode(), memberId);

        return ResponseEntity.ok(
                ApiResponse.of(ResponseMessage.SUCCESS)
        );
    }

    @ApiOperation("게시글 전체 조회")
    @GetMapping()
    public ResponseEntity<ApiResponse> getAll(
            @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC, size = 20) Pageable pageable,
            @RequestParam(value = "categoryCode", required = false) Integer categoryCode,
            @RequestParam(value = "memberId", required = false) Long memberId,
            @RequestParam(value = "tradeStatusCode", required = false) Integer tradeStatusCode,
            @AuthenticationPrincipal LoginMember loginMember
    ) {

        Page<ArticleBriefViewResponse> pageResponses = articleService.findAllByPages(
                pageable,
                Optional.ofNullable(categoryCode),
                Optional.ofNullable(memberId),
                Optional.ofNullable(tradeStatusCode),
                loginMember
        );

        PageInfo pageInfo = getPageInfo(pageResponses);

        return ResponseEntity.ok(
                ApiResponse.of(ResponseMessage.SUCCESS, PageDto.of(pageResponses.getContent(), pageInfo))
        );
    }

    @ApiOperation("마이페이지에서 내가 구매한 모든 게시글 조회")
    @GetMapping(value = "/buy")
    @MemberOnly
    public ResponseEntity<ApiResponse> getAllInMyPage(
            @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC, size = 20) Pageable pageable,
            @AuthenticationPrincipal LoginMember loginMember
    ) {
        Page<ArticleBriefViewResponse> pageResponses = articleService.findAllBoughtProducts(pageable, loginMember);

        PageInfo pageInfo = getPageInfo(pageResponses);

        return ResponseEntity.ok(
                ApiResponse.of(ResponseMessage.SUCCESS, PageDto.of(pageResponses.getContent(), pageInfo))
        );
    }

    @GetMapping(value = "/{articleId}")
    public ResponseEntity<ArticleResponse> showArticle(@PathVariable Long articleId,
                                                       @AuthenticationPrincipal LoginMember loginMember) {
        ArticleResponse response = articleService.findArticle(articleId, loginMember);
        return ResponseEntity.ok(response);
    }

    @ApiOperation("마이페이지에서 내가 제안한 모든 게시글 조회")
    @GetMapping(value = "/offers")
    @MemberOnly
    public ResponseEntity<ApiResponse> getAllByOffersInMyPage(
            @RequestParam(value = "tradeStatusCode", required = true) int tradeStatusCode,
            @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC, size = 20) Pageable pageable,
            @AuthenticationPrincipal LoginMember loginMember
    ) {
        Page<ArticleBriefViewResponse> pageResponses = articleService.findAllByMyOffers(pageable, tradeStatusCode,
                loginMember);

        PageInfo pageInfo = getPageInfo(pageResponses);

        return ResponseEntity.ok(
                ApiResponse.of(ResponseMessage.SUCCESS, PageDto.of(pageResponses.getContent(), pageInfo))
        );
    }

    @ApiOperation("단건 게시글의 이미지 전체 조회")
    @GetMapping(value = "/{articleId}/imageUrls")
    public ResponseEntity<ApiResponse> getAllImageUrls(@PathVariable Long articleId) {
        ProductImageUrlsResponse response = articleService.findAllImageUrls(articleId);

        return ResponseEntity.ok(
                ApiResponse.of(ResponseMessage.SUCCESS, response)
        );
    }

    @ApiOperation("카테고리, 상품상태, 거래방법, 거래상태 목록 조회")
    @GetMapping(value = "/infos")
    public ResponseEntity<ApiResponse> getAllCodeAndNameInfos() {
        CodeAndNameInfosResponse response = articleService.getAllCodeAndNameInfos();

        return ResponseEntity.ok(
                ApiResponse.of(ResponseMessage.SUCCESS, response)
        );
    }

    @ApiOperation("게시글 삭제")
    @DeleteMapping(value = "/{articleId}")
    @MemberOnly
    public ResponseEntity<ApiResponse> deleteOne(
            @PathVariable Long articleId,
            @AuthenticationPrincipal LoginMember loginMember
    ) {
        articleService.deleteOne(articleId, loginMember);

        return ResponseEntity.ok(
                ApiResponse.of(ResponseMessage.SUCCESS)
        );
    }

    @GetMapping("/like-articles")
    @MemberOnly
    public ResponseEntity<ApiResponse> getLikeArticles(
            @RequestParam Integer tradeStatusCode,
            @PageableDefault(sort = "created_date", direction = Sort.Direction.DESC, size = 20) Pageable pageable,
            @AuthenticationPrincipal LoginMember loginMember) {

        Page<ArticleBriefViewResponse> responses = articleService.getLikeArticlesWithTradeStatusCode(pageable,
                loginMember, tradeStatusCode);
        PageInfo pageInfo = getPageInfo(responses);
        return ResponseEntity.ok(
                ApiResponse.of(ResponseMessage.SUCCESS, PageDto.of(responses.getContent(), pageInfo))
        );
    }

    private PageInfo getPageInfo(Page<?> pageResponses) {
        return PageInfo.of(
                pageResponses.getPageable().getPageNumber(),
                pageResponses.getTotalPages(),
                pageResponses.getPageable().getPageSize(),
                pageResponses.getTotalElements(),
                pageResponses.isLast(),
                pageResponses.isFirst()
        );
    }

}
