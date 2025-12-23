package com.sinsaflower.server.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberProductPriceResponse {

    private String categoryName;
    private Integer price;       // 천원 단위
    private Boolean isAvailable;
}
