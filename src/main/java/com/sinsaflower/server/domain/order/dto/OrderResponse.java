package com.sinsaflower.server.domain.order.dto;

import com.sinsaflower.server.domain.order.entity.Order;
import com.sinsaflower.server.domain.order.entity.Order.OrderStatus;
import com.sinsaflower.server.domain.order.entity.OrderOption;
import com.sinsaflower.server.domain.order.entity.OrderMessage;
import com.sinsaflower.server.domain.order.entity.OrderSender;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private String shopName;
    private String phone;
    private String productName;
    private String productDetail;
    private Integer quantity;
    private BigDecimal originPrice;
    private BigDecimal price;
    private BigDecimal payment;

    // 주문자 정보
    private String orderCustomerName;
    private String orderCustomerPhone;
    private String orderCustomerMobile;

    // 수령자 정보
    private String receiverName;
    private String receiverPhone;
    private String receiverMobile;

    // 배송 정보
    private LocalDate deliveryDate;
    private String deliveryHours;
    private String deliveryMinutes;
    private String deliveryType;
    private String eventHours;
    private String eventMinutes;
    private String deliveryPlace;

    // 추가 정보
    private String card;
    private String request;
    private Boolean hideDeliveryPhoto;

    // 상태 정보
    private OrderStatus orderStatus;
    private String orderStatusDescription;

    // 이미지 정보
    private String productImagePath;
    private String productImageOriginalName;
    private String productImageContentType;
    private Long productImageSize;
    private Boolean hasProductImage;

    // 연관 정보
    private Long memberId;
    private String memberName;
    private Long regionId;
    private String regionName;
    private Long productId;

    // 연관 데이터
    private List<OrderOptionResponse> options;
    private List<OrderMessageResponse> messages;
    private List<OrderSenderResponse> senders;

    // 총 금액
    private BigDecimal totalAmount;

    // 시간 정보
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity -> DTO 변환
    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .shopName(order.getShopName())
                .phone(order.getPhone())
                .productName(order.getProductName())
                .productDetail(order.getProductDetail())
                .quantity(order.getQuantity())
                .originPrice(order.getOriginPrice())
                .price(order.getPrice())
                .payment(order.getPayment())
                .orderCustomerName(order.getOrderCustomerName())
                .orderCustomerPhone(order.getOrderCustomerPhone())
                .orderCustomerMobile(order.getOrderCustomerMobile())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverMobile(order.getReceiverMobile())
                .deliveryDate(order.getDeliveryDate())
                .deliveryHours(order.getDeliveryHours())
                .deliveryMinutes(order.getDeliveryMinutes())
                .deliveryType(order.getDeliveryType())
                .eventHours(order.getEventHours())
                .eventMinutes(order.getEventMinutes())
                .deliveryPlace(order.getDeliveryPlace())
                .card(order.getCard())
                .request(order.getRequest())
                .hideDeliveryPhoto(order.getHideDeliveryPhoto())
                .orderStatus(order.getOrderStatus())
                .orderStatusDescription(order.getOrderStatus().getDescription())
                .productImagePath(order.getProductImagePath())
                .productImageOriginalName(order.getProductImageOriginalName())
                .productImageContentType(order.getProductImageContentType())
                .productImageSize(order.getProductImageSize())
                .hasProductImage(order.hasProductImage())
                .memberId(order.getMember() != null ? order.getMember().getId() : null)
                .memberName(order.getMember() != null ? order.getMember().getName() : null)
                .regionId(order.getRegion() != null ? order.getRegion().getId() : null)
                .regionName(order.getRegion() != null ? order.getRegion().getFullName() : null)
                .productId(order.getProduct() != null ? order.getProduct().getId() : null)
                .options(order.getOrderOptions().stream()
                        .map(OrderOptionResponse::from)
                        .collect(Collectors.toList()))
                .messages(order.getOrderMessages().stream()
                        .map(OrderMessageResponse::from)
                        .collect(Collectors.toList()))
                .senders(order.getOrderSenders().stream()
                        .map(OrderSenderResponse::from)
                        .collect(Collectors.toList()))
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    // 내부 DTO 클래스들
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderOptionResponse {
        private Long id;
        private String optionName;
        private Boolean checked;
        private BigDecimal price;
        private String description;

        public static OrderOptionResponse from(OrderOption option) {
            return OrderOptionResponse.builder()
                    .id(option.getId())
                    .optionName(option.getOptionName())
                    .checked(option.getChecked())
                    .price(option.getPrice())
                    .description(option.getDescription())
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderMessageResponse {
        private Long id;
        private String text;
        private OrderMessage.MessageType messageType;
        private String messageTypeDescription;
        private Integer sortOrder;

        public static OrderMessageResponse from(OrderMessage message) {
            return OrderMessageResponse.builder()
                    .id(message.getId())
                    .text(message.getText())
                    .messageType(message.getMessageType())
                    .messageTypeDescription(message.getMessageType().getDescription())
                    .sortOrder(message.getSortOrder())
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderSenderResponse {
        private Long id;
        private String name;
        private String relationship;
        private String phone;
        private Integer sortOrder;
        private Boolean isMain;

        public static OrderSenderResponse from(OrderSender sender) {
            return OrderSenderResponse.builder()
                    .id(sender.getId())
                    .name(sender.getName())
                    .relationship(sender.getRelationship())
                    .phone(sender.getPhone())
                    .sortOrder(sender.getSortOrder())
                    .isMain(sender.getIsMain())
                    .build();
        }
    }
}
