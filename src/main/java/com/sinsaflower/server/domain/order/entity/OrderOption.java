package com.sinsaflower.server.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_option", indexes = {
    @Index(name = "idx_order_option_order_id", columnList = "order_id")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 옵션 정보
    @Column(length = 100, nullable = false)
    private String optionName; // 옵션명 (예: "카드", "추가")

    @Column(nullable = false)
    @Builder.Default
    private Boolean checked = false; // 선택 여부

    @Column(precision = 10, scale = 0)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO; // 옵션 가격

    @Column(length = 200)
    private String description; // 옵션 설명

    // 비즈니스 메서드
    public void toggleCheck() {
        this.checked = !this.checked;
    }

    public void updatePrice(BigDecimal newPrice) {
        this.price = newPrice != null ? newPrice : BigDecimal.ZERO;
    }
}
