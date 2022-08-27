package com.prgrms.offer.domain.article.model.entity;

import com.prgrms.offer.common.message.ResponseMessage;
import com.prgrms.offer.core.error.exception.BusinessException;
import com.prgrms.offer.domain.article.model.value.TradeStatus;
import com.prgrms.offer.domain.member.model.entity.Member;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder(builderMethodName = "requiredFieldBuilder")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(indexes = {
        @Index(name = "article_idx_writer_id", columnList = "writer_id"),
        @Index(name = "article_idx_category_code", columnList = "category_code"),
        @Index(name = "article_idx_trade_status_code", columnList = "trade_status_code"),
        @Index(name = "article_idx_writer_id_trade_status_code", columnList = "writer_id, trade_status_code")
})
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", referencedColumnName = "member_id")
    private Member writer;

    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private Integer likeCount = 0;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "category_code")
    private int categoryCode;

    @Column(name = "product_status_code")
    private int productStatusCode;

    @Column(name = "trade_area")
    private String tradeArea;

    @Column(name = "trade_method_code")
    private int tradeMethodCode;

    @Column(name = "trade_status_code")
    private int tradeStatusCode;

    @Column(name = "main_image_url", columnDefinition = "TEXT")
    private String mainImageUrl;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "view_count")
    private int viewCount;

    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    private LocalDateTime modifiedDate;

    // TODO: 2022/08/26 필수 필드 추가, CategoryCode, productStatusCode, tradeArea tradeMethodCode, tradeStatusCode
    public static ArticleBuilder builder(Member writer, String title, String content, int price) {
        return requiredFieldBuilder()
                .writer(writer)
                .title(title)
                .content(content)
                .price(price);
    }

    public void updateInfo(String title, String content, int categoryCode, String tradeArea, int price) {
        this.title = title;
        this.content = content;
        this.categoryCode = categoryCode;
        this.tradeArea = tradeArea;
        this.price = price;
        modifiedDate = LocalDateTime.now();
    }

    public void updateTradeStatusCode(int tradeStatusCode) {
        if(TradeStatus.isCompleted(this.tradeStatusCode)){
            throw new BusinessException(ResponseMessage.ALREADY_SWITCH_TRADESTATUS);
        }

        this.tradeStatusCode = tradeStatusCode;
        modifiedDate = LocalDateTime.now();
    }

    public void updateTradeMethodCode(int tradeMethodCode) {
        this.tradeMethodCode = tradeMethodCode;
        modifiedDate = LocalDateTime.now();
    }

    public void addLikeCount(){
        this.likeCount++;
    }

    public void addViewCount(){
        this.viewCount++;
    }

    public void subtractLikeCount(){
        if(this.likeCount <= 0)
            throw new BusinessException(ResponseMessage.INTERNAL_SERVER_ERROR);

        this.likeCount--;
    }

    public void updateMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
        modifiedDate = LocalDateTime.now();
    }

    public boolean validateWriterByPrincipal(Long id){
        return this.writer.getId() == id ? true : false;
    }

    public boolean validateWriterByWriterId(Long writerId){
        return this.writer.getId().longValue() == writerId.longValue();
    }

}

