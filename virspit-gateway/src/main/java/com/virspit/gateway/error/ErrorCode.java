package com.virspit.gateway.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "C001", "입력값이 올바르지 않습니다"),
    METHOD_NOT_ALLOWED(405, "C002", "허용되지 않는 HTTP Method 입니다"),
    ENTITY_NOT_FOUND(400, "C003", " 데이터가 존재하지 않습니다"),
    INTERNAL_SERVER_ERROR(500, "C004", "서버 내부 오류. 이슈를 관리자에게 전달해주세요"),
    INVALID_TYPE_VALUE(400, "C005", "타입이 일치하지 않습니다"),
    ACCESS_DENIED(403, "C006", "접근 거부"),
    NOT_FOUND(404, "C007", "요청을 찾을 수 없습니다"),
    INVALID_CONTENT_TYPE(415, "C008", "Content Type이 올바르지 않습니다"),
    FILE_NOT_FOUND(400, "C009", "파일을 찾을 수 없습니다"),

    // TODO: 각 도메인에서 발생할 오류에 대한 코드 추가

    TOKEN_NOT_VALID(500, "G001", "권한 없는 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(501, "G002", "만료된 accessToken 입니다.")
    ;


    @Getter
    private final int status;
    @Getter
    private final String code;
    @Getter
    private final String message;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }
}
