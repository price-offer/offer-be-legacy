package com.prgrms.offer.domain.message.service;

import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.message.model.dto.MessageRoomResponse;
import com.prgrms.offer.domain.message.model.dto.MessageRoomResponse.MessageInfo;
import com.prgrms.offer.domain.message.model.dto.MessageRoomResponse.UserInfo;
import com.prgrms.offer.domain.message.model.entity.Message;
import com.prgrms.offer.domain.message.model.entity.MessageRoom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class MessageRoomConverter {

    private final MessageRoomResponseComparator messageRoomComparator = new MessageRoomResponseComparator();

    public MessageRoomResponse toMessageRoomResponse(MessageRoom messageRoom, Message message, String messageRoomType) {
        Member messagePartner = messageRoom.getPartner();
        UserInfo userInfo = messagePartner != null ? UserInfo.createUserInfo(messagePartner)
            : UserInfo.createNullUserInfo();

        return MessageRoomResponse.builder()
            .userInfo(userInfo)
            .productImageUrl(messageRoom.getOffer().getArticle().getMainImageUrl())
            .message(new MessageInfo(message.getContent(), message.getCreatedDate()))
            .messageRoomType(messageRoomType)
            .messageRoomId(messageRoom.getId())
            .build();
    }

    public Page<MessageRoomResponse> toMessageRoomResponsePage(
        List<MessageRoom> messageRoomList,
        List<Message> messageList,
        List<String> messageTypeList,
        long numMessageRoom,
        Pageable pageable) {

        List<MessageRoomResponse> messageRoomResponseList = new ArrayList<>();

        Iterator<MessageRoom> messageRoomIterator = messageRoomList.listIterator();
        Iterator<Message> messageIterator = messageList.listIterator();
        Iterator<String> messageTypeIterator = messageTypeList.listIterator();

        while (messageRoomIterator.hasNext()) {
            messageRoomResponseList.add(
                toMessageRoomResponse(messageRoomIterator.next(), messageIterator.next(), messageTypeIterator.next())
            );
        }

        messageRoomResponseList.sort(messageRoomComparator);

        final Page<MessageRoomResponse> page = new PageImpl<>(
            messageRoomResponseList, pageable, numMessageRoom);

        return page;
    }

    class MessageRoomResponseComparator implements Comparator<MessageRoomResponse> {

        // 최신 쪽지 내역이 있는 messageRoom 내림차순 정렬
        @Override
        public int compare(MessageRoomResponse o1, MessageRoomResponse o2) {
            return o1.getMessage().getCreatedDate().isAfter(o2.getMessage().getCreatedDate()) ? -1
                : 1;
        }
    }

}
