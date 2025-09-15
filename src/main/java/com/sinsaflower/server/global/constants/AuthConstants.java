package com.sinsaflower.server.global.constants;

/**
 * 인증 관련 상수 클래스
 * JWT 토큰, 쿠키, 메시지 등의 상수를 관리합니다.
 */
public final class AuthConstants {

    private AuthConstants() {
        // Prevent instantiation
    }

    /**
     * JWT 토큰 관련 상수
     */
    public static class Token {
        public static final String ACCESS_TOKEN_COOKIE = "accessToken";
        public static final String REFRESH_TOKEN_COOKIE = "refreshToken";
        public static final int ACCESS_TOKEN_EXPIRES_SECONDS = 24 * 60 * 60; // 24시간
        public static final int REFRESH_TOKEN_EXPIRES_SECONDS = 7 * 24 * 60 * 60; // 7일
    }

    /**
     * 쿠키 관련 상수
     */
    public static class Cookie {
        public static final String IS_LOGGED_IN = "isLoggedIn";
        public static final String USER_TYPE = "userType";
        public static final String USERNAME = "username";
        public static final String COOKIE_PATH = "/";
        public static final String SAME_SITE = "Lax";
        public static final boolean HTTP_ONLY_TOKEN = true; // 토큰은 HttpOnly
        public static final boolean HTTP_ONLY_STATUS = false; // 상태는 JS 접근 가능
    }

    /**
     * 사용자 타입 상수
     */
    public static class UserType {
        public static final String ADMIN = "ADMIN";
        public static final String PARTNER = "PARTNER";
    }

    /**
     * 인증 메시지 상수
     */
    public static class Messages {
        public static final String LOGIN_SUCCESS = "로그인이 성공적으로 완료되었습니다.";
        public static final String LOGOUT_SUCCESS = "로그아웃되었습니다.";
        public static final String TOKEN_REFRESH_SUCCESS = "토큰 갱신이 완료되었습니다.";
        public static final String USER_INFO_SUCCESS = "사용자 정보 조회가 성공적으로 완료되었습니다.";
        public static final String TOKEN_VALID = "토큰이 유효합니다.";
        
        // 오류 메시지
        public static final String UNAUTHORIZED_USER = "인증되지 않은 사용자입니다.";
        public static final String INVALID_TOKEN = "유효하지 않은 토큰입니다.";
        public static final String TOKEN_REFRESH_FAILED = "토큰 갱신에 실패했습니다.";
    }

    /**
     * HTTP 응답 코드 관련 상수
     */
    public static class ResponseCode {
        public static final int SUCCESS = 200;
        public static final int CREATED = 201;
        public static final int UNAUTHORIZED = 401;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }
}
