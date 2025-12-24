package com.sinsaflower.server.domain.order.controller;

import com.sinsaflower.server.domain.order.dto.*;
import com.sinsaflower.server.domain.order.entity.Order;
import com.sinsaflower.server.domain.order.entity.Order.OrderStatus;
import com.sinsaflower.server.domain.order.service.OrderService;
import com.sinsaflower.server.domain.order.constants.OrderConstants;
import com.sinsaflower.server.domain.order.util.PagingUtils;
import com.sinsaflower.server.global.dto.ApiResponse;
import com.sinsaflower.server.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 주문 조회 전용 컨트롤러
 * 주문 목록, 검색, 통계 관련 API 제공
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "주문 조회", description = "주문 조회, 검색, 통계 API")
public class OrderQueryController {

    private final OrderService orderService;

    /**
     * 개별 주문 조회
     */
    @GetMapping("/{orderNumber}")
    @Operation(summary = "주문 조회", description = "주문 ID로 특정 주문을 조회합니다.")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @PathVariable String orderNumber,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("주문 조회 요청: {} by member: {}", orderNumber, userDetails.getUserId());

        OrderResponse response = orderService.getOrder(orderNumber);

        return ResponseEntity.ok(ApiResponse.success("주문 조회가 성공적으로 완료되었습니다.", response));
    }

    /**
     * 내 주문 목록 조회
     */
    @GetMapping("/my")
    @Operation(summary = "내 주문 목록 조회", description = "로그인한 회원의 주문 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("내 주문 목록 조회 요청: member {}", userDetails.getUserId());

        Pageable pageable = PagingUtils.createPageable(page, size, sort, direction);
        Page<Order> orders = orderService.getOrdersByMember(userDetails.getUserId(), pageable);
        Page<OrderResponse> response = orders.map(OrderResponse::from);

        return ResponseEntity.ok(ApiResponse.success("주문 목록 조회가 성공적으로 완료되었습니다.", response));
    }

    /**
     * 내 주문 상태별 조회
     */
    @GetMapping("/my/status/{status}")
    @Operation(summary = "내 주문 상태별 조회", description = "로그인한 회원의 특정 상태 주문들을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrdersByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("내 주문 상태별 조회 요청: {} for member: {}", status, userDetails.getUserId());

        Pageable pageable = PagingUtils.createPageable(page, size, sort, direction);
        Page<Order> orders = orderService.getOrdersByMemberAndStatus(userDetails.getUserId(), status, pageable);
        Page<OrderResponse> response = orders.map(OrderResponse::from);

        return ResponseEntity.ok(ApiResponse.success("상태별 주문 목록 조회가 성공적으로 완료되었습니다.", response));
    }

    /**
     * 발주 리스트 조회 (고급 검색)
     */
    @GetMapping("/purchase")
    @Operation(summary = "발주 리스트 조회", description = "다양한 조건으로 발주 리스트를 조회합니다.")
    public ResponseEntity<ApiResponse<Page<OrderPurchaseDto >>> getPurchaseOrders(
            @Parameter(description = "검색 시작일 (yyyy-MM-dd)")
            @RequestParam(required = false) String startDate,
            
            @Parameter(description = "검색 종료일 (yyyy-MM-dd)")
            @RequestParam(required = false) String endDate,
            
            @Parameter(description = "날짜 필드 타입 (createdAt, orderDate, deliveryDate)")
            @RequestParam(defaultValue = "createdAt") String dateField,
            
            @Parameter(description = "주문 상태 필터")
            @RequestParam(required = false) OrderStatus orderStatus,
            
            @Parameter(description = "검색 필드 (purchaseShopName, productName 등)")
            @RequestParam(required = false) String searchField,
            
            @Parameter(description = "검색 키워드")
            @RequestParam(required = false) String searchKeyword,
            
            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "페이지 크기")
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "정렬 필드")
            @RequestParam(defaultValue = "createdAt") String sort,
            
            @Parameter(description = "정렬 방향 (asc, desc)")
            @RequestParam(defaultValue = "desc") String direction,
            
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("발주 리스트 조회 요청: member {}", userDetails.getUserId());

        // 검색 조건 객체 생성
        OrderSearchRequest searchRequest = OrderSearchRequest.builder()
                .startDate(parseDate(startDate))
                .endDate(parseDate(endDate))
                .dateField(dateField)
                .orderStatus(orderStatus)
                .searchField(searchField)
                .searchKeyword(searchKeyword)
                .page(page)
                .size(size)
                .sort(sort)
                .direction(direction)
                .build();

        // 유효성 검증
        if (!searchRequest.isValidDateRange()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(OrderConstants.Messages.INVALID_DATE_RANGE));
        }

        if (!searchRequest.isValidSearchField()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(OrderConstants.Messages.INVALID_SEARCH_FIELD));
        }

        // 페이징 설정
        Pageable pageable = PagingUtils.createPageable(page, size, sort, direction);

        // 주문 목록 조회 (본인 주문만 조회)
        Page<OrderPurchaseDto> orders = orderService.searchOrders(
                List.of(userDetails.getUserId()), // 본인 memberId만 포함
                orderStatus,
                searchRequest.getStartDate(),
                searchRequest.getEndDate(),
                null, // regionIds
                pageable
        );
        return ResponseEntity.ok(
                ApiResponse.success(
                        "발주 리스트 조회가 성공적으로 완료되었습니다.",
                        orders
                )
        );

//        // OrderListResponse로 변환
//        Page<OrderListResponse> response = orders.map(OrderListResponse::from);
//
//        return ResponseEntity.ok(ApiResponse.success("발주 리스트 조회가 성공적으로 완료되었습니다.", response));
    }

    /**
     * 발주 요약 통계 조회
     */
    @GetMapping("/purchase/summary")
    @Operation(summary = "발주 요약 통계 조회", description = "본인의 발주 요약 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<OrderSummaryResponse>> getPurchaseSummary(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("발주 요약 통계 조회 요청: member {}", userDetails.getUserId());

        OrderSummaryResponse summary = orderService.getOrderSummary(userDetails.getUserId());

        return ResponseEntity.ok(ApiResponse.success("발주 요약 통계 조회가 성공적으로 완료되었습니다.", summary));
    }

    /**
     * 발주서/영수증 조회
     */
    @GetMapping("/purchase/{orderId}/receipt")
    @Operation(summary = "발주서/영수증 조회", description = "특정 주문의 발주서/영수증 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<OrderResponse>> getPurchaseReceipt(
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("발주서/영수증 조회 요청: order {} by member {}", orderId, userDetails.getUserId());

        Order order = orderService.getOrder(orderId);
        
        // 본인 주문인지 확인 (보안)
        if (!order.getMember().getId().equals(userDetails.getUserId())) {
            return ResponseEntity.notFound().build();
        }
        
        OrderResponse response = OrderResponse.from(order);

        return ResponseEntity.ok(ApiResponse.success("발주서/영수증 조회가 성공적으로 완료되었습니다.", response));
    }

    /**
     * 문자열을 LocalDate로 변환하는 헬퍼 메서드
     */
    private java.time.LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return java.time.LocalDate.parse(dateStr);
        } catch (Exception e) {
            log.warn("잘못된 날짜 형식: {}", dateStr);
            return null;
        }
    }
}
