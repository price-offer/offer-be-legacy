package com.prgrms.offer.domain.message.controller;

import com.prgrms.offer.authentication.presentation.AuthenticationPrincipal;
import com.prgrms.offer.authentication.presentation.LoginMember;
import com.prgrms.offer.authentication.aop.MemberOnly;
import com.prgrms.offer.common.ApiResponse;
import com.prgrms.offer.common.message.ResponseMessage;
import com.prgrms.offer.common.page.PageDto;
import com.prgrms.offer.common.page.PageInfo;
import com.prgrms.offer.domain.member.service.MemberService;
import com.prgrms.offer.domain.message.model.dto.MessageContentResponse;
import com.prgrms.offer.domain.message.model.dto.MessageRequest;
import com.prgrms.offer.domain.message.model.dto.MessageRoomInfoResponse;
import com.prgrms.offer.domain.message.model.dto.MessageRoomResponse;
import com.prgrms.offer.domain.message.model.dto.OutgoingMessageResponse;
import com.prgrms.offer.domain.message.service.MessageService;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/messages")
@RestController
public class MessageController {

    private final MemberService memberService;
    private final MessageService messageService;

    @PostMapping("/member/{memberId}/offerId/{offerId}")
    @MemberOnly
    public ResponseEntity<ApiResponse> sendMessageToOffererOnclickMessageButton(
        @PathVariable Long memberId,
        @RequestParam(value = "articleId") long articleId,
        @PathVariable @Min(1) long offerId,
        @RequestBody @Valid MessageRequest messageRequest,
        @AuthenticationPrincipal LoginMember loginMember) {

        messageService.sendMessageToOffererOnclickMessageButton(memberId, loginMember.getId(),
            articleId,
            offerId,
            messageRequest.getContent());

        return ResponseEntity.ok(ApiResponse.of(ResponseMessage.SUCCESS));
    }

    @GetMapping("/messageBox")
    @MemberOnly
    public ResponseEntity<ApiResponse> getMessageBox(
        @PageableDefault Pageable pageable,
        @AuthenticationPrincipal LoginMember loginMember) {

        Page<MessageRoomResponse> messageRoomResponsePage = messageService.getMessageBox(
            loginMember.getId(), pageable);

        PageInfo pageInfo = getPageInfo(messageRoomResponsePage);

        return ResponseEntity.ok(
            ApiResponse.of(ResponseMessage.SUCCESS,
                PageDto.of(messageRoomResponsePage.getContent(), pageInfo))
        );
    }

    // request param, pathvariable notnull, body not null 체크하기
    @PostMapping("/messageRoom/{messageRoomId}")
    @MemberOnly
    public ResponseEntity<ApiResponse> sendMessage(
        @PathVariable @Min(1) long messageRoomId,
        @RequestBody @Valid MessageRequest messageRequest,
        @AuthenticationPrincipal LoginMember loginMember) {

        OutgoingMessageResponse messageResponse = messageService.sendMessage(messageRoomId,
            messageRequest, loginMember.getId()
        );

        return ResponseEntity.ok(ApiResponse.of(ResponseMessage.SUCCESS, messageResponse));
    }

    // 대화방 단건 조회 : 상대방과의 쪽지 내용 조회
    @GetMapping("/messageRoom/{messageRoomId}/contents")
    @MemberOnly
    public ResponseEntity<ApiResponse> getMessageRoomContents(
        @PathVariable @Min(1) long messageRoomId,
        @PageableDefault Pageable pageable,
        @AuthenticationPrincipal LoginMember loginMember) {

        Page<MessageContentResponse> messageContentResponsePage =
            messageService.getMessageRoomContents(messageRoomId, loginMember.getId(), pageable);

        PageInfo pageInfo = getPageInfo(messageContentResponsePage);

        return ResponseEntity.ok(
            ApiResponse.of(
                ResponseMessage.SUCCESS,
                PageDto.of(
                    messageContentResponsePage.getContent(), pageInfo
                )
            )
        );
    }

    @GetMapping("/messageRoom/{messageRoomId}/messageRoomInfo")
    @MemberOnly
    public ResponseEntity<ApiResponse> getMessageRoomInfo(
        @PathVariable @Min(1) long messageRoomId,
        @AuthenticationPrincipal LoginMember loginMember) {
        MessageRoomInfoResponse messageRoomInfoResponse
            = messageService.getMessageRoomInfo(messageRoomId, loginMember.getId());

        return ResponseEntity.ok(ApiResponse.of(ResponseMessage.SUCCESS, messageRoomInfoResponse));
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
