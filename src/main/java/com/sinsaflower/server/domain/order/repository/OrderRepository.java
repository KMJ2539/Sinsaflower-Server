package com.sinsaflower.server.domain.order.repository;

import com.sinsaflower.server.domain.order.entity.Order;
import com.sinsaflower.server.domain.order.entity.Order.OrderStatus;
import com.sinsaflower.server.domain.member.entity.Member;
import com.sinsaflower.server.domain.delivery.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 회원별 주문 조회
    List<Order> findByMemberAndIsDeletedFalseOrderByCreatedAtDesc(Member member);
    
    Page<Order> findByMemberAndIsDeletedFalse(Member member, Pageable pageable);

    // 주문 상태별 조회
    List<Order> findByOrderStatusAndIsDeletedFalseOrderByCreatedAtDesc(OrderStatus orderStatus);
    
    Page<Order> findByOrderStatusAndIsDeletedFalse(OrderStatus orderStatus, Pageable pageable);

    // 배송일별 주문 조회
    List<Order> findByDeliveryDateAndIsDeletedFalseOrderByCreatedAtDesc(LocalDate deliveryDate);
    
    Page<Order> findByDeliveryDateAndIsDeletedFalse(LocalDate deliveryDate, Pageable pageable);

    // 지역별 주문 조회
    List<Order> findByRegionAndIsDeletedFalseOrderByCreatedAtDesc(Region region);
    
    Page<Order> findByRegionAndIsDeletedFalse(Region region, Pageable pageable);

    // 기간별 주문 조회
    @Query("SELECT o FROM Order o WHERE o.deliveryDate BETWEEN :startDate AND :endDate AND o.isDeleted = false ORDER BY o.createdAt DESC")
    List<Order> findByDeliveryDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT o FROM Order o WHERE o.deliveryDate BETWEEN :startDate AND :endDate AND o.isDeleted = false ORDER BY o.createdAt DESC")
    Page<Order> findByDeliveryDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    // 회원별 특정 상태 주문 조회
    List<Order> findByMemberAndOrderStatusAndIsDeletedFalseOrderByCreatedAtDesc(Member member, OrderStatus orderStatus);
    
    Page<Order> findByMemberAndOrderStatusAndIsDeletedFalse(Member member, OrderStatus orderStatus, Pageable pageable);

    // 주문자명으로 검색
    @Query("SELECT o FROM Order o WHERE o.orderCustomerName LIKE %:customerName% AND o.isDeleted = false ORDER BY o.createdAt DESC")
    List<Order> findByOrderCustomerNameContaining(@Param("customerName") String customerName);
    
    @Query("SELECT o FROM Order o WHERE o.orderCustomerName LIKE %:customerName% AND o.isDeleted = false ORDER BY o.createdAt DESC")
    Page<Order> findByOrderCustomerNameContaining(@Param("customerName") String customerName, Pageable pageable);

    // 수령자명으로 검색
    @Query("SELECT o FROM Order o WHERE o.receiverName LIKE %:receiverName% AND o.isDeleted = false ORDER BY o.createdAt DESC")
    List<Order> findByReceiverNameContaining(@Param("receiverName") String receiverName);

    // 상품명으로 검색
    @Query("SELECT o FROM Order o WHERE o.productName LIKE %:productName% AND o.isDeleted = false ORDER BY o.createdAt DESC")
    List<Order> findByProductNameContaining(@Param("productName") String productName);

    // 복합 검색 조건
    @Query("SELECT o FROM Order o WHERE " +
           "(:memberIds IS NULL OR o.member.id IN :memberIds) AND " +
           "(:orderStatus IS NULL OR o.orderStatus = :orderStatus) AND " +
           "(:startDate IS NULL OR o.deliveryDate >= :startDate) AND " +
           "(:endDate IS NULL OR o.deliveryDate <= :endDate) AND " +
           "(:regionIds IS NULL OR o.region.id IN :regionIds) AND " +
           "o.isDeleted = false " +
           "ORDER BY o.createdAt DESC")
    Page<Order> findOrdersWithConditions(
            @Param("memberIds") List<Long> memberIds,
            @Param("orderStatus") OrderStatus orderStatus,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("regionIds") List<Long> regionIds,
            Pageable pageable
    );

    // 통계용 쿼리들
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :status AND o.isDeleted = false")
    long countByOrderStatus(@Param("status") OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.deliveryDate = :date AND o.isDeleted = false")
    long countByDeliveryDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.member = :member AND o.isDeleted = false")
    long countByMember(@Param("member") Member member);

    // 오늘 주문 조회
    @Query("SELECT o FROM Order o WHERE CAST(o.createdAt AS DATE) = CURRENT_DATE AND o.isDeleted = false ORDER BY o.createdAt DESC")
    List<Order> findTodayOrders();

    // 오늘 배송 예정 주문 조회
    @Query("SELECT o FROM Order o WHERE o.deliveryDate = CAST(CURRENT_DATE AS DATE) AND o.isDeleted = false ORDER BY o.createdAt DESC")
    List<Order> findTodayDeliveryOrders();

    // 주문번호 중복 확인
    boolean existsByOrderNumber(String orderNumber);

    // Summary 통계용 쿼리들
    
    // 회원별 총 주문 개수
    @Query("SELECT COUNT(o) FROM Order o WHERE o.member.id = :memberId AND o.isDeleted = false")
    long countByMemberId(@Param("memberId") Long memberId);

    // 회원별 이번 달 주문 개수
    @Query("SELECT COUNT(o) FROM Order o WHERE o.member.id = :memberId AND " +
           "YEAR(o.createdAt) = YEAR(CURRENT_DATE) AND MONTH(o.createdAt) = MONTH(CURRENT_DATE) AND " +
           "o.isDeleted = false")
    long countByMemberIdAndCurrentMonth(@Param("memberId") Long memberId);

    // 회원별 배송완료 주문 개수
    @Query("SELECT COUNT(o) FROM Order o WHERE o.member.id = :memberId AND " +
           "o.orderStatus = :status AND o.isDeleted = false")
    long countByMemberIdAndOrderStatus(@Param("memberId") Long memberId, @Param("status") OrderStatus status);

    // 회원별 진행중 주문 개수 (PENDING, CONFIRMED, PREPARING)
    @Query("SELECT COUNT(o) FROM Order o WHERE o.member.id = :memberId AND " +
           "o.orderStatus IN ('PENDING', 'CONFIRMED', 'PREPARING') AND o.isDeleted = false")
    long countByMemberIdAndInProgress(@Param("memberId") Long memberId);
}
