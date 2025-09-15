package com.sinsaflower.server.domain.order.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSummaryResponse {

    private Long totalCount;        // 본인의 총 주문 개수
    private Long monthCount;        // 이번 달 본인 주문 개수
    private Long deliveredCount;    // 본인의 배송완료 주문 개수
    private Long inProgressCount;   // 본인의 진행중 주문 개수 (주문접수~배송준비)

    // 정적 팩토리 메서드
    public static OrderSummaryResponse of(Long totalCount, Long monthCount, 
                                        Long deliveredCount, Long inProgressCount) {
        return OrderSummaryResponse.builder()
                .totalCount(totalCount)
                .monthCount(monthCount)
                .deliveredCount(deliveredCount)
                .inProgressCount(inProgressCount)
                .build();
    }
}
