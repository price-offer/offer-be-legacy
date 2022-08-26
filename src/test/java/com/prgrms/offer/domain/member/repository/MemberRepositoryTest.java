package com.prgrms.offer.domain.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.offer.core.config.JpaConfig;
import com.prgrms.offer.core.config.QueryDSLConfig;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.member.model.entity.Member.OAuthType;
import java.time.LocalDateTime;
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
        Member member = memberRepository.save(Member.builder()
                .oauthId(1L)
                .oauthType(OAuthType.KAKAO)
                .nickname("행복한 냉장고 3호")
                .build());

        LocalDateTime afterSave = LocalDateTime.now();
        assertThat(member.getCreatedDate()).isBeforeOrEqualTo(afterSave);
    }

    @Test
    @DisplayName("입력받은 닉네임이 이미 존재하면 참을 반환한다")
    void returnTrueIfExistNickname() {
        String nickname = "행복한 냉장고 3호";
        memberRepository.save(Member.builder()
                .oauthId(1L)
                .oauthType(OAuthType.KAKAO)
                .nickname(nickname)
                .build());

        boolean isDuplicate = memberRepository.existsByNickname(nickname);

        assertThat(isDuplicate).isTrue();
    }

    @Test
    @DisplayName("입력받은 닉네임이 이미 존재하면 참을 반환한다")
    void returnFalseIfNonExistNickname() {
        String nickname = "행복한 냉장고 3호";

        boolean isDuplicate = memberRepository.existsByNickname(nickname);

        assertThat(isDuplicate).isFalse();
    }
}
