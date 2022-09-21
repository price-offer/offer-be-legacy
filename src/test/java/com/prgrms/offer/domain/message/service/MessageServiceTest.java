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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class MessageServiceTest {

    private final MessageService messageService;
    private final MessageRepository messageRepository;
    private final MessageRoomRepository messageRoomRepository;
    private final MemberRepository memberRepository;
    private final OfferRepository offerRepository;
    private final ArticleRepository articleRepository;


    MessageServiceTest(
            @Autowired MessageService messageService,
            @Autowired MessageRepository messageRepository,
            @Autowired MessageRoomRepository messageRoomRepository,
            @Autowired MemberRepository memberRepository,
            @Autowired OfferRepository offerRepository,
            @Autowired ArticleRepository articleRepository) {
        this.messageService = messageService;
        this.messageRepository = messageRepository;
        this.messageRoomRepository = messageRoomRepository;
        this.memberRepository = memberRepository;
        this.offerRepository = offerRepository;
        this.articleRepository = articleRepository;
    }

    private Member me;
    private Member partner;
    private Article article;
    private Offer offer;

    @BeforeAll
    void setTestData() {
        me = memberRepository.save(
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

        partner = memberRepository.save(
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

        article = articleRepository.save(
                Article.builder(me, "당근", "팝니다", 1000)
                        .build()
        );


        offer = offerRepository.save(
                Offer.builder()
                        .isSelected(false)
                        .price(1000)
                        .article(article)
                        .offerer(partner)
                        .build()
        );
    }

    @Test
    void createMessageRoom() {
        MessageRoom messageRoom = messageRoomRepository.save(
                MessageRoom.builder()
                        .member(me)
                        .offer(offer)
                        .partner(partner)
                        .build());

        assertThat(messageRoomRepository.findById(messageRoom.getId()).get()).isEqualTo(messageRoom);
    }

    @Test
    void sendMessageToOffererOnclickMessageButton() {
        messageService.sendMessageToOffererOnclickMessageButton(
                me.getId(), partner.getId(), offer.getId(), "판매자에게 거래 제안 메시지를 전송한다.");

        MessageRoom offererMessageRoom = messageRoomRepository.findByMemberAndPartnerAndOffer(
                partner, me, offer).orElse(messageService.createMessageRoom(partner, me, offer)
        );

        MessageRoom myMessageRoom = messageRoomRepository.findByMemberAndPartnerAndOffer(
                me, partner, offer).orElse(messageService.createMessageRoom(me, partner, offer)
        );

        assertThat(offererMessageRoom.getMember()).isEqualTo(partner);
        assertThat(myMessageRoom.getMember()).isEqualTo(me);

    }


}