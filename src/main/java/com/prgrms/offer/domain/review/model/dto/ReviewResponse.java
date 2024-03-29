package com.prgrms.offer.domain.review.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class ReviewResponse {
    private final Long id;

    private final ReviewerDto reviewer;

    private final int score;

    private final ArticleBriefDto article;

    private final String content;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Boolean isWritingAvailableFromCurrentMember;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime createdDate;

    @Getter
    @RequiredArgsConstructor
    public static class ReviewerDto {
        private final Long id;

        private final String profileImageUrl;

        private final String nickname;

        private final int offerLevel;
    }

    @Getter
    @RequiredArgsConstructor
    public static class ArticleBriefDto {
        private final Long id;

        private final String title;
    }
}
