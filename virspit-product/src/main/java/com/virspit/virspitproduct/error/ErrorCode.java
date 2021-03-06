package com.virspit.virspitproduct.error;

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
    FILE_NOT_SUPPORT(400, "C010", "지원하지 않는 파일입니다"),

    // Sports
    ICON_FILE_NOT_FOUND(400, "S001", "업로드된 아이콘 파일이 없습니다"),
    DUPLICATED_SPORTS_NAME(400, "S002", "이미 등록된 종목입니다"),
    TEAM_PLAYER_EXIST(400, "S003", "종목에 소속된 팀/플레이어가 존재합니다"),

    // TeamPlayer
    PRODUCT_EXIST(400, "T001", "팀/플레이어에 관련된 상품이 존재합니다"),

    // Product
    NFT_CONTRACT_TRANSACTION_FAILED(500, "P001", "컨트랙트 배포 실패. 관리자에게 문의하세요");


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
