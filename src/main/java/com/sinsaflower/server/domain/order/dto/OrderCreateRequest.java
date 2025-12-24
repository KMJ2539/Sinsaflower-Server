package com.sinsaflower.server.domain.order.dto;

import com.sinsaflower.server.domain.order.entity.Order;
import com.sinsaflower.server.domain.order.entity.OrderOption;
import com.sinsaflower.server.domain.order.entity.OrderMessage;
import com.sinsaflower.server.domain.order.entity.OrderSender;
import com.sinsaflower.server.domain.order.constants.OrderConstants;
import lombok.*;

import jakarta.validation.constraints.*;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderCreateRequest {

    // 기본 주문 정보
    @Size(max = 10, message = "주문 타입은 10자 이하여야 합니다")
    @Builder.Default
    private String orderType = OrderConstants.OrderType.DIRECT; // 주문 타입 ("직", "본")

    @NotBlank(message = "상점명은 필수입니다")
    @Size(max = 100, message = "상점명은 100자 이하여야 합니다")
    private String shopName;

    @NotBlank(message = "연락처는 필수입니다")
    @Size(max = 20, message = "연락처는 20자 이하여야 합니다")
    private String phone;

    @NotBlank(message = "상품명은 필수입니다")
    @Size(max = 200, message = "상품명은 200자 이하여야 합니다")
    private String productName;

    @Size(max = 500, message = "상품 상세는 500자 이하여야 합니다")
    private String productDetail;

    @Min(value = 1, message = "수량은 1개 이상이어야 합니다")
    @Builder.Default
    private Integer quantity = 1;

    @DecimalMin(value = "0", message = "원가는 0 이상이어야 합니다")
    private BigDecimal originPrice;

    @NotNull(message = "판매가는 필수입니다")
    @DecimalMin(value = "0", message = "판매가는 0 이상이어야 합니다")
    private BigDecimal price;

    @NotNull(message = "결제금액은 필수입니다")
    @DecimalMin(value = "0", message = "결제금액은 0 이상이어야 합니다")
    private BigDecimal payment;

    // 주문자 정보
    @NotBlank(message = "주문자명은 필수입니다")
    @Size(max = 50, message = "주문자명은 50자 이하여야 합니다")
    private String orderCustomerName;

    @Size(max = 20, message = "주문자 전화번호는 20자 이하여야 합니다")
    private String orderCustomerPhone;

    @NotBlank(message = "주문자 휴대폰은 필수입니다")
    @Size(max = 20, message = "주문자 휴대폰은 20자 이하여야 합니다")
    private String orderCustomerMobile;

    // 수령자 정보
    @NotBlank(message = "수령자명은 필수입니다")
    @Size(max = 50, message = "수령자명은 50자 이하여야 합니다")
    private String receiverName;

    @Size(max = 20, message = "수령자 전화번호는 20자 이하여야 합니다")
    private String receiverPhone;

    @Size(max = 20, message = "수령자 휴대폰은 20자 이하여야 합니다")
    private String receiverMobile;

    // 배송 정보
    @NotNull(message = "배송일은 필수입니다")
    @Future(message = "배송일은 미래 날짜여야 합니다")
    private LocalDate deliveryDate;

    @Size(max = 10, message = "배송 시간은 10자 이하여야 합니다")
    private String deliveryHours;

    @Size(max = 10, message = "배송 분은 10자 이하여야 합니다")
    private String deliveryMinutes;

    @Size(max = 20, message = "배송 타입은 20자 이하여야 합니다")
    private String deliveryType;

    @Size(max = 10, message = "행사 시간은 10자 이하여야 합니다")
    private String eventHours;

    @Size(max = 10, message = "행사 분은 10자 이하여야 합니다")
    private String eventMinutes;

    @NotBlank(message = "배송 장소는 필수입니다")
    @Size(max = 200, message = "배송 장소는 200자 이하여야 합니다")
    private String deliveryPlace;

    // 추가 정보
    @Size(max = 50, message = "카드 정보는 50자 이하여야 합니다")
    private String card;

    @Size(max = 500, message = "요청사항은 500자 이하여야 합니다")
    private String request;

    @Builder.Default
    private Boolean hideDeliveryPhoto = false;

    // 연관 데이터
    private Long regionId; // 지역 ID
    private Long productId; // 상품 ID

    // 옵션, 메시지, 발송자 리스트
    @Builder.Default
    private List<OrderOptionRequest> options = new ArrayList<>();

    @Builder.Default
    private List<OrderMessageRequest> messages = new ArrayList<>();

    @Builder.Default
    private List<OrderSenderRequest> senders = new ArrayList<>();

    // DTO -> Entity 변환
    public Order toEntity() {
        return Order.builder()
                .orderType(orderType)
                .shopName(shopName)
                .phone(phone)
                .productName(productName)
                .productDetail(productDetail)
                .quantity(quantity)
                .originPrice(originPrice)
                .price(price)
                .payment(payment)
                .orderCustomerName(orderCustomerName)
                .orderCustomerPhone(orderCustomerPhone)
                .orderCustomerMobile(orderCustomerMobile)
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .receiverMobile(receiverMobile)
                .deliveryDate(deliveryDate)
                .deliveryHours(deliveryHours)
                .deliveryMinutes(deliveryMinutes)
                .deliveryType(deliveryType)
                .eventHours(eventHours)
                .eventMinutes(eventMinutes)
                .deliveryPlace(deliveryPlace)
                .card(card)
                .request(request)
                .hideDeliveryPhoto(hideDeliveryPhoto)
                .build();
    }

    // 내부 DTO 클래스들
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderOptionRequest {
        private String optionName;

        @Builder.Default
        private Boolean checked = false;

        @Builder.Default
        private BigDecimal price = BigDecimal.ZERO;

        private String description;

        public OrderOption toEntity() {
            return OrderOption.builder()
                    .optionName(optionName)
                    .checked(checked)
                    .price(price)
                    .description(description)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderMessageRequest {
        @NotBlank(message = "메시지 내용은 필수입니다")
        @Size(max = 500, message = "메시지는 500자 이하여야 합니다")
        private String text;

        @Builder.Default
        private OrderMessage.MessageType messageType = OrderMessage.MessageType.GENERAL;

        @Builder.Default
        private Integer sortOrder = 0;

        public OrderMessage toEntity() {
            return OrderMessage.builder()
                    .text(text)
                    .messageType(messageType)
                    .sortOrder(sortOrder)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderSenderRequest {
        @NotBlank(message = "발송자명은 필수입니다")
        @Size(max = 50, message = "발송자명은 50자 이하여야 합니다")
        private String name;

        @Size(max = 100, message = "관계는 100자 이하여야 합니다")
        private String relationship;

        @Size(max = 20, message = "연락처는 20자 이하여야 합니다")
        private String phone;

        @Builder.Default
        private Integer sortOrder = 0;

        @Builder.Default
        private Boolean isMain = false;

        public OrderSender toEntity() {
            return OrderSender.builder()
                    .name(name)
                    .relationship(relationship)
                    .phone(phone)
                    .sortOrder(sortOrder)
                    .isMain(isMain)
                    .build();
        }
    }
}
