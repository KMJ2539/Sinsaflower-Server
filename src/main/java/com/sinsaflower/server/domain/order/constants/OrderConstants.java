package com.sinsaflower.server.domain.order.constants;

/**
 * Order 도메인 관련 상수 정의
 * 매직 넘버와 하드코딩된 문자열을 중앙 관리
 */
public final class OrderConstants {

    private OrderConstants() {
        throw new IllegalStateException("Constants class");
    }

    // 주문 타입
    public static final class OrderType {
        public static final String DIRECT = "직";   // 직접 주문
        public static final String BRANCH = "본";   // 본사 주문
        
        private OrderType() {
            throw new IllegalStateException("Constants class");
        }
    }

    // 주문번호 생성 관련
    public static final class OrderNumber {
        public static final int MIN_VALUE = 100000;        // 6자리 최소값
        public static final int MAX_VALUE = 999999;        // 6자리 최대값
        public static final int RANGE = MAX_VALUE - MIN_VALUE + 1;  // 범위
        public static final int MAX_GENERATION_ATTEMPTS = 100;      // 최대 생성 시도 횟수
        
        private OrderNumber() {
            throw new IllegalStateException("Constants class");
        }
    }

    // 페이징 기본값
    public static final class Pagination {
        public static final int DEFAULT_PAGE = 0;
        public static final int DEFAULT_SIZE = 20;
        public static final int MAX_SIZE = 100;
        public static final String DEFAULT_SORT = "createdAt";
        public static final String DEFAULT_DIRECTION = "desc";
        
        private Pagination() {
            throw new IllegalStateException("Constants class");
        }
    }

    // 파일 업로드 관련
    public static final class FileUpload {
        public static final String ORDER_PRODUCT_IMAGE_PATH = "orders/products";
        public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
        public static final String[] ALLOWED_IMAGE_TYPES = {
            "image/jpeg", "image/jpg", "image/png", "image/gif"
        };
        
        private FileUpload() {
            throw new IllegalStateException("Constants class");
        }
    }

    // 알림 상태
    public static final class NotificationStatus {
        public static final String SUCCESS = "성공";
        public static final String FAILED = "실패";
        public static final String REJECTED = "거부";
        public static final String EMPTY = "";
        
        private NotificationStatus() {
            throw new IllegalStateException("Constants class");
        }
    }

    // 배송 시간 기본값
    public static final class DeliveryTime {
        public static final String DEFAULT_TIME = "기본시간";
        
        private DeliveryTime() {
            throw new IllegalStateException("Constants class");
        }
    }

    // 검색 필드
    public static final class SearchField {
        public static final String PURCHASE_SHOP_NAME = "purchaseShopName";
        public static final String SALES_SHOP_NAME = "salesShopName";
        public static final String PRODUCT_NAME = "productName";
        public static final String ORDER_NUMBER = "orderNumber";
        public static final String CONSIGNEE = "consignee";
        public static final String RECEIVER = "receiver";
        public static final String DELIVERY_ADDRESS = "deliveryAddress";
        public static final String CORP_NAME = "corpName";
        
        private SearchField() {
            throw new IllegalStateException("Constants class");
        }
    }

    // 날짜 필드
    public static final class DateField {
        public static final String CREATED_AT = "createdAt";
        public static final String ORDER_DATE = "orderDate";
        public static final String DELIVERY_DATE = "deliveryDate";
        
        private DateField() {
            throw new IllegalStateException("Constants class");
        }
    }

    // 메시지
    public static final class Messages {
        public static final String ORDER_CREATED = "주문이 생성되었습니다.";
        public static final String ORDER_UPDATED = "주문이 수정되었습니다.";
        public static final String ORDER_STATUS_UPDATED = "주문 상태가 변경되었습니다.";
        public static final String ORDER_DELETED = "주문이 삭제되었습니다.";
        public static final String ORDER_RETRIEVED = "주문 조회 완료";
        public static final String ORDER_LIST_RETRIEVED = "주문 목록 조회 완료";
        public static final String ORDER_STATISTICS_RETRIEVED = "주문 통계 조회 완료";
        public static final String IMAGE_UPLOADED = "상품 이미지가 업로드되었습니다.";
        public static final String IMAGE_DELETED = "상품 이미지가 삭제되었습니다.";
        
        // 에러 메시지
        public static final String INVALID_DATE_RANGE = "시작일이 종료일보다 늦을 수 없습니다.";
        public static final String INVALID_SEARCH_FIELD = "유효하지 않은 검색 필드입니다.";
        public static final String ORDER_NUMBER_GENERATION_FAILED = "주문번호 생성에 실패했습니다. 잠시 후 다시 시도해주세요.";
        
        private Messages() {
            throw new IllegalStateException("Constants class");
        }
    }
}
