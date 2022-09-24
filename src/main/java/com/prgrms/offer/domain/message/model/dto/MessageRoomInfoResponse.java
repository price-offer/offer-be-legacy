package com.prgrms.offer.domain.message.model.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.prgrms.offer.domain.article.model.entity.Article;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.offer.model.entity.Offer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageRoomInfoResponse  {

    private ArticleInfo articleInfo;
    private MessagePartnerInfo messagePartnerInfo;
    private OfferInfo offerInfo;
    private long lastPageOfMessageContents;

    @AllArgsConstructor
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class ArticleInfo {

        private String title;
        private int price;
        private String productImageUrl;

        public static MessageRoomInfoResponse.ArticleInfo createArticleInfo(Article article) {
            return new MessageRoomInfoResponse.ArticleInfo(
                article.getTitle(),
                article.getPrice(),
                article.getMainImageUrl()
            );
        }

    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class MessagePartnerInfo {

        private String nickName;
        private String profileImageUrl;

        private MessagePartnerInfo(String userNickName, String userProfileImageUrl) {
            this.nickName = userNickName;
            this.profileImageUrl = userProfileImageUrl;
        }

        public static MessageRoomInfoResponse.MessagePartnerInfo createMessagePartnerInfo(
            Member member) {
            return new MessageRoomInfoResponse.MessagePartnerInfo(member.getNickname(),
                member.getProfileImageUrl());
        }
    }

    @EqualsAndHashCode
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class OfferInfo {

        private int offerPrice;
        private boolean isSelected;

        public OfferInfo(int offerPrice, boolean isSelected) {
            this.offerPrice = offerPrice;
            this.isSelected = isSelected;
        }

        public static MessageRoomInfoResponse.OfferInfo createOfferInfo(
                Offer offer) {
            return new MessageRoomInfoResponse.OfferInfo(offer.getPrice(),
                    offer.getIsSelected());
        }
    }

}
