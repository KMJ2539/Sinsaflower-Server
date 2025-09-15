package com.sinsaflower.server.domain.order.service;

import com.sinsaflower.server.domain.order.entity.Order;
import com.sinsaflower.server.domain.order.entity.Order.OrderStatus;
import com.sinsaflower.server.domain.order.entity.OrderOption;
import com.sinsaflower.server.domain.order.entity.OrderMessage;
import com.sinsaflower.server.domain.order.entity.OrderSender;
import com.sinsaflower.server.domain.order.dto.OrderSummaryResponse;
import com.sinsaflower.server.domain.order.repository.OrderRepository;
import com.sinsaflower.server.domain.order.repository.OrderOptionRepository;
import com.sinsaflower.server.domain.order.repository.OrderMessageRepository;
import com.sinsaflower.server.domain.order.repository.OrderSenderRepository;
import com.sinsaflower.server.domain.order.constants.OrderConstants;
import com.sinsaflower.server.domain.member.entity.Member;
import com.sinsaflower.server.domain.member.repository.MemberRepository;
import com.sinsaflower.server.domain.delivery.entity.Region;
import com.sinsaflower.server.domain.delivery.repository.RegionRepository;
import com.sinsaflower.server.domain.product.entity.Product;
import com.sinsaflower.server.global.exception.ResourceNotFoundException;
import com.sinsaflower.server.global.exception.InvalidRequestException;
import com.sinsaflower.server.global.service.FileUploadService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderOptionRepository orderOptionRepository;
    private final OrderMessageRepository orderMessageRepository;
    private final OrderSenderRepository orderSenderRepository;
    private final MemberRepository memberRepository;
    private final FileUploadService fileUploadService;

    /**
     * 주문 생성
     */
    @Transactional
    public Order createOrder(Long memberId, Order orderData) {
        log.info("Creating order for member: {}", memberId);

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + memberId));

        // 지역 조회 (지역 정보가 있는 경우) - DTO에서 regionId로 처리
        // 이 부분은 Controller에서 처리하도록 수정 예정

        // 회원 정보 설정
        orderData.setMember(member);

        // 주문 유효성 검증
        orderData.validateOrder();
        
        // 주문번호 생성
        String orderNumber = generateUniqueOrderNumber();
        orderData.setOrderNumber(orderNumber);

        // 배송 년도 자동 설정
        orderData.setDeliveryYearFromDate();

        // 주문 저장
        Order savedOrder = orderRepository.save(orderData);

        // 연관 엔티티들 처리
        if (orderData.getOrderOptions() != null && !orderData.getOrderOptions().isEmpty()) {
            orderData.getOrderOptions().forEach(option -> {
                option.setOrder(savedOrder);
                orderOptionRepository.save(option);
            });
        }

        if (orderData.getOrderMessages() != null && !orderData.getOrderMessages().isEmpty()) {
            orderData.getOrderMessages().forEach(message -> {
                message.setOrder(savedOrder);
                orderMessageRepository.save(message);
            });
        }

        if (orderData.getOrderSenders() != null && !orderData.getOrderSenders().isEmpty()) {
            orderData.getOrderSenders().forEach(sender -> {
                sender.setOrder(savedOrder);
                orderSenderRepository.save(sender);
            });
        }

        log.info("Order created successfully: {}", savedOrder.getId());
        return savedOrder;
    }

    /**
     * 주문 조회
     */
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .filter(order -> !order.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    /**
     * 회원별 주문 목록 조회
     */
    public Page<Order> getOrdersByMember(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + memberId));
        
        return orderRepository.findByMemberAndIsDeletedFalse(member, pageable);
    }

    /**
     * 회원별 상태별 주문 목록 조회
     */
    public Page<Order> getOrdersByMemberAndStatus(Long memberId, OrderStatus status, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + memberId));
        
        return orderRepository.findByMemberAndOrderStatusAndIsDeletedFalse(member, status, pageable);
    }

    /**
     * 주문 상태별 조회
     */
    public Page<Order> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByOrderStatusAndIsDeletedFalse(status, pageable);
    }

    /**
     * 배송일별 주문 조회
     */
    public Page<Order> getOrdersByDeliveryDate(LocalDate deliveryDate, Pageable pageable) {
        return orderRepository.findByDeliveryDateAndIsDeletedFalse(deliveryDate, pageable);
    }

    /**
     * 복합 조건으로 주문 검색
     */
    public Page<Order> searchOrders(List<Long> memberIds, OrderStatus orderStatus, 
                                   LocalDate startDate, LocalDate endDate, 
                                   List<Long> regionIds, Pageable pageable) {
        return orderRepository.findOrdersWithConditions(
                memberIds, orderStatus, startDate, endDate, regionIds, pageable);
    }

    /**
     * 주문 상태 변경
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Updating order status: {} to {}", orderId, newStatus);

        Order order = getOrder(orderId);
        
        // Entity에서 비즈니스 규칙 검증 후 상태 변경
        order.updateStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        log.info("Order status updated successfully: {} -> {}", orderId, newStatus);
        return updatedOrder;
    }

    /**
     * 주문 수정
     */
    @Transactional
    public Order updateOrder(Long orderId, Order updateData) {
        log.info("Updating order: {}", orderId);

        Order existingOrder = getOrder(orderId);

        // 기본 정보 업데이트
        if (updateData.getShopName() != null) {
            existingOrder.setShopName(updateData.getShopName());
        }
        if (updateData.getProductName() != null) {
            existingOrder.setProductName(updateData.getProductName());
        }
        if (updateData.getProductDetail() != null) {
            existingOrder.setProductDetail(updateData.getProductDetail());
        }
        if (updateData.getQuantity() != null) {
            existingOrder.setQuantity(updateData.getQuantity());
        }
        if (updateData.getPrice() != null) {
            existingOrder.setPrice(updateData.getPrice());
        }
        if (updateData.getPayment() != null) {
            existingOrder.setPayment(updateData.getPayment());
        }
        if (updateData.getDeliveryDate() != null) {
            existingOrder.setDeliveryDate(updateData.getDeliveryDate());
        }
        if (updateData.getDeliveryPlace() != null) {
            existingOrder.setDeliveryPlace(updateData.getDeliveryPlace());
        }
        if (updateData.getRequest() != null) {
            existingOrder.setRequest(updateData.getRequest());
        }

        Order updatedOrder = orderRepository.save(existingOrder);
        log.info("Order updated successfully: {}", orderId);
        return updatedOrder;
    }

    /**
     * 주문 옵션 추가/수정
     */
    @Transactional
    public OrderOption addOrUpdateOrderOption(Long orderId, OrderOption optionData) {
        Order order = getOrder(orderId);
        optionData.setOrder(order);
        return orderOptionRepository.save(optionData);
    }

    /**
     * 주문 메시지 추가/수정
     */
    @Transactional
    public OrderMessage addOrUpdateOrderMessage(Long orderId, OrderMessage messageData) {
        Order order = getOrder(orderId);
        messageData.setOrder(order);
        return orderMessageRepository.save(messageData);
    }

    /**
     * 주문 발송자 추가/수정
     */
    @Transactional
    public OrderSender addOrUpdateOrderSender(Long orderId, OrderSender senderData) {
        Order order = getOrder(orderId);
        senderData.setOrder(order);
        return orderSenderRepository.save(senderData);
    }

    /**
     * 주문 삭제 (소프트 삭제)
     */
    @Transactional
    public void deleteOrder(Long orderId, String deletedBy) {
        log.info("Deleting order: {} by {}", orderId, deletedBy);

        Order order = getOrder(orderId);
        
        // 삭제 가능 여부 확인 (진행 중인 주문은 삭제 불가)
        if (!order.canBeCancelled()) {
            throw new InvalidRequestException("진행 중이거나 완료된 주문은 삭제할 수 없습니다.");
        }

        order.softDelete(deletedBy);
        orderRepository.save(order);

        log.info("Order deleted successfully: {}", orderId);
    }

    /**
     * 오늘 주문 목록 조회
     */
    public List<Order> getTodayOrders() {
        return orderRepository.findTodayOrders();
    }

    /**
     * 오늘 배송 예정 주문 목록 조회
     */
    public List<Order> getTodayDeliveryOrders() {
        return orderRepository.findTodayDeliveryOrders();
    }

    /**
     * 주문 통계 조회
     */
    public Map<String, Long> getOrderStatistics() {
        return Map.of(
                "pending", orderRepository.countByOrderStatus(OrderStatus.PENDING),
                "confirmed", orderRepository.countByOrderStatus(OrderStatus.CONFIRMED),
                "preparing", orderRepository.countByOrderStatus(OrderStatus.PREPARING),
                "delivered", orderRepository.countByOrderStatus(OrderStatus.DELIVERED),
                "cancelled", orderRepository.countByOrderStatus(OrderStatus.CANCELLED),
                "todayDelivery", (long) orderRepository.findTodayDeliveryOrders().size()
        );
    }

    /**
     * 주문에 상품 이미지 업로드
     */
    @Transactional
    public Order uploadProductImage(Long orderId, MultipartFile imageFile) {
        log.info("Uploading product image for order: {}", orderId);

        Order order = getOrder(orderId);

        try {
            // 기존 이미지가 있다면 삭제
            if (order.hasProductImage()) {
                fileUploadService.deleteFile(order.getProductImagePath());
            }

            // 새 이미지 업로드
            String filePath = fileUploadService.saveFile(imageFile, OrderConstants.FileUpload.ORDER_PRODUCT_IMAGE_PATH);
            
            // Order 엔티티에 이미지 정보 설정
            order.setProductImage(
                filePath,
                imageFile.getOriginalFilename(),
                imageFile.getContentType(),
                imageFile.getSize()
            );

            Order updatedOrder = orderRepository.save(order);
            log.info("Product image uploaded successfully for order: {}", orderId);
            return updatedOrder;

        } catch (IOException e) {
            log.error("Failed to upload product image for order: {}", orderId, e);
            throw new InvalidRequestException("Failed to upload image: " + e.getMessage());
        }
    }

    /**
     * 주문의 상품 이미지 삭제
     */
    @Transactional
    public Order deleteProductImage(Long orderId) {
        log.info("Deleting product image for order: {}", orderId);

        Order order = getOrder(orderId);

        if (order.hasProductImage()) {
            // 파일 시스템에서 이미지 삭제
            fileUploadService.deleteFile(order.getProductImagePath());
            
            // Order 엔티티에서 이미지 정보 제거
            order.removeProductImage();
            
            Order updatedOrder = orderRepository.save(order);
            log.info("Product image deleted successfully for order: {}", orderId);
            return updatedOrder;
        } else {
            log.warn("No product image found for order: {}", orderId);
            return order;
        }
    }

    /**
     * 주문 생성 시 이미지와 함께 처리
     */
    @Transactional
    public Order createOrderWithImage(Long memberId, Order orderData, MultipartFile productImage) {
        log.info("Creating order with image for member: {}", memberId);

        // 기본 주문 생성
        Order savedOrder = createOrder(memberId, orderData);

        // 이미지가 있다면 업로드
        if (productImage != null && !productImage.isEmpty()) {
            try {
                String filePath = fileUploadService.saveFile(productImage, OrderConstants.FileUpload.ORDER_PRODUCT_IMAGE_PATH);
                savedOrder.setProductImage(
                    filePath,
                    productImage.getOriginalFilename(),
                    productImage.getContentType(),
                    productImage.getSize()
                );
                savedOrder = orderRepository.save(savedOrder);
                log.info("Order created with image successfully: {}", savedOrder.getId());
            } catch (IOException e) {
                log.error("Failed to upload image during order creation: {}", savedOrder.getId(), e);
                // 이미지 업로드 실패해도 주문은 생성되도록 함
            }
        }

        return savedOrder;
    }

    /**
     * 회원별 주문 요약 통계 조회
     */
    public OrderSummaryResponse getOrderSummary(Long memberId) {
        log.info("Getting order summary for member: {}", memberId);

        // 총 주문 개수
        long totalCount = orderRepository.countByMemberId(memberId);

        // 이번 달 주문 개수
        long monthCount = orderRepository.countByMemberIdAndCurrentMonth(memberId);

        // 배송완료 주문 개수
        long deliveredCount = orderRepository.countByMemberIdAndOrderStatus(memberId, OrderStatus.DELIVERED);

        // 진행중 주문 개수 (PENDING, CONFIRMED, PREPARING)
        long inProgressCount = orderRepository.countByMemberIdAndInProgress(memberId);

        return OrderSummaryResponse.of(totalCount, monthCount, deliveredCount, inProgressCount);
    }

    /**
     * 중복되지 않는 6자리 주문번호 생성
     */
    private String generateUniqueOrderNumber() {
        Random random = new Random();
        String orderNumber;
        int attempts = 0;

        do {
            // 6자리 숫자 생성
            int number = OrderConstants.OrderNumber.MIN_VALUE + 
                        random.nextInt(OrderConstants.OrderNumber.RANGE);
            orderNumber = String.valueOf(number);
            attempts++;

            // 최대 시도 횟수 초과 시 예외 발생
            if (attempts > OrderConstants.OrderNumber.MAX_GENERATION_ATTEMPTS) {
                throw new RuntimeException(OrderConstants.Messages.ORDER_NUMBER_GENERATION_FAILED);
            }

        } while (orderRepository.existsByOrderNumber(orderNumber));

        log.info("Generated unique order number: {} (attempts: {})", orderNumber, attempts);
        return orderNumber;
    }


}
