package com.prgrms.offer.domain.member.repository;

import com.prgrms.offer.core.config.JpaConfig;
import com.prgrms.offer.core.config.QueryDSLConfig;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.member.model.entity.Member.OAuthType;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({JpaConfig.class, QueryDSLConfig.class})
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("엔티티 저장 시 생성 시간도 함께 저장한다")
    void saveCreatedDate() {
        Member member1 = Member.builder()
                .oauthId(1L)
                .oauthType(OAuthType.KAKAO)
                .nickname("행복한 냉장고 3호")
                .build();
        Member member = memberRepository.save(member1);

        LocalDateTime afterSave = LocalDateTime.now();
        Assertions.assertThat(member.getCreatedDate()).isBeforeOrEqualTo(afterSave);
    }
}
