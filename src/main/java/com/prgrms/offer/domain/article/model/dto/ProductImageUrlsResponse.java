package com.prgrms.offer.domain.article.model.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class ProductImageUrlsResponse {
    private final List<String> imageUrls = new ArrayList<>();
}
