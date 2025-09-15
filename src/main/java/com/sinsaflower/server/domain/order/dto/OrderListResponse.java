package com.sinsaflower.server.domain.order.dto;

import com.sinsaflower.server.domain.order.entity.Order;
import com.sinsaflower.server.domain.order.constants.OrderConstants;
import lombok.*;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderListResponse {

    private String orderNumber;      // 주문번호 (예: "877915")
    private String orderType;        // 주문타입 (예: "직")
    private String orderDate;        // 주문일 (예: "25-07-22")
    private String orderTime;        // 주문시간 (예: "18:43")
    private String deliveryDate;     // 배송일 (예: "25-07-31")
    private String deliveryTime;     // 배송시간 (예: "기본시간")
    private String sender;           // 발송자 (예: "")
    private String receiver;         // 수령자 (예: "고인 OOO")
    private String corpAddress;      // 업체주소 (예: "강원 속초시")
    private String corpName;         // 업체명 (예: "다경플라워")
    private String productName;      // 상품명 (예: "근조3단")
    private String deliveryAddress;  // 배송주소 (예: "강릉시 사천면 방동길 38")
    private BigDecimal originPrice;  // 원가 (예: 0)
    private BigDecimal payment;      // 결제금액 (예: 200000)
    private String sms;              // SMS 상태 (예: "성공")
    private String fax;              // FAX 상태 (예: "거부")
    private String deliveryStatus;   // 배송상태 (예: "배송완료")
    private String consignee;        // 수탁자 (예: "김철수")
    private Boolean isDelivery;      // 배송여부 (예: true)
    private Boolean onSite;          // 현장여부 (예: false)

    // Entity -> DTO 변환
    public static OrderListResponse from(Order order) {
        return OrderListResponse.builder()
                .orderNumber(order.getOrderNumber())
                .orderType(order.getOrderType()) // 실제 필드 사용
                .orderDate(formatDate(order.getCreatedAt().toLocalDate()))
                .orderTime(formatTime(order.getCreatedAt()))
                .deliveryDate(formatDate(order.getDeliveryDate()))
                .deliveryTime(buildDeliveryTime(order)) // 배송 시간 조합
                .sender(getFirstSenderName(order)) // 첫 번째 발송자명
                .receiver(order.getReceiverName()) // 실제 필드 사용
                .corpAddress(getCorpAddress(order)) // Member 비즈니스 프로필 주소
                .corpName(order.getShopName())
                .productName(order.getProductName())
                .deliveryAddress(order.getDeliveryPlace()) // deliveryPlace를 deliveryAddress로 매핑
                .originPrice(order.getOriginPrice() != null ? order.getOriginPrice() : BigDecimal.ZERO)
                .payment(order.getPayment())
                .sms(order.getSms()) // 실제 필드 사용
                .fax(order.getFax()) // 실제 필드 사용
                .deliveryStatus(mapOrderStatusToDeliveryStatus(order.getOrderStatus()))
                .consignee(order.getConsignee()) // 실제 필드 사용
                .isDelivery(order.getIsDelivery()) // 실제 필드 사용
                .onSite(order.getOnSite()) // 실제 필드 사용
                .build();
    }

    // 날짜 포맷팅 (yy-MM-dd)
    private static String formatDate(java.time.LocalDate date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("yy-MM-dd"));
    }

    // 시간 포맷팅 (HH:mm)
    private static String formatTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // 배송 시간 조합 (deliveryYear + deliveryHours + deliveryMinutes)
    private static String buildDeliveryTime(Order order) {
        if (order.getDeliveryHours() != null && order.getDeliveryMinutes() != null) {
            return order.getDeliveryHours() + ":" + order.getDeliveryMinutes();
        }
        return OrderConstants.DeliveryTime.DEFAULT_TIME; // 기본값
    }

    // 회사 주소 가져오기 (Member 비즈니스 프로필에서)
    private static String getCorpAddress(Order order) {
        if (order.getMember() != null && 
            order.getMember().getBusinessProfile() != null && 
            order.getMember().getBusinessProfile().getCompanyAddress() != null) {
            return order.getMember().getBusinessProfile().getCompanyAddress();
        }
        return ""; // 비즈니스 프로필이 없거나 주소가 없으면 빈값
    }

    // 첫 번째 발송자명 가져오기
    private static String getFirstSenderName(Order order) {
        if (order.getOrderSenders() != null && !order.getOrderSenders().isEmpty()) {
            return order.getOrderSenders().get(0).getName();
        }
        return ""; // 발송자가 없으면 빈값
    }

    // OrderStatus를 배송상태로 매핑
    private static String mapOrderStatusToDeliveryStatus(Order.OrderStatus orderStatus) {
        if (orderStatus == null) return "";
        
        return switch (orderStatus) {
            case PENDING -> "주문접수";
            case CONFIRMED -> "주문확인";
            case PREPARING -> "배송준비";
            case DELIVERED -> "배송완료";
            case CANCELLED -> "주문취소";
        };
    }
}
