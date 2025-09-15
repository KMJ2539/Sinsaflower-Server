package com.sinsaflower.server.domain.order.util;

import com.sinsaflower.server.domain.order.constants.OrderConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 페이징 관련 유틸리티 클래스
 * Controller에서 반복되는 페이징 로직을 중앙화
 */
public final class PagingUtils {

    private PagingUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 기본 페이징 객체 생성
     * 
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param sort 정렬 필드
     * @param direction 정렬 방향 (asc/desc)
     * @return Pageable 객체
     */
    public static Pageable createPageable(int page, int size, String sort, String direction) {
        // 페이지 크기 제한
        int validatedSize = Math.min(size, OrderConstants.Pagination.MAX_SIZE);
        
        // 정렬 방향 결정
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
            
        return PageRequest.of(page, validatedSize, Sort.by(sortDirection, sort));
    }

    /**
     * 기본값을 사용한 페이징 객체 생성
     * 
     * @return 기본 설정의 Pageable 객체
     */
    public static Pageable createDefaultPageable() {
        return createPageable(
            OrderConstants.Pagination.DEFAULT_PAGE,
            OrderConstants.Pagination.DEFAULT_SIZE,
            OrderConstants.Pagination.DEFAULT_SORT,
            OrderConstants.Pagination.DEFAULT_DIRECTION
        );
    }

    /**
     * 정렬만 지정한 페이징 객체 생성
     * 
     * @param sort 정렬 필드
     * @param direction 정렬 방향
     * @return Pageable 객체
     */
    public static Pageable createPageableWithSort(String sort, String direction) {
        return createPageable(
            OrderConstants.Pagination.DEFAULT_PAGE,
            OrderConstants.Pagination.DEFAULT_SIZE,
            sort,
            direction
        );
    }

    /**
     * 페이지 번호 유효성 검증
     * 
     * @param page 페이지 번호
     * @return 유효한 페이지 번호 (음수면 0으로 조정)
     */
    public static int validatePageNumber(int page) {
        return Math.max(0, page);
    }

    /**
     * 페이지 크기 유효성 검증
     * 
     * @param size 페이지 크기
     * @return 유효한 페이지 크기 (1~MAX_SIZE 범위로 조정)
     */
    public static int validatePageSize(int size) {
        if (size <= 0) {
            return OrderConstants.Pagination.DEFAULT_SIZE;
        }
        return Math.min(size, OrderConstants.Pagination.MAX_SIZE);
    }

    /**
     * 정렬 필드 유효성 검증
     * 허용된 정렬 필드가 아니면 기본값 반환
     * 
     * @param sort 정렬 필드
     * @return 유효한 정렬 필드
     */
    public static String validateSortField(String sort) {
        if (sort == null || sort.trim().isEmpty()) {
            return OrderConstants.Pagination.DEFAULT_SORT;
        }
        
        // 허용된 정렬 필드 목록
        String[] allowedSortFields = {
            "id", "createdAt", "updatedAt", "deliveryDate", 
            "orderStatus", "payment", "shopName", "productName"
        };
        
        for (String allowedField : allowedSortFields) {
            if (allowedField.equals(sort)) {
                return sort;
            }
        }
        
        // 허용되지 않은 필드면 기본값 반환
        return OrderConstants.Pagination.DEFAULT_SORT;
    }
}
