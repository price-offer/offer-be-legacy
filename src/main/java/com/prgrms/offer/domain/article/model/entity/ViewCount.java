package com.prgrms.offer.domain.article.model.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(indexes = {
        @Index(name = "view_count_article_id", columnList = "article_id")
})
public class ViewCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "view_count_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", referencedColumnName = "article_id")
    private Article article;

    @Column(name = "view_count")
    private long viewCount;

    public void addViewCount(){
        this.viewCount++;
    }

    private ViewCount(Article article) {
        this.article = article;
        this.viewCount = 1L;
    }

    public static ViewCount dd (Article article) {
        return new ViewCount(article);
    }
}
