package com.prgrms.offer.domain.message.model.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.prgrms.offer.domain.member.model.entity.Member;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MessageRoomResponse {

    private UserInfo userInfo;

    private String productImageUrl;

    private MessageInfo message;

    private Long messageRoomId;

    private String messageRoomType;

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class UserInfo {

        private String nickName;
        private String profileImageUrl;

        private UserInfo(String userNickName, String userProfileImageUrl) {
            this.nickName = userNickName;
            this.profileImageUrl = userProfileImageUrl;
        }

        public static UserInfo createNullUserInfo() {
            return new UserInfo(null, null);
        }

        public static UserInfo createUserInfo(Member member){
            return new UserInfo(member.getNickname(), member.getProfileImageUrl());
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class MessageInfo {

        private String content;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdDate;

    }

}
