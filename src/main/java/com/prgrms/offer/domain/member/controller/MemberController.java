package com.prgrms.offer.domain.member.controller;

import com.prgrms.offer.authentication.presentation.AuthenticationPrincipal;
import com.prgrms.offer.common.ApiResponse;
import com.prgrms.offer.common.message.ResponseMessage;
import com.prgrms.offer.core.error.exception.BusinessException;
import com.prgrms.offer.domain.member.model.dto.MemberProfile;
import com.prgrms.offer.domain.member.model.dto.MemberResponse;
import com.prgrms.offer.domain.member.model.dto.MyProfile;
import com.prgrms.offer.domain.member.model.dto.ProfileEdit;
import com.prgrms.offer.domain.member.service.MemberService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/members/imageUrls")
    public ResponseEntity<ApiResponse> convertToImageUrl(@ModelAttribute MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new BusinessException(ResponseMessage.INVALID_IMAGE_EXCEPTION);
        }

        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", memberService.getProfileImageUrl(image));
        return ResponseEntity.ok(ApiResponse.of(ResponseMessage.SUCCESS, response));
    }

    @PatchMapping("/members/me")
    public ResponseEntity<ApiResponse> editProfile(
            @AuthenticationPrincipal Long memberId,
            @RequestBody @Valid ProfileEdit request) {
        MemberResponse response = memberService.editProfile(memberId, request);
        return ResponseEntity.ok(ApiResponse.of(ResponseMessage.SUCCESS, response));
    }

    @GetMapping("/members/me")
    public ResponseEntity<ApiResponse> getProfile(@AuthenticationPrincipal Long memberId) {
        MemberResponse response = memberService.getProfile(memberId);
        return ResponseEntity.ok(ApiResponse.of(ResponseMessage.SUCCESS, response));
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<ApiResponse> getOthersProfile(@PathVariable Long memberId) {
        MemberProfile response = memberService.getOthersProfile(memberId);
        return ResponseEntity.ok(ApiResponse.of(ResponseMessage.SUCCESS, response));
    }

    @GetMapping("/members/mypage")
    public ResponseEntity<ApiResponse> getMyProfile(@AuthenticationPrincipal Long memberId) {
        MyProfile response = memberService.getMyProfile(memberId);
        return ResponseEntity.ok(ApiResponse.of(ResponseMessage.SUCCESS, response));
    }
}
