package com.sinsaflower.server.domain.order.repository;

import com.sinsaflower.server.domain.order.entity.Order;
import com.sinsaflower.server.domain.order.entity.OrderMessage;
import com.sinsaflower.server.domain.order.entity.OrderMessage.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMessageRepository extends JpaRepository<OrderMessage, Long> {

    // 주문별 메시지 조회 (정렬 순서대로)
    List<OrderMessage> findByOrderOrderBySortOrderAscIdAsc(Order order);

    // 메시지 타입별 조회
    List<OrderMessage> findByOrderAndMessageTypeOrderBySortOrderAscIdAsc(Order order, MessageType messageType);

    // 특정 텍스트 포함 메시지 검색
    @Query("SELECT m FROM OrderMessage m WHERE m.order = :order AND m.text LIKE %:text% ORDER BY m.sortOrder ASC, m.id ASC")
    List<OrderMessage> findByOrderAndTextContaining(@Param("order") Order order, @Param("text") String text);

    // 주문별 메시지 개수 조회
    @Query("SELECT COUNT(m) FROM OrderMessage m WHERE m.order = :order")
    long countByOrder(@Param("order") Order order);

    // 주문 삭제시 메시지들도 함께 삭제
    void deleteByOrder(Order order);
}
