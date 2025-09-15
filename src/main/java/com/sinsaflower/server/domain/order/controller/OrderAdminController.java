package com.sinsaflower.server.domain.order.controller;

import com.sinsaflower.server.domain.order.dto.OrderResponse;
import com.sinsaflower.server.domain.order.entity.Order;
import com.sinsaflower.server.domain.order.entity.Order.OrderStatus;
import com.sinsaflower.server.domain.order.service.OrderService;
import com.sinsaflower.server.domain.order.util.PagingUtils;
import com.sinsaflower.server.global.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 주문 관리자 전용 컨트롤러
 * 관리자용 주문 조회, 통계 API 제공
 */
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "주문 관리 (관리자)", description = "관리자 전용 주문 관리 API")
public class OrderAdminController {

    private final OrderService orderService;

    /**
     * 주문 상태별 조회 (관리자용)
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "주문 상태별 조회", description = "특정 상태의 모든 주문을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        log.info("관리자 상태별 주문 조회 요청: {}", status);

        Pageable pageable = PagingUtils.createPageable(page, size, sort, direction);
        Page<Order> orders = orderService.getOrdersByStatus(status, pageable);
        Page<OrderResponse> response = orders.map(OrderResponse::from);

        return ResponseEntity.ok(ApiResponse.success("상태별 주문 조회가 성공적으로 완료되었습니다.", response));
    }

    /**
     * 배송일별 주문 조회
     */
    @GetMapping("/delivery-date/{date}")
    @Operation(summary = "배송일별 주문 조회", description = "특정 배송일의 모든 주문을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrdersByDeliveryDate(
            @PathVariable LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        log.info("관리자 배송일별 주문 조회 요청: {}", date);

        Pageable pageable = PagingUtils.createPageable(page, size, sort, direction);
        Page<Order> orders = orderService.getOrdersByDeliveryDate(date, pageable);
        Page<OrderResponse> response = orders.map(OrderResponse::from);

        return ResponseEntity.ok(ApiResponse.success("배송일별 주문 조회가 성공적으로 완료되었습니다.", response));
    }

    /**
     * 오늘 주문 목록 조회
     */
    @GetMapping("/today")
    @Operation(summary = "오늘 주문 조회", description = "오늘 등록된 모든 주문을 조회합니다.")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getTodayOrders() {
        log.info("관리자 오늘 주문 조회 요청");

        List<Order> orders = orderService.getTodayOrders();
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success("오늘 주문 조회가 성공적으로 완료되었습니다.", response));
    }

    /**
     * 오늘 배송 예정 주문 조회
     */
    @GetMapping("/today-delivery")
    @Operation(summary = "오늘 배송 예정 주문 조회", description = "오늘 배송 예정인 모든 주문을 조회합니다.")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getTodayDeliveryOrders() {
        log.info("관리자 오늘 배송 예정 주문 조회 요청");

        List<Order> orders = orderService.getTodayDeliveryOrders();
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success("오늘 배송 예정 주문 조회가 성공적으로 완료되었습니다.", response));
    }

    /**
     * 주문 통계 조회
     */
    @GetMapping("/statistics")
    @Operation(summary = "주문 통계 조회", description = "전체 주문 상태별 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getOrderStatistics() {
        log.info("관리자 주문 통계 조회 요청");

        Map<String, Long> statistics = orderService.getOrderStatistics();

        return ResponseEntity.ok(ApiResponse.success("주문 통계 조회가 성공적으로 완료되었습니다.", statistics));
    }
}
