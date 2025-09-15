package com.sinsaflower.server.domain.order.repository;

import com.sinsaflower.server.domain.order.entity.Order;
import com.sinsaflower.server.domain.order.entity.OrderSender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderSenderRepository extends JpaRepository<OrderSender, Long> {

    // 주문별 발송자 조회 (정렬 순서대로)
    List<OrderSender> findByOrderOrderBySortOrderAscIdAsc(Order order);

    // 주 발송자 조회
    Optional<OrderSender> findByOrderAndIsMainTrue(Order order);

    // 발송자명으로 검색
    @Query("SELECT s FROM OrderSender s WHERE s.order = :order AND s.name LIKE %:name% ORDER BY s.sortOrder ASC, s.id ASC")
    List<OrderSender> findByOrderAndNameContaining(@Param("order") Order order, @Param("name") String name);

    // 관계별 발송자 조회
    @Query("SELECT s FROM OrderSender s WHERE s.order = :order AND s.relationship LIKE %:relationship% ORDER BY s.sortOrder ASC, s.id ASC")
    List<OrderSender> findByOrderAndRelationshipContaining(@Param("order") Order order, @Param("relationship") String relationship);

    // 주문별 발송자 개수 조회
    @Query("SELECT COUNT(s) FROM OrderSender s WHERE s.order = :order")
    long countByOrder(@Param("order") Order order);

    // 주문 삭제시 발송자들도 함께 삭제
    void deleteByOrder(Order order);
}
