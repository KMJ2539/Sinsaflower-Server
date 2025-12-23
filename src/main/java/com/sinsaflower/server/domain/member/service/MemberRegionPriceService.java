package com.sinsaflower.server.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinsaflower.server.domain.member.dto.*;
import com.sinsaflower.server.domain.member.entity.*;
import com.sinsaflower.server.domain.member.repository.*;
import com.sinsaflower.server.domain.product.entity.MemberProductPrice;
import com.sinsaflower.server.domain.product.repository.MemberProductPriceRepository;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class MemberRegionPriceService {

    private final MemberRepository memberRepository;
    private final MemberActivityRegionRepository regionRepository;
    private final MemberProductPriceRepository priceRepository;

    public void save(Long memberId, List<MemberRegionPriceRequest> requests) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        /* 1️⃣ 기존 데이터 비활성화 */
        member.getActivityRegions()
                .forEach(r -> r.setIsActive(false));

        member.getProductPrices()
                .forEach(p -> p.setIsAvailable(false));

        /* 2️⃣ 신규 데이터 저장 */
        for (MemberRegionPriceRequest regionReq : requests) {

            // 활동 지역
            MemberActivityRegion region = MemberActivityRegion.builder()
                    .member(member)
                    .sido(regionReq.getSido())
                    .sigungu(regionReq.getSigungu())
                    .isActive(Boolean.TRUE.equals(regionReq.getHandled()))
                    .build();

            regionRepository.save(region);

            // 상품 가격 (UPSERT)
            for (MemberProductPriceRequest priceReq : regionReq.getPrices()) {

                MemberProductPrice price = priceRepository
                        .findByMemberIdAndSidoAndSigunguAndCategoryName(
                                memberId,
                                regionReq.getSido(),
                                regionReq.getSigungu(),
                                priceReq.getCategoryName()
                        )
                        .orElseGet(() -> MemberProductPrice.builder()
                                .member(member)
                                .sido(regionReq.getSido())
                                .sigungu(regionReq.getSigungu())
                                .categoryName(priceReq.getCategoryName())
                                .build()
                        );

                price.setPrice(BigDecimal.valueOf(priceReq.getPrice()));
                price.setIsAvailable(
                        Boolean.TRUE.equals(regionReq.getHandled())
                                && Boolean.TRUE.equals(priceReq.getIsAvailable())
                );

                priceRepository.save(price);
            }
        }
    }
}
