package com.prgrms.offer.domain.member.model.entity;

import com.prgrms.offer.domain.member.model.value.Score;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Entity
@Builder
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private Long oauthId;
    private String oauthType;
    private String nickname;
    private String address;
    private String profileImageUrl;

    @Builder.Default
    private int offerLevel = 1;

    private int score;

    protected Member() {
    }

    public Member(final Long id, final Long oauthId, final String oauthType, final String nickname,
                  final String address, final String profileImageUrl,
                  final int offerLevel, final int score) {
        this.id = id;
        this.oauthId = oauthId;
        this.oauthType = oauthType;
        this.nickname = nickname;
        this.address = address;
        this.profileImageUrl = profileImageUrl;
        this.offerLevel = offerLevel;
        this.score = score;
    }

    public int evaluateScore(int score) {
        return this.score += Score.of(score).getValue();
    }

    public void chageOfferLevel(int offerLevel) {
        this.offerLevel = offerLevel;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeAddress(String address) {
        this.address = address;
    }

    public void changeProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return id.equals(member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
