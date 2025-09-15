package com.sinsaflower.server.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;
import com.sinsaflower.server.domain.common.BaseTimeEntity;
import com.sinsaflower.server.domain.member.entity.Member;
import com.sinsaflower.server.domain.delivery.entity.Region;
import com.sinsaflower.server.domain.product.entity.Product;
import com.sinsaflower.server.domain.order.constants.OrderConstants;
import com.sinsaflower.server.global.exception.InvalidRequestException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_date", columnList = "deliveryDate"),
    @Index(name = "idx_order_status", columnList = "orderStatus"),
    @Index(name = "idx_member_id", columnList = "member_id")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 주문번호 (6자리 숫자)
    @Column(length = 6, unique = true, nullable = false)
    private String orderNumber;


    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 주문한 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region; // 배송 지역

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; // 주문 상품

    // 기본 주문 정보
    @Column(length = 10)
    @Builder.Default
    private String orderType = OrderConstants.OrderType.DIRECT; // 주문 타입 ("직", "본")

    @Column(length = 100, nullable = false)
    private String shopName; // 상점명

    @Column(length = 20, nullable = false)
    private String phone; // 상점 연락처

    @Column(length = 200, nullable = false)
    private String productName; // 상품명

    @Column(length = 500)
    private String productDetail; // 상품 상세

    @Column(nullable = false)
    private Integer quantity = 1; // 수량

    @Column(precision = 10, scale = 0)
    private BigDecimal originPrice; // 원가

    @Column(precision = 10, scale = 0, nullable = false)
    private BigDecimal price; // 판매가

    @Column(precision = 10, scale = 0, nullable = false)
    private BigDecimal payment; // 결제금액

    // 주문자 정보
    @Column(length = 50, nullable = false)
    private String orderCustomerName; // 주문자명

    @Column(length = 20)
    private String orderCustomerPhone; // 주문자 전화번호

    @Column(length = 20, nullable = false)
    private String orderCustomerMobile; // 주문자 휴대폰

    // 수령자 정보
    @Column(length = 50, nullable = false)
    private String receiverName; // 수령자명

    @Column(length = 20)
    private String receiverPhone; // 수령자 전화번호

    @Column(length = 20)
    private String receiverMobile; // 수령자 휴대폰

    // 수탁자 (실제 수령 담당자)
    @Column(length = 50)
    private String consignee; // 수탁자 (예: 병원 담당자, 장례식장 담당자)

    // 배송 정보
    @Column(nullable = false)
    private LocalDate deliveryDate; // 배송일

    @Column
    private Integer deliveryYear; // 배송 년도 (백엔드에서 자동 설정)

    @Column(length = 10)
    private String deliveryHours; // 배송 시간

    @Column(length = 10)
    private String deliveryMinutes; // 배송 분

    @Column(length = 20)
    private String deliveryType; // 배송 타입

    @Column(length = 10)
    private String eventHours; // 행사 시간

    @Column(length = 10)
    private String eventMinutes; // 행사 분

    @Column(length = 200, nullable = false)
    private String deliveryPlace; // 배송 장소

    // 추가 정보
    @Column(length = 50)
    private String card; // 카드 정보

    @Column(length = 500)
    private String request; // 요청사항

    @Column(nullable = false)
    @Builder.Default
    private Boolean hideDeliveryPhoto = false; // 배송 사진 숨김 여부

    // 알림 상태
    @Column(length = 10)
    @Builder.Default
    private String sms = ""; // SMS 발송 상태 ("성공", "실패", "거부", "")

    @Column(length = 10)
    @Builder.Default
    private String fax = ""; // FAX 발송 상태 ("성공", "실패", "거부", "")

    // 배송 관련 플래그
    @Column(nullable = false)
    @Builder.Default
    private Boolean isDelivery = true; // 배송 여부

    @Column(nullable = false)
    @Builder.Default
    private Boolean onSite = false; // 현장 여부

    // 주문 상태
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.PENDING; // 주문 상태

    // 연관 엔티티들
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderOption> orderOptions = new ArrayList<>(); // 주문 옵션

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderMessage> orderMessages = new ArrayList<>(); // 주문 메시지

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderSender> orderSenders = new ArrayList<>(); // 발송자 목록

    // 상품 이미지 정보 (productImage.File에 해당)
    @Column(length = 500)
    private String productImagePath; // 상품 이미지 파일 경로

    @Column(length = 255)
    private String productImageOriginalName; // 원본 파일명

    @Column(length = 100)
    private String productImageContentType; // 파일 MIME 타입

    @Column
    private Long productImageSize; // 파일 크기 (bytes)

    // 비즈니스 메서드
    public void addOrderOption(OrderOption orderOption) {
        this.orderOptions.add(orderOption);
        orderOption.setOrder(this);
    }

    public void addOrderMessage(OrderMessage orderMessage) {
        this.orderMessages.add(orderMessage);
        orderMessage.setOrder(this);
    }

    public void addOrderSender(OrderSender orderSender) {
        this.orderSenders.add(orderSender);
        orderSender.setOrder(this);
    }

    // 주문 상태 변경 (비즈니스 규칙 적용)
    public void updateStatus(OrderStatus newStatus) {
        validateStatusTransition(this.orderStatus, newStatus);
        this.orderStatus = newStatus;
    }
    
    // 상태 전환 유효성 검증
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // 취소된 주문은 다른 상태로 변경 불가
        if (currentStatus == OrderStatus.CANCELLED) {
            throw new InvalidRequestException("취소된 주문은 상태를 변경할 수 없습니다.");
        }

        // 배송 완료된 주문은 취소 불가
        if (currentStatus == OrderStatus.DELIVERED && newStatus == OrderStatus.CANCELLED) {
            throw new InvalidRequestException("배송 완료된 주문은 취소할 수 없습니다.");
        }
        
        // 동일한 상태로 변경 시도 방지
        if (currentStatus == newStatus) {
            throw new InvalidRequestException("이미 " + newStatus.getDescription() + " 상태입니다.");
        }
    }
    
    // 주문 취소 가능 여부 확인
    public boolean canBeCancelled() {
        return this.orderStatus != OrderStatus.CANCELLED && 
               this.orderStatus != OrderStatus.DELIVERED;
    }
    
    // 주문 수정 가능 여부 확인
    public boolean canBeModified() {
        return this.orderStatus == OrderStatus.PENDING || 
               this.orderStatus == OrderStatus.CONFIRMED;
    }

    // 총 주문 금액 계산 (기본 상품 + 옵션)
    public BigDecimal getTotalAmount() {
        BigDecimal optionTotal = orderOptions.stream()
                .filter(option -> option.getChecked())
                .map(OrderOption::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return payment.add(optionTotal);
    }

    // 상품 이미지 설정
    public void setProductImage(String filePath, String originalName, String contentType, Long fileSize) {
        this.productImagePath = filePath;
        this.productImageOriginalName = originalName;
        this.productImageContentType = contentType;
        this.productImageSize = fileSize;
    }

    // 상품 이미지 삭제
    public void removeProductImage() {
        this.productImagePath = null;
        this.productImageOriginalName = null;
        this.productImageContentType = null;
        this.productImageSize = null;
    }

    // 상품 이미지 존재 여부 확인
    public boolean hasProductImage() {
        return productImagePath != null && !productImagePath.trim().isEmpty();
    }
    
    // 배송년도 자동 설정
    public void setDeliveryYearFromDate() {
        if (this.deliveryDate != null) {
            this.deliveryYear = this.deliveryDate.getYear();
        } else {
            this.deliveryYear = Year.now().getValue();
        }
    }
    
    // 소프트 삭제 처리
    public void softDelete(String deletedBy) {
        super.softDelete(deletedBy);
    }
    
    // 주문 완료 여부 확인
    public boolean isCompleted() {
        return this.orderStatus == OrderStatus.DELIVERED;
    }
    
    // 진행 중인 주문 여부 확인
    public boolean isInProgress() {
        return this.orderStatus == OrderStatus.PENDING || 
               this.orderStatus == OrderStatus.CONFIRMED || 
               this.orderStatus == OrderStatus.PREPARING;
    }
    
    // 주문 유효성 검증
    public void validateOrder() {
        if (this.deliveryDate == null) {
            throw new InvalidRequestException("배송일은 필수입니다.");
        }
        
        if (this.deliveryDate.isBefore(LocalDate.now())) {
            throw new InvalidRequestException("배송일은 오늘 이후여야 합니다.");
        }
        
        if (this.payment == null || this.payment.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRequestException("결제금액은 0보다 커야 합니다.");
        }
        
        if (this.quantity == null || this.quantity <= 0) {
            throw new InvalidRequestException("수량은 1개 이상이어야 합니다.");
        }
    }
    
    // 배송 시간 조합 문자열 생성
    public String getDeliveryTimeString() {
        if (this.deliveryHours != null && this.deliveryMinutes != null) {
            return String.format("%s:%s", this.deliveryHours, this.deliveryMinutes);
        }
        return OrderConstants.DeliveryTime.DEFAULT_TIME;
    }
    
    // 행사 시간 조합 문자열 생성
    public String getEventTimeString() {
        if (this.eventHours != null && this.eventMinutes != null) {
            return String.format("%s:%s", this.eventHours, this.eventMinutes);
        }
        return "";
    }

    // 주문 상태 enum
    public enum OrderStatus {
        PENDING("주문접수"),
        CONFIRMED("주문확인"),
        PREPARING("배송준비"),
        DELIVERED("배송완료"),
        CANCELLED("주문취소");

        private final String description;

        OrderStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
