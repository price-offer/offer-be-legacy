package com.prgrms.offer.domain.message.repository;

import com.prgrms.offer.domain.article.model.entity.Article;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.message.model.entity.MessageRoom;
import com.prgrms.offer.domain.offer.model.entity.Offer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface MessageRoomRepository extends Repository<MessageRoom, Long> {

    List<MessageRoom> findByMemberId(Long userId, Pageable pageable);

    MessageRoom save(MessageRoom messageRoom);

    Optional<MessageRoom> findByMemberAndPartnerAndOffer(Member me, Member partner, Offer offer);

    Optional<MessageRoom> findById(long messageRoomId);

    Long countMessageRoomByMember(Member me);

//    @Modifying(clearAutomatically = true)
//    @Query("UPDATE MessageRoom mr SET mr.offer = NULL WHERE mr.offer = :offer")
//    void doOnDeleteSetNullFromArticle(Article article);
}
