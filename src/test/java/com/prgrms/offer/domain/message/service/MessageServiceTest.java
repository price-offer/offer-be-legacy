package com.prgrms.offer.domain.message.service;

import com.prgrms.offer.domain.article.model.entity.Article;
import com.prgrms.offer.domain.article.repository.ArticleRepository;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.member.repository.MemberRepository;
import com.prgrms.offer.domain.message.model.entity.MessageRoom;
import com.prgrms.offer.domain.message.repository.MessageRepository;
import com.prgrms.offer.domain.message.repository.MessageRoomRepository;
import com.prgrms.offer.domain.offer.model.entity.Offer;
import com.prgrms.offer.domain.offer.repository.OfferRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
class MessageServiceTest {

    private final MessageRepository messageRepository;
    private final MessageRoomRepository messageRoomRepository;
    private final MemberRepository memberRepository;
    private final OfferRepository offerRepository;
    private final ArticleRepository articleRepository;

    MessageServiceTest(
            @Autowired MessageRepository messageRepository,
            @Autowired MessageRoomRepository messageRoomRepository,
            @Autowired MemberRepository memberRepository,
            @Autowired OfferRepository offerRepository,
            @Autowired ArticleRepository articleRepository) {
        this.messageRepository = messageRepository;
        this.messageRoomRepository = messageRoomRepository;
        this.memberRepository = memberRepository;
        this.offerRepository = offerRepository;
        this.articleRepository = articleRepository;
    }

    @Test
    void createMessageRoom() {
        Member me = memberRepository.save(
                Member.builder()
                        .nickname("user1")
                        .oauthId(1L)
                        .oauthType(Member.OAuthType.KAKAO)
                        .offerLevel(1)
                        .profileImageUrl("")
                        .score(1)
                        .createdDate(LocalDateTime.now())
                        .build()
        );

        Member partner = memberRepository.save(
                Member.builder()
                        .nickname("user2")
                        .oauthId(1L)
                        .oauthType(Member.OAuthType.KAKAO)
                        .offerLevel(1)
                        .profileImageUrl("")
                        .score(1)
                        .createdDate(LocalDateTime.now())
                        .build()
        );

        Article article = articleRepository.save(
                Article.builder(me, "당근", "팝니다", 1000)
                        .build()
        );


        Offer offer = offerRepository.save(
                Offer.builder()
                        .isSelected(false)
                        .price(1000)
                        .article(article)
                        .offerer(partner)
                        .build()
        );

        MessageRoom messageRoom = messageRoomRepository.save(
                MessageRoom.builder()
                        .member(me)
                        .offer(offer)
                        .partner(partner)
                        .build());

        assertThat(messageRoomRepository.findById(messageRoom.getId()).get()).isEqualTo(messageRoom);
    }


}