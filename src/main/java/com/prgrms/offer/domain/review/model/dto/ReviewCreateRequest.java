package com.prgrms.offer.domain.review.model.dto;

import com.prgrms.offer.common.message.DtoValidationMessage;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewCreateRequest {
    @Min(value = -1, message = DtoValidationMessage.INVALID_REVIEW_SCORE)
    @Max(value = 2, message = DtoValidationMessage.INVALID_REVIEW_SCORE)
    private int score; // TODO: 0값에 대해 예외처리

    @Size(min = 3, max = 4000, message = DtoValidationMessage.INVALID_CONTENT_LENGTH)
    private String content;
}
