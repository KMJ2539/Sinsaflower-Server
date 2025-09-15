package com.sinsaflower.server.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_sender", indexes = {
    @Index(name = "idx_order_sender_order_id", columnList = "order_id")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 발송자 정보
    @Column(length = 50, nullable = false)
    private String name; // 발송자명

    @Column(length = 100)
    private String relationship; // 관계 (예: 친구, 가족, 동료 등)

    @Column(length = 20)
    private String phone; // 연락처

    @Column(nullable = false)
    @Builder.Default
    private Integer sortOrder = 0; // 정렬 순서

    @Column(nullable = false)
    @Builder.Default
    private Boolean isMain = false; // 주 발송자 여부

    // 비즈니스 메서드
    public void updateName(String newName) {
        this.name = newName != null ? newName.trim() : "";
    }

    public void setAsMainSender() {
        this.isMain = true;
    }

    public void unsetAsMainSender() {
        this.isMain = false;
    }

    public void updateSortOrder(Integer order) {
        this.sortOrder = order != null ? order : 0;
    }
}
