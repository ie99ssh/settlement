package com.sandbox.settlement.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sandbox.settlement.common.domain.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**--------------------------------------------------------------------
 * ■로그인 실패 이력 페이지 모델 ■sangheon
 --------------------------------------------------------------------**/
@Data
@EqualsAndHashCode(callSuper=false)
public class AdminLoginFailHistDto extends BaseModel {
    //------------------------------------------------------------------
    // 엔티티 베이스 멤버 변수(엔티티 컬럼 정렬 순서 순)
    //------------------------------------------------------------------
    private String  adminId;     // 관리자 아이디
    private int     errCode;     // 오류 코드
    private String  errMsg;      // 오류 메시지
    private String  ipAddr;      // 아이피 주소

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    private LocalDateTime regDate;     // 등록 일시

    //------------------------------------------------------------------
    // 엔터티 외 파라메터 변수
    //------------------------------------------------------------------
    private String  strFromYMD;    // 로그 검색용 (시작 일자)
    private String  strToYMD;      // 로그 검색용 (종료 일자)

}
