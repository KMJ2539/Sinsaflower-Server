package com.sinsaflower.server.domain.order.repository;

import com.sinsaflower.server.domain.order.entity.Order;
import com.sinsaflower.server.domain.order.entity.OrderOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderOptionRepository extends JpaRepository<OrderOption, Long> {

    // 주문별 옵션 조회
    List<OrderOption> findByOrderOrderByIdAsc(Order order);

    // 선택된 옵션만 조회
    List<OrderOption> findByOrderAndCheckedTrueOrderByIdAsc(Order order);

    // 특정 옵션명으로 조회
    List<OrderOption> findByOrderAndOptionNameContainingOrderByIdAsc(Order order, String optionName);

    // 주문별 옵션 개수 조회
    @Query("SELECT COUNT(o) FROM OrderOption o WHERE o.order = :order")
    long countByOrder(@Param("order") Order order);

    // 주문별 선택된 옵션 개수 조회
    @Query("SELECT COUNT(o) FROM OrderOption o WHERE o.order = :order AND o.checked = true")
    long countCheckedByOrder(@Param("order") Order order);

    // 주문 삭제시 옵션들도 함께 삭제
    void deleteByOrder(Order order);
}
