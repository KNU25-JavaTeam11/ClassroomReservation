package org.javateam11.ClassroomReservation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.*;
import org.javateam11.ClassroomReservation.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Spring 백엔드와의 HTTP 통신을 담당하는 기본 API 서비스 클래스
 * 멀티스레드 환경에서 안전하게 동작하도록 설계됨
 */
public class ApiService {
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);

    private static final String BASE_URL = "http://localhost:8080";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public ApiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * 에러 응답에서 에러 메시지를 추출하는 헬퍼 메서드
     */
    private String extractErrorMessage(Response response) {
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
     * GET 요청을 비동기로 실행
     */
    public <T> CompletableFuture<T> getAsync(String endpoint, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Request request = new Request.Builder()
                        .url(BASE_URL + endpoint)
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorMessage = extractErrorMessage(response);
                        throw new RuntimeException(errorMessage);
                    }

                    String responseBody = response.body().string();
                    logger.debug("GET {} 응답: {}", endpoint, responseBody);

                    return objectMapper.readValue(responseBody, responseType);
                }
            } catch (IOException e) {
                logger.error("GET {} 요청 중 오류 발생", endpoint, e);
                throw new RuntimeException("API 요청 중 오류 발생", e);
            }
        });
    }

    /**
     * GET 요청을 비동기로 실행 (TypeReference 사용)
     */
    public <T> CompletableFuture<T> getAsync(String endpoint, TypeReference<T> typeReference) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Request request = new Request.Builder()
                        .url(BASE_URL + endpoint)
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorMessage = extractErrorMessage(response);
                        throw new RuntimeException(errorMessage);
                    }

                    String responseBody = response.body().string();
                    logger.debug("GET {} 응답: {}", endpoint, responseBody);

                    return objectMapper.readValue(responseBody, typeReference);
                }
            } catch (IOException e) {
                logger.error("GET {} 요청 중 오류 발생", endpoint, e);
                throw new RuntimeException("API 요청 중 오류 발생", e);
            }
        });
    }

    /**
     * 인증된 GET 요청을 비동기로 실행
     */
    public <T> CompletableFuture<T> getAuthenticatedAsync(String endpoint, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TokenManager tokenManager = TokenManager.getInstance();
                if (!tokenManager.isAuthenticated()) {
                    throw new RuntimeException("인증되지 않은 상태입니다. 로그인이 필요합니다.");
                }

                Request request = new Request.Builder()
                        .url(BASE_URL + endpoint)
                        .addHeader("Authorization", tokenManager.getAuthorizationHeader())
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        if (response.code() == 401) {
                            throw new RuntimeException("인증이 만료되었습니다. 다시 로그인해주세요.");
                        }
                        String errorMessage = extractErrorMessage(response);
                        throw new RuntimeException(errorMessage);
                    }

                    String responseBody = response.body().string();
                    logger.debug("GET {} 응답: {}", endpoint, responseBody);

                    return objectMapper.readValue(responseBody, responseType);
                }
            } catch (IOException e) {
                logger.error("GET {} 요청 중 오류 발생", endpoint, e);
                throw new RuntimeException("API 요청 중 오류 발생", e);
            }
        });
    }

    /**
     * POST 요청을 비동기로 실행
     */
    public <T> CompletableFuture<T> postAsync(String endpoint, Object requestBody, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonBody = objectMapper.writeValueAsString(requestBody);
                logger.debug("POST {} 요청: {}", endpoint, jsonBody);

                Request request = new Request.Builder()
                        .url(BASE_URL + endpoint)
                        .post(RequestBody.create(jsonBody, JSON))
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorMessage = extractErrorMessage(response);
                        throw new RuntimeException(errorMessage);
                    }

                    String responseBody = response.body().string();
                    logger.debug("POST {} 응답: {}", endpoint, responseBody);

                    return objectMapper.readValue(responseBody, responseType);
                }
            } catch (IOException e) {
                logger.error("POST {} 요청 중 오류 발생", endpoint, e);
                throw new RuntimeException("API 요청 중 오류 발생", e);
            }
        });
    }

    /**
     * 인증된 POST 요청을 비동기로 실행
     */
    public <T> CompletableFuture<T> postAuthenticatedAsync(String endpoint, Object requestBody, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TokenManager tokenManager = TokenManager.getInstance();
                if (!tokenManager.isAuthenticated()) {
                    throw new RuntimeException("인증되지 않은 상태입니다. 로그인이 필요합니다.");
                }

                String jsonBody = objectMapper.writeValueAsString(requestBody);
                logger.debug("POST {} 요청: {}", endpoint, jsonBody);

                Request request = new Request.Builder()
                        .url(BASE_URL + endpoint)
                        .addHeader("Authorization", tokenManager.getAuthorizationHeader())
                        .post(RequestBody.create(jsonBody, JSON))
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        if (response.code() == 401) {
                            throw new RuntimeException("인증이 만료되었습니다. 다시 로그인해주세요.");
                        }
                        String errorMessage = extractErrorMessage(response);
                        throw new RuntimeException(errorMessage);
                    }

                    String responseBody = response.body().string();
                    logger.debug("POST {} 응답: {}", endpoint, responseBody);

                    return objectMapper.readValue(responseBody, responseType);
                }
            } catch (IOException e) {
                logger.error("POST {} 요청 중 오류 발생", endpoint, e);
                throw new RuntimeException("API 요청 중 오류 발생", e);
            }
        });
    }

    /**
     * PUT 요청을 비동기로 실행
     */
    public <T> CompletableFuture<T> putAsync(String endpoint, Object requestBody, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonBody = objectMapper.writeValueAsString(requestBody);
                logger.debug("PUT {} 요청: {}", endpoint, jsonBody);

                Request request = new Request.Builder()
                        .url(BASE_URL + endpoint)
                        .put(RequestBody.create(jsonBody, JSON))
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorMessage = extractErrorMessage(response);
                        throw new RuntimeException(errorMessage);
                    }

                    String responseBody = response.body().string();
                    logger.debug("PUT {} 응답: {}", endpoint, responseBody);

                    return objectMapper.readValue(responseBody, responseType);
                }
            } catch (IOException e) {
                logger.error("PUT {} 요청 중 오류 발생", endpoint, e);
                throw new RuntimeException("API 요청 중 오류 발생", e);
            }
        });
    }

    /**
     * 인증된 PUT 요청을 비동기로 실행
     */
    public <T> CompletableFuture<T> putAuthenticatedAsync(String endpoint, Object requestBody, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TokenManager tokenManager = TokenManager.getInstance();
                if (!tokenManager.isAuthenticated()) {
                    throw new RuntimeException("인증되지 않은 상태입니다. 로그인이 필요합니다.");
                }

                String jsonBody = objectMapper.writeValueAsString(requestBody);
                logger.debug("PUT {} 요청: {}", endpoint, jsonBody);

                Request request = new Request.Builder()
                        .url(BASE_URL + endpoint)
                        .addHeader("Authorization", tokenManager.getAuthorizationHeader())
                        .put(RequestBody.create(jsonBody, JSON))
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        if (response.code() == 401) {
                            throw new RuntimeException("인증이 만료되었습니다. 다시 로그인해주세요.");
                        }
                        String errorMessage = extractErrorMessage(response);
                        throw new RuntimeException(errorMessage);
                    }

                    String responseBody = response.body().string();
                    logger.debug("PUT {} 응답: {}", endpoint, responseBody);

                    return objectMapper.readValue(responseBody, responseType);
                }
            } catch (IOException e) {
                logger.error("PUT {} 요청 중 오류 발생", endpoint, e);
                throw new RuntimeException("API 요청 중 오류 발생", e);
            }
        });
    }

    /**
     * DELETE 요청을 비동기로 실행
     */
    public CompletableFuture<Void> deleteAsync(String endpoint) {
        return CompletableFuture.runAsync(() -> {
            try {
                Request request = new Request.Builder()
                        .url(BASE_URL + endpoint)
                        .delete()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorMessage = extractErrorMessage(response);
                        throw new RuntimeException(errorMessage);
                    }
                    logger.debug("DELETE {} 성공", endpoint);
                }
            } catch (IOException e) {
                logger.error("DELETE {} 요청 중 오류 발생", endpoint, e);
                throw new RuntimeException("API 요청 중 오류 발생", e);
            }
        });
    }

    /**
     * 인증된 DELETE 요청을 비동기로 실행
     */
    public CompletableFuture<Void> deleteAuthenticatedAsync(String endpoint) {
        return CompletableFuture.runAsync(() -> {
            try {
                TokenManager tokenManager = TokenManager.getInstance();
                if (!tokenManager.isAuthenticated()) {
                    throw new RuntimeException("인증되지 않은 상태입니다. 로그인이 필요합니다.");
                }

                Request request = new Request.Builder()
                        .url(BASE_URL + endpoint)
                        .addHeader("Authorization", tokenManager.getAuthorizationHeader())
                        .delete()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        if (response.code() == 401) {
                            throw new RuntimeException("인증이 만료되었습니다. 다시 로그인해주세요.");
                        }
                        String errorMessage = extractErrorMessage(response);
                        throw new RuntimeException(errorMessage);
                    }
                    logger.debug("DELETE {} 성공", endpoint);
                }
            } catch (IOException e) {
                logger.error("DELETE {} 요청 중 오류 발생", endpoint, e);
                throw new RuntimeException("API 요청 중 오류 발생", e);
            }
        });
    }
}