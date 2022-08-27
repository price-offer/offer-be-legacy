package com.prgrms.offer.domain.article.model.entity;

import com.prgrms.offer.domain.member.model.entity.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(builderMethodName = "requiredFieldsBuilder")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "like_article_idx_member_id_article_id", columnList = "member_id, article_id", unique = true),
        @Index(name = "like_article_idx_member_id", columnList = "member_id")
})
public class LikeArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_article_id")
    private Long id;

    // TODO: 2022/08/26 Member가 아닌 memberId를 가지도록 수정. LikeArticle 조회시 memberId로 member를 추가로 쿼리해야됨
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", referencedColumnName = "article_id")
    private Article article;

    public static LikeArticleBuilder builder(Member member, Article article) {
        return requiredFieldsBuilder()
                .member(member)
                .article(article);
    }
}
