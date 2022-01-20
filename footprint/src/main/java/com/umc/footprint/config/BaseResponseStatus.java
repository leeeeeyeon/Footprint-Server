package com.umc.footprint.config;

import lombok.Getter;

/**
 * [ 1000단위 ]
 *  1000 : 요청 성공
 *  2 : Request 오류
 *  3 : Reponse 오류
 *  4 : DB, Server 오류
 *
 * [ 100단위 ]
 *  0 : 공통 오류
 *  1 : users 오류
 *  2 : walks 오류
 *
 * [10단위]
 *  0~19 : Common
 *  20~39 : GET
 *  40~59 : POST
 *  60~79 : PATCH
 *  80~99 : else
 */

@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    /**
     * 2000 : Request 오류
     */
    INVALID_USERIDX(false,2100,"잘못된 유저 인덱스입니다."),
    EXIST_USER_ERROR(false, 2140,"이미 존재하는 유저입니다."),


    /**
     * 3000 : Response 오류
     */

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
