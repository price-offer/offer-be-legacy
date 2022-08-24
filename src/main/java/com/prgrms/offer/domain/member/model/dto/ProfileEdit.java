package com.prgrms.offer.domain.member.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ProfileEdit {
    @NotBlank
    @Size(max = 20)
    private String nickname;

    @NotBlank
    private String profileImageUrl;

    @Override
    public String toString() {
        return "ProfileEdit{" +
                "nickname='" + nickname + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                '}';
    }
}
