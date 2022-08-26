package com.prgrms.offer.domain.member.service.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DuplicationResponse {

    private final boolean isDuplicate;

}
