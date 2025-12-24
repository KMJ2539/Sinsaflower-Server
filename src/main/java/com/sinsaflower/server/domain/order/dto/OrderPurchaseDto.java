package com.sinsaflower.server.domain.order.dto;

import com.sinsaflower.server.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@Builder
public class OrderPurchaseDto {

    private String orderNumber;
    private String orderType;
    private LocalDateTime createdAt;
    private LocalDate deliveryDate;
    private String deliveryTime;
    private String deliveryMinutes;

    private String receiver;
    private String corpName;

    private String productName;
    private String deliveryAddress;

    private BigDecimal originPrice;
    private BigDecimal payment;

    private Order.OrderStatus deliveryStatus;

    private Boolean isDelivery;
    private Boolean onSite;

    // 🔥 JPQL 전용 생성자 (순서/타입 정확히 맞춤)
    public OrderPurchaseDto(
            String orderNumber,
            String orderType,
            LocalDateTime createdAt,
            LocalDate deliveryDate,
            String deliveryTime,
            String deliveryMinutes,
            String receiver,
            String corpName,
            String productName,
            String deliveryAddress,
            BigDecimal originPrice,
            BigDecimal payment,
            Order.OrderStatus deliveryStatus,
            Boolean isDelivery,
            Boolean onSite
    ) {
        this.orderNumber = orderNumber;
        this.orderType = orderType;
        this.createdAt = createdAt;
        this.deliveryDate = deliveryDate;
        this.deliveryTime = deliveryTime;
        this.deliveryMinutes = deliveryMinutes;
        this.receiver = receiver;
        this.corpName = corpName;
        this.productName = productName;
        this.deliveryAddress = deliveryAddress;
        this.originPrice = originPrice;
        this.payment = payment;
        this.deliveryStatus = deliveryStatus;
        this.isDelivery = isDelivery;
        this.onSite = onSite;
    }
}