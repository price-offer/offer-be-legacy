package com.prgrms.offer.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.prgrms.offer.authentication.application.RandomNicknameGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RandomNicknameGeneratorTest {

    @Autowired
    RandomNicknameGenerator randomNicknameGenerator;

    @Test
    @DisplayName("랜덤한 닉네임을 생성한다. ex) 섭섭한 냉장고 12호")
    void generateRandomNickname() {
        String randomNickname = randomNicknameGenerator.generateRandomNickname();
        assertAll(
                () -> assertThat(randomNickname).isNotBlank(),
                () -> assertThat(randomNickname).contains("호")
        );
    }
}
