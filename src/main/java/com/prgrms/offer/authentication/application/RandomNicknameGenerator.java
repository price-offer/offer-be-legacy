package com.prgrms.offer.authentication.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RandomNicknameGenerator {

    private final List<String> adjectives;
    private final List<String> nouns;

    public RandomNicknameGenerator(@Value("classpath:nickname/nickname_adjectives.txt") Resource adjectivesResource,
                                   @Value("classpath:nickname/nickname_nouns.txt") Resource nounsResource) {
        try {
            this.adjectives = Collections.unmodifiableList(Files.readAllLines(Path.of(adjectivesResource.getURI())));
            this.nouns = Collections.unmodifiableList(Files.readAllLines(Path.of(nounsResource.getURI())));
        } catch (IOException e) {
            log.error("랜덤 닉네임 데이터 파일을 읽는데 실패했습니다. adjectivesResource = {}, nounsResource = {}",
                    adjectivesResource, nounsResource);
            throw new RuntimeException(e);
        }
    }

    public String generateRandomNickname() {
        int adjIndex = ThreadLocalRandom.current().nextInt(0, adjectives.size());
        int nounIndex = ThreadLocalRandom.current().nextInt(0, nouns.size());
        int number = ThreadLocalRandom.current().nextInt(1, 100);
        return String.format("%s %s %d호", adjectives.get(adjIndex), nouns.get(nounIndex), number);
    }
}