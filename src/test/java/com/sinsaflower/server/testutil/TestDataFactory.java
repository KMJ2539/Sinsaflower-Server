package com.sinsaflower.server.testutil;

import com.sinsaflower.server.domain.member.dto.MemberSignupRequest;
import com.sinsaflower.server.domain.member.entity.Member;
import com.sinsaflower.server.domain.member.entity.MemberBusinessProfile;

/**
 * 테스트 데이터 생성을 위한 팩토리 클래스
 * 테스트에서 공통으로 사용되는 데이터 생성 로직을 중앙화
 */
public class TestDataFactory {

    /**
     * 기본 회원 가입 요청 생성 (최소한의 필수 정보만)
     */
    public static MemberSignupRequest createBasicSignupRequest() {
        return createSignupRequest("testuser", "테스트화환", "123-45-67890");
    }

    /**
     * 커스텀 회원 가입 요청 생성
     */
    public static MemberSignupRequest createSignupRequest(String loginId, String name, String businessNumber) {
        MemberSignupRequest request = new MemberSignupRequest();
        request.setLoginId(loginId);
        request.setPassword("password123");
        request.setName(name);
        request.setNickname(name.substring(0, Math.min(name.length(), 4))); // 이름에서 앞 4글자
        request.setMobile("010-1234-5678");

        // 사업자 프로필 (필수 정보만)
        MemberSignupRequest.BusinessProfileRequest businessProfile = new MemberSignupRequest.BusinessProfileRequest();
        businessProfile.setBusinessNumber(businessNumber);
        businessProfile.setCorpName(name + " 주식회사");
        businessProfile.setCeoName("김대표");
        businessProfile.setBusinessType("농업");
        businessProfile.setBusinessItem("화훼재배업");
        businessProfile.setCompanyAddress("서울 강남구 테스트로 123");

        // 간단한 주소 정보
        MemberSignupRequest.AddressRequest officeAddress = new MemberSignupRequest.AddressRequest();
        officeAddress.setSido("서울");
        officeAddress.setSigungu("강남구");
        officeAddress.setDetail("테스트로 123");
        officeAddress.setZipcode("12345");

        businessProfile.setOfficeAddress(officeAddress);
        request.setBusinessProfile(businessProfile);

        return request;
    }

    /**
     * 통합 테스트용 회원 가입 요청 생성 (고유한 ID로)
     */
    public static MemberSignupRequest createIntegrationTestSignupRequest() {
        String uniqueId = "integration_" + System.currentTimeMillis();
        // 사업자등록번호는 12자리 제한에 맞게 생성 (XXX-XX-XXXXX 형식, 총 12글자)
        long timestamp = System.currentTimeMillis();
        String businessNumber = String.format("999-88-%05d", timestamp % 100000);
        return createSignupRequest(uniqueId, "통합테스트화환", businessNumber);
    }

    /**
     * Member 엔티티 생성
     */
    public static Member createMemberEntity(String loginId, String name) {
        return Member.builder()
                .id(1L)
                .loginId(loginId)
                .password("encodedPassword")
                .name(name)
                .nickname(name.substring(0, Math.min(name.length(), 4)))
                .mobile("010-1234-5678")
                .status(Member.MemberStatus.PENDING)
                .build();
    }

    /**
     * MemberBusinessProfile 엔티티 생성
     */
    public static MemberBusinessProfile createBusinessProfileEntity(String businessNumber, String corpName) {
        return MemberBusinessProfile.builder()
                .id(1L)
                .businessNumber(businessNumber)
                .corpName(corpName)
                .ceoName("김대표")
                .businessType("농업")
                .businessItem("화훼재배업")
                .companyAddress("서울 강남구 테스트로 123")
                .approvalStatus(MemberBusinessProfile.ApprovalStatus.PENDING)
                .build();
    }

    /**
     * 중복 검증용 회원 가입 요청 생성
     */
    public static MemberSignupRequest createDuplicateTestRequest(String existingLoginId, String existingBusinessNumber) {
        return createSignupRequest(existingLoginId, "중복테스트화환", existingBusinessNumber);
    }
}
