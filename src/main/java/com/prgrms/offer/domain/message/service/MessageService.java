package com.prgrms.offer.domain.message.service;

import com.prgrms.offer.common.message.ResponseMessage;
import com.prgrms.offer.core.config.PropertyProvider;
import com.prgrms.offer.core.error.exception.BusinessException;
import com.prgrms.offer.domain.article.model.entity.Article;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.member.repository.MemberRepository;
import com.prgrms.offer.domain.message.model.dto.MessageContentResponse;
import com.prgrms.offer.domain.message.model.dto.MessageRequest;
import com.prgrms.offer.domain.message.model.dto.MessageRoomInfoResponse;
import com.prgrms.offer.domain.message.model.dto.MessageRoomResponse;
import com.prgrms.offer.domain.message.model.dto.OutgoingMessageResponse;
import com.prgrms.offer.domain.message.model.entity.Message;
import com.prgrms.offer.domain.message.model.entity.MessageRoom;
import com.prgrms.offer.domain.message.model.value.MessageRoomType;
import com.prgrms.offer.domain.message.repository.MessageRepository;
import com.prgrms.offer.domain.message.repository.MessageRoomRepository;
import com.prgrms.offer.domain.offer.model.entity.Offer;
import com.prgrms.offer.domain.offer.repository.OfferRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageRoomRepository messageRoomRepository;
    private final MemberRepository memberRepository;
    //private final ArticleRepository articleRepository;
    private final MessageConverter messageConverter;
    private final MessageRoomConverter messageRoomConverter;
    private final OfferRepository offerRepository;

    private final PropertyProvider propertyProvider;

    // 이미 보낸적 있는 사람에겐 채팅방 생성하지 않고 기존 채팅방 사용하기
    @Transactional
    public void sendMessageToOffererOnclickMessageButton(long receiverId, Long senderId,
                                                         long offerId,
                                                         String content) {

        Member receiver = memberRepository.findById(receiverId)
            .orElseThrow(() -> new BusinessException(ResponseMessage.MEMBER_NOT_FOUND));

        Member sender = memberRepository.getById(senderId);

        Offer offer = offerRepository.findById(offerId)
            .orElseThrow(() -> new BusinessException(ResponseMessage.OFFER_NOT_FOUND));

//        TODO : messageRoom is_deleted=true 일때의 시나리오
//        Boolean isMyMessageRoomExists = true;
//        Boolean isOffererMessageRoomExits = true;


        // 쪽지버튼 눌러서 보낼때(최초 전송 이후)
        // messageRoom 찾아오는 쿼리메소드 간소화하기

        MessageRoom myMessageRoom = messageRoomRepository.findByMemberAndPartnerAndOffer(
            sender, receiver, offer).orElseGet(
            () -> createMessageRoom(sender, receiver, offer)
        );

        MessageRoom offererMessageRoom = messageRoomRepository.findByMemberAndPartnerAndOffer(
            receiver, sender, offer).orElseGet(
            () -> createMessageRoom(receiver, sender, offer)
        );

        Message myMessage = messageConverter.createMessage(true, content,
            myMessageRoom);
        Message offererMessage = messageConverter.createMessage(false, content,
            offererMessageRoom);

        messageRepository.save(myMessage);
        messageRepository.save(offererMessage);
    }

    // 쪽지함 가져오기
    @Transactional(readOnly = true)
    public Page<MessageRoomResponse> getMessageBox(Long memberId, Pageable pageable) {
        Member me = memberRepository.getById(memberId);

        List<MessageRoom> messageRoomList = messageRoomRepository.findByMemberId(me.getId(), pageable);

        List<Message> messageList = messageRoomList.stream().map(
            messageRoom -> messageRepository.findTop1ByMessageRoomOrderByCreatedDateDesc(
                messageRoom)).collect(Collectors.toList());

        List<String> messageTypeList = messageRoomList.stream().map(messageRoom -> (MessageRoomType.of(memberId.equals(messageRoom.getOffer().getOfferer().getId())))).collect(Collectors.toList());

        long numMessageRoom = messageRoomRepository.countMessageRoomByMember(me);

        Page<MessageRoomResponse> messageRoomResponsePage =
            messageRoomConverter.toMessageRoomResponsePage(messageRoomList, messageList, messageTypeList, numMessageRoom, pageable);

        return messageRoomResponsePage;
    }

    // 대화방에서 메시지 전송
    @Transactional
    public OutgoingMessageResponse sendMessage(
            long messageRoomId,
            MessageRequest messageRequest,
            Long memberId) {
        // 내가 대화방을 나간 상황
        MessageRoom myMessageRoom = messageRoomRepository.findById(messageRoomId)
            .orElseThrow(() -> new BusinessException(ResponseMessage.EXITED_MESSAGE_ROOM));

        isAuthenticatedUser(memberId, myMessageRoom);

        // 상대방이 대화방을 나간 상황
        Member messagePartner = myMessageRoom.getPartner();

        if (messagePartner == null) {
            throw new BusinessException(ResponseMessage.MEMBER_NOT_FOUND);
        }

        Member me = myMessageRoom.getMember();

        MessageRoom receiverMessageRoom = messageRoomRepository.findByMemberAndPartnerAndOffer(
                messagePartner, me, myMessageRoom.getOffer())
            .orElseThrow(
                () -> new BusinessException(ResponseMessage.MESSAGE_PARTNER_EXITED_MESSAGE_ROOM));

//        receiverMessageRoom으로 receiver 찾기 vs myMessageRoom에서 messagePartner 찾기
//        Member receiver = memberRepository.findById(receiverMessageRoom.getMember().getId())
//            .orElseThrow(() -> new BusinessException(ResponseMessage.MEMBER_NOT_FOUND));

        String content = messageRequest.getContent();

        Message myMessage = messageConverter.createMessage(true, content,
            myMessageRoom);
        Message receiverMessage = messageConverter.createMessage(false, content,
            receiverMessageRoom);

        messageRepository.save(myMessage);
        messageRepository.save(receiverMessage);

        return messageConverter.toOutgoingMessageResponse(myMessage.getContent(),
            myMessage.getCreatedDate());

    }

    @Transactional(readOnly = true)
    public Page<MessageContentResponse> getMessageRoomContents(long messageRoomId, Long memberId,
                                                               Pageable pageable) {

        MessageRoom myMessageRoom = messageRoomRepository.findById(messageRoomId)
            .orElseThrow(() -> new BusinessException(ResponseMessage.MESSAGE_ROOM_NOT_FOUND));

        isAuthenticatedUser(memberId, myMessageRoom);

        Page<Message> messageContentPage = messageRepository.findByMessageRoomOrderByMessageIdAsc(
            myMessageRoom, pageable);

        long numMessage = messageRepository.countByMessageRoomAndIsSendMessage(myMessageRoom, false);
        myMessageRoom.setNumReadMessage(numMessage);

        return messageContentPage.map(message -> messageConverter.toMessageContentResponsePage(message));
    }

    @Transactional(readOnly = true)
    public MessageRoomInfoResponse getMessageRoomInfo(long messageRoomId, Long memberId) {

        MessageRoom myMessageRoom = messageRoomRepository.findById(messageRoomId)
            .orElseThrow(() -> new BusinessException(ResponseMessage.MESSAGE_ROOM_NOT_FOUND));

        isAuthenticatedUser(memberId, myMessageRoom);

        Member messagePartner = myMessageRoom.getPartner();

        Offer offer = myMessageRoom.getOffer();
        Article article = offer.getArticle();

        long numMessageContent = messageRepository.countAllByMessageRoom(myMessageRoom);
        long numReceivedMessageContent = messageRepository.countByMessageRoomAndIsSendMessage(myMessageRoom, false);
        long numNotReadMessage = numReceivedMessageContent - myMessageRoom.getNumReadMessage();

        long lastPageOfMessageContents = (long) Math.ceil(
            numMessageContent / propertyProvider.getREQURIED_CONTENTS_SIZE());

        return messageConverter.toMessageRoomInfoResponse(messagePartner, article, offer,
            lastPageOfMessageContents, numNotReadMessage);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public MessageRoom createMessageRoom(Member member1, Member member2, Offer offer) {
        return messageRoomRepository.save(new MessageRoom(member1, member2, offer));
    }

    private Member isAuthenticatedUser(Long memberId, MessageRoom myMessageRoom) {
        Member me = memberRepository.getById(memberId);

        // 다른 멤버의 대화방에 접근한 경우
        if (me.getId() != myMessageRoom.getMember().getId()) {
            throw new BusinessException(ResponseMessage.PERMISSION_DENIED);
        }

        return me;
    }

}
