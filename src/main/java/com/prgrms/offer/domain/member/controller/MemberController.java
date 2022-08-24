package com.prgrms.offer.domain.member.controller;

import com.prgrms.offer.authentication.aop.MemberOnly;
import com.prgrms.offer.authentication.presentation.AuthenticationPrincipal;
import com.prgrms.offer.authentication.presentation.LoginMember;
import com.prgrms.offer.common.ApiResponse;
import com.prgrms.offer.common.message.ResponseMessage;
import com.prgrms.offer.core.error.exception.BusinessException;
import com.prgrms.offer.domain.member.model.dto.MemberResponse;
import com.prgrms.offer.domain.member.model.dto.ProfileEdit;
import com.prgrms.offer.domain.member.service.MemberService;
import com.prgrms.offer.domain.member.service.response.MemberProfileResponse;
import com.prgrms.offer.domain.member.service.response.MyActivityResponse;
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
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/imageUrls")
    public ResponseEntity<ApiResponse> convertToImageUrl(@ModelAttribute MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new BusinessException(ResponseMessage.INVALID_IMAGE_EXCEPTION);
        }

        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", memberService.getProfileImageUrl(image));
        return ResponseEntity.ok(ApiResponse.of(ResponseMessage.SUCCESS, response));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse> editProfile(
            @AuthenticationPrincipal Long memberId,
            @RequestBody @Valid ProfileEdit request) {
        MemberResponse response = memberService.editProfile(memberId, request);
        return ResponseEntity.ok(ApiResponse.of(ResponseMessage.SUCCESS, response));
    }

    @GetMapping("/me")
    @MemberOnly
    public ResponseEntity<MemberProfileResponse> getProfileOfMine(@AuthenticationPrincipal LoginMember loginMember) {
        MemberProfileResponse response = memberService.getProfile(loginMember.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberProfileResponse> getProfile(@PathVariable Long memberId) {
        MemberProfileResponse response = memberService.getProfile(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/activity")
    @MemberOnly
    public ResponseEntity<MyActivityResponse> getMyProfile(@AuthenticationPrincipal Long memberId) {
        MyActivityResponse response = memberService.getMyActivity(memberId);
        return ResponseEntity.ok(response);
    }
}
