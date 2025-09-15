package com.sinsaflower.server.domain.order.dto;

import com.sinsaflower.server.domain.order.entity.Order.OrderStatus;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSearchRequest {

    // 날짜 검색 (이미지의 params 참고)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;    // 시작일 (예: "2025-07-01")

    @DateTimeFormat(pattern = "yyyy-MM-dd") 
    private LocalDate endDate;      // 종료일 (예: "2025-08-15")

    // 날짜 필드 타입 (이미지의 dateField 참고)
    @Builder.Default
    private String dateField = "createdAt";  // "createdAt", "orderDate", "deliveryDate"

    // 주문 상태 필터
    private OrderStatus orderStatus;  // 주문상태 필터

    // 검색 조건 (이미지의 searchField, searchKeyword 참고)
    private String searchField;      // 검색 필드 ("purchaseShopName" 등)
    private String searchKeyword;    // 검색 키워드

    // 페이징 정보
    @Builder.Default
    private int page = 0;           // 페이지 번호 (0부터 시작)
    
    @Builder.Default
    private int size = 20;          // 페이지 크기

    // 정렬 정보
    @Builder.Default
    private String sort = "createdAt"; // 정렬 필드
    
    @Builder.Default
    private String direction = "desc"; // 정렬 방향 (asc, desc)

    public enum SearchField {
        PURCHASE_SHOP_NAME("purchaseShopName", "구매업체명"),  // 이미지에서 확인된 필드
        SALES_SHOP_NAME("salesShopName", "판매업체명"),
        PRODUCT_NAME("productName", "상품명"),
        ORDER_NUMBER("orderNumber", "주문번호"),
        CONSIGNEE("consignee", "수탁자"),
        RECEIVER("receiver", "수령자"),
        DELIVERY_ADDRESS("deliveryAddress", "배송주소"),
        CORP_NAME("corpName", "업체명");

        private final String fieldName;
        private final String description;

        SearchField(String fieldName, String description) {
            this.fieldName = fieldName;
            this.description = description;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getDescription() {
            return description;
        }
    }

    // 날짜 필드 enum
    public enum DateFieldType {
        CREATED_AT("createdAt", "주문등록일"),
        ORDER_DATE("orderDate", "주문일"),
        DELIVERY_DATE("deliveryDate", "배송일");

        private final String fieldName;
        private final String description;

        DateFieldType(String fieldName, String description) {
            this.fieldName = fieldName;
            this.description = description;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getDescription() {
            return description;
        }
    }

    // 유효성 검증 메서드들
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true; // null인 경우는 검증하지 않음
        }
        return !startDate.isAfter(endDate);
    }

    public boolean hasSearchKeyword() {
        return searchKeyword != null && !searchKeyword.trim().isEmpty();
    }

    public boolean hasDateRange() {
        return startDate != null && endDate != null;
    }

    // 검색 필드가 유효한지 확인
    public boolean isValidSearchField() {
        if (searchField == null) return true;
        
        for (SearchField field : SearchField.values()) {
            if (field.getFieldName().equals(searchField)) {
                return true;
            }
        }
        return false;
    }
}
