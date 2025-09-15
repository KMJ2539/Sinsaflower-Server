package com.sinsaflower.server.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_message", indexes = {
    @Index(name = "idx_order_message_order_id", columnList = "order_id")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 메시지 정보
    @Column(length = 500, nullable = false)
    private String text; // 메시지 내용

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private MessageType messageType = MessageType.GENERAL; // 메시지 타입

    @Column(nullable = false)
    @Builder.Default
    private Integer sortOrder = 0; // 정렬 순서

    // 메시지 타입 enum
    public enum MessageType {
        GENERAL("일반메시지"),
        GREETING("축하메시지"),
        CONDOLENCE("조문메시지"),
        SPECIAL("특별메시지");

        private final String description;

        MessageType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // 비즈니스 메서드
    public void updateText(String newText) {
        this.text = newText != null ? newText.trim() : "";
    }

    public void updateSortOrder(Integer order) {
        this.sortOrder = order != null ? order : 0;
    }
}
