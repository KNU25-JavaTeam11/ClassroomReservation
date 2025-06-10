package org.javateam11.ClassroomReservation.util;

import okhttp3.Response;
import org.javateam11.ClassroomReservation.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 에러 메시지 처리를 위한 유틸리티 클래스
 * 중복된 에러 메시지 추출 로직을 통합하여 코드 중복을 제거합니다.
 */
public class ErrorMessageUtils {
    private static final Logger logger = LoggerFactory.getLogger(ErrorMessageUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 예외에서 깨끗한 에러 메시지를 추출하는 메서드
     * 
     * @param throwable 원본 예외
     * @return 사용자에게 표시할 깨끗한 에러 메시지
     */
    public static String extractCleanErrorMessage(Throwable throwable) {
        if (throwable == null) {
            return "알 수 없는 오류가 발생했습니다";
        }

        Throwable current = throwable;

        // 중첩된 예외를 풀어서 원본 메시지를 찾음
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }

        String message = current.getMessage();
        if (message != null && !message.trim().isEmpty()) {
            if (message.startsWith("java.lang.RuntimeException: ")) {
                message = message.substring("java.lang.RuntimeException: ".length());
            }
            return message;
        }

        // 메시지가 없으면 기본 메시지 반환
        return "요청 처리 중 오류가 발생했습니다";
    }

    /**
     * HTTP 응답에서 에러 메시지를 추출하는 메서드
     * 
     * @param response HTTP 응답 객체
     * @return 추출된 에러 메시지
     */
    public static String extractApiErrorMessage(Response response) {
        if (response == null) {
            return "API 응답을 받을 수 없습니다";
        }

        try {
            if (response.body() != null) {
                String errorBody = response.body().string();
                logger.debug("에러 응답 body: {}", errorBody);

                // JSON 형식의 에러 응답을 파싱
                try {
                    ErrorResponse errorResponse = objectMapper.readValue(errorBody, ErrorResponse.class);
                    if (errorResponse.getError() != null && !errorResponse.getError().trim().isEmpty()) {
                        return errorResponse.getError();
                    }
                } catch (Exception e) {
                    logger.debug("에러 응답 JSON 파싱 실패, 원본 응답 사용: {}", errorBody);
                    // JSON 파싱에 실패하면 원본 응답을 반환
                    return errorBody;
                }
            }
        } catch (Exception e) {
            logger.debug("에러 응답 읽기 실패", e);
        }

        // 기본 에러 메시지
        return "API 요청 실패: " + response.code() + " " + response.message();
    }

    /**
     * 인증 관련 에러인지 확인하는 메서드
     * 
     * @param throwable 확인할 예외
     * @return 인증 에러 여부
     */
    public static boolean isAuthenticationError(Throwable throwable) {
        String message = extractCleanErrorMessage(throwable);
        return message.contains("401") ||
                message.contains("Unauthorized") ||
                message.contains("인증이 만료되었습니다") ||
                message.contains("인증되지 않은 상태입니다");
    }

    /**
     * 네트워크 관련 에러인지 확인하는 메서드
     * 
     * @param throwable 확인할 예외
     * @return 네트워크 에러 여부
     */
    public static boolean isNetworkError(Throwable throwable) {
        String message = extractCleanErrorMessage(throwable);
        return message.contains("Connection") ||
                message.contains("timeout") ||
                message.contains("ConnectException") ||
                message.contains("SocketTimeoutException");
    }

    /**
     * 에러 메시지를 사용자 친화적으로 변환하는 메서드
     * 
     * @param throwable 원본 예외
     * @return 사용자 친화적인 메시지
     */
    public static String getUserFriendlyMessage(Throwable throwable) {
        if (isAuthenticationError(throwable)) {
            return "로그인이 필요하거나 세션이 만료되었습니다. 다시 로그인해주세요.";
        } else if (isNetworkError(throwable)) {
            return "서버에 연결할 수 없습니다. 네트워크 상태를 확인하고 잠시 후 다시 시도해주세요.";
        } else {
            return extractCleanErrorMessage(throwable);
        }
    }
}