package com.sinsaflower.server.domain.order.dto;

import com.sinsaflower.server.domain.order.entity.Order;
import com.sinsaflower.server.domain.order.entity.Order.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateResponse {

    private Long id;                        // 주문 ID
    private String orderNumber;             // 주문번호 (향후 추가 예정)
    private OrderStatus orderStatus;        // 주문 상태
    private String orderStatusDescription;  // 주문 상태 설명
    private String productName;             // 상품명
    private BigDecimal payment;             // 결제금액
    private LocalDate deliveryDate;         // 배송일
    private String shopName;                // 상점명
    private Boolean hasProductImage;        // 이미지 업로드 여부
    private LocalDateTime createdAt;        // 주문 생성 시간

    // Entity -> DTO 변환
    public static OrderCreateResponse from(Order order) {
        return OrderCreateResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber()) // 실제 주문번호 사용
                .orderStatus(order.getOrderStatus())
                .orderStatusDescription(order.getOrderStatus().getDescription())
                .productName(order.getProductName())
                .payment(order.getPayment())
                .deliveryDate(order.getDeliveryDate())
                .shopName(order.getShopName())
                .hasProductImage(order.hasProductImage())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
