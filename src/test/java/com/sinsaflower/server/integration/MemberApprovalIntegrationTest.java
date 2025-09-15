package com.sinsaflower.server.integration;

import com.sinsaflower.server.domain.admin.service.AdminService;
import com.sinsaflower.server.domain.member.dto.MemberResponse;
import com.sinsaflower.server.domain.member.dto.MemberSignupRequest;
import com.sinsaflower.server.domain.member.entity.Member;
import com.sinsaflower.server.domain.member.entity.MemberBusinessProfile;
import com.sinsaflower.server.domain.member.repository.MemberRepository;
import com.sinsaflower.server.domain.member.repository.MemberBusinessProfileRepository;
import com.sinsaflower.server.domain.member.service.MemberService;
import com.sinsaflower.server.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "logging.level.org.springframework.web=DEBUG"
})
@Transactional
@DisplayName("회원 승인 통합 테스트 - 개선된 버전")
class MemberApprovalIntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberBusinessProfileRepository businessProfileRepository;

    private String testLoginId;
    private String testBusinessNumber;

    @BeforeEach
    void setUp() {
        // 각 테스트마다 고유한 식별자 생성하여 격리 보장
        long timestamp = System.currentTimeMillis();
        testLoginId = "test_" + timestamp;
        // 사업자등록번호는 12자리 제한에 맞게 생성 (XXX-XX-XXXXX 형식, 총 12글자)
        testBusinessNumber = String.format("999-88-%05d", timestamp % 100000);
    }

    @Test
    @DisplayName("통합 테스트: 회원 가입 → 관리자 승인 → 상태 변경 전체 플로우")
    void memberSignupAndApprovalFlow() {
        // given - 회원 가입 요청 데이터
        MemberSignupRequest signupRequest = TestDataFactory.createSignupRequest(testLoginId, "통합테스트화환", testBusinessNumber);

        // when - 1단계: 회원 가입
        MemberResponse memberResponse = memberService.signUp(signupRequest);

        // then - 회원 가입 검증
        assertThat(memberResponse).isNotNull();
        assertThat(memberResponse.getLoginId()).isEqualTo(testLoginId);
        assertThat(memberResponse.getStatus()).isEqualTo("승인대기");

        // given - 2단계: 관리자 승인
        Long memberId = memberResponse.getId();

        // when - 관리자가 파트너 승인
        MemberResponse approvedMember = adminService.approveMember(memberId);

        // then - 승인 후 상태 검증
        assertThat(approvedMember).isNotNull();
        assertThat(approvedMember.getLoginId()).isEqualTo(testLoginId);

        // DB에서 실제 상태 확인
        Member memberFromDB = memberRepository.findById(memberId).orElse(null);
        assertThat(memberFromDB).isNotNull();
        assertThat(memberFromDB.getStatus()).isEqualTo(Member.MemberStatus.ACTIVE);

        // 사업자 프로필 승인 상태도 확인
        MemberBusinessProfile profileFromDB = businessProfileRepository.findByMemberId(memberId).orElse(null);
        assertThat(profileFromDB).isNotNull();
        assertThat(profileFromDB.getApprovalStatus()).isEqualTo(MemberBusinessProfile.ApprovalStatus.APPROVED);
    }

    @Test
    @DisplayName("통합 테스트: 회원 가입 → 관리자 거부 → 상태 변경 전체 플로우")
    void memberSignupAndRejectionFlow() {
        // given - 회원 가입 요청 데이터
        MemberSignupRequest signupRequest = TestDataFactory.createSignupRequest(testLoginId, "통합테스트화환", testBusinessNumber);

        // when - 1단계: 회원 가입
        MemberResponse memberResponse = memberService.signUp(signupRequest);

        // then - 회원 가입 검증
        assertThat(memberResponse).isNotNull();
        assertThat(memberResponse.getLoginId()).isEqualTo(testLoginId);
        assertThat(memberResponse.getStatus()).isEqualTo("승인대기");

        // given - 2단계: 관리자 거부
        Long memberId = memberResponse.getId();
        String rejectionReason = "서류 미비";

        // when - 관리자가 파트너 거부
        MemberResponse rejectedMember = adminService.rejectMember(memberId, rejectionReason);

        // then - 거부 후 상태 검증
        assertThat(rejectedMember).isNotNull();
        assertThat(rejectedMember.getLoginId()).isEqualTo(testLoginId);

        // DB에서 실제 상태 확인 (거부 후에도 PENDING 상태 유지)
        Member memberFromDB = memberRepository.findById(memberId).orElse(null);
        assertThat(memberFromDB).isNotNull();
        assertThat(memberFromDB.getStatus()).isEqualTo(Member.MemberStatus.PENDING);

        // 사업자 프로필 거부 상태 확인
        MemberBusinessProfile profileFromDB = businessProfileRepository.findByMemberId(memberId).orElse(null);
        assertThat(profileFromDB).isNotNull();
        assertThat(profileFromDB.getApprovalStatus()).isEqualTo(MemberBusinessProfile.ApprovalStatus.REJECTED);
        assertThat(profileFromDB.getRejectionReason()).isEqualTo(rejectionReason);
    }

    @Test
    @DisplayName("통합 테스트: 중복 로그인 ID 검증")
    void duplicateLoginIdValidation() {
        // given - 첫 번째 회원 가입
        MemberSignupRequest firstRequest = TestDataFactory.createSignupRequest(testLoginId, "첫번째화환", testBusinessNumber);
        memberService.signUp(firstRequest);

        // when & then - 동일한 로그인 ID로 재가입 시도 (다른 사업자번호 사용)
        long anotherTimestamp = System.currentTimeMillis();
        String anotherBusinessNumber = String.format("888-77-%05d", anotherTimestamp % 100000);
        MemberSignupRequest duplicateRequest = TestDataFactory.createSignupRequest(testLoginId, "두번째화환", anotherBusinessNumber);
        
        assertThatThrownBy(() -> memberService.signUp(duplicateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 사용 중인 로그인 ID입니다");
    }

    @Test
    @DisplayName("통합 테스트: 중복 사업자등록번호 검증")
    void duplicateBusinessNumberValidation() {
        // given - 첫 번째 회원 가입
        MemberSignupRequest firstRequest = TestDataFactory.createSignupRequest(testLoginId, "첫번째화환", testBusinessNumber);
        memberService.signUp(firstRequest);

        // when & then - 동일한 사업자등록번호로 재가입 시도 (다른 로그인ID 사용)
        String anotherLoginId = "another_" + System.currentTimeMillis();
        MemberSignupRequest duplicateRequest = TestDataFactory.createSignupRequest(anotherLoginId, "두번째화환", testBusinessNumber);
        
        assertThatThrownBy(() -> memberService.signUp(duplicateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 등록된 사업자등록번호입니다");
    }

    @Test
    @DisplayName("통합 테스트: 중복 확인 API 동작")
    void duplicateCheckIntegration() {
        // given - 회원 가입
        MemberSignupRequest signupRequest = TestDataFactory.createSignupRequest(testLoginId, "중복확인화환", testBusinessNumber);
        memberService.signUp(signupRequest);

        // when & then - 로그인 ID 중복 확인
        boolean isLoginIdDuplicate = memberService.isLoginIdDuplicate(testLoginId);
        assertThat(isLoginIdDuplicate).isTrue();

        // when & then - 사업자등록번호 중복 확인
        boolean isBusinessNumberDuplicate = memberService.isBusinessNumberDuplicate(testBusinessNumber);
        assertThat(isBusinessNumberDuplicate).isTrue();

        // when & then - 존재하지 않는 데이터 확인
        boolean isNewIdDuplicate = memberService.isLoginIdDuplicate("nonexistent_user_" + System.currentTimeMillis());
        assertThat(isNewIdDuplicate).isFalse();

        boolean isNewBusinessNumberDuplicate = memberService.isBusinessNumberDuplicate("000-00-00000");
        assertThat(isNewBusinessNumberDuplicate).isFalse();
    }

    @Test
    @DisplayName("통합 테스트: 회원 정보 조회")
    void getMemberInfoIntegration() {
        // given - 회원 가입
        MemberSignupRequest signupRequest = TestDataFactory.createSignupRequest(testLoginId, "정보조회화환", testBusinessNumber);
        MemberResponse signedUpMember = memberService.signUp(signupRequest);

        // when - 회원 정보 조회
        MemberResponse memberInfo = memberService.getMemberInfo(signedUpMember.getId());

        // then - 조회된 정보 검증
        assertThat(memberInfo).isNotNull();
        assertThat(memberInfo.getId()).isEqualTo(signedUpMember.getId());
        assertThat(memberInfo.getLoginId()).isEqualTo(testLoginId);
        assertThat(memberInfo.getName()).isEqualTo("정보조회화환");
        assertThat(memberInfo.getStatus()).isEqualTo("승인대기");
    }

    @Test
    @DisplayName("통합 테스트: 존재하지 않는 회원 승인 시도")
    void approveNonExistentMember() {
        // given - 존재하지 않는 회원 ID
        Long nonExistentMemberId = 99999L;

        // when & then - 존재하지 않는 회원 승인 시도
        assertThatThrownBy(() -> adminService.approveMember(nonExistentMemberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다");
    }
} 