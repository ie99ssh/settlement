package com.sandbox.settlement.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sandbox.settlement.common.domain.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**--------------------------------------------------------------------
 * ■로그 등록 요청 모델 ■sangheon
 --------------------------------------------------------------------**/
@EqualsAndHashCode(callSuper = true)
@Data
public class AdminMenuAccessLogDto extends BaseModel {
    //------------------------------------------------------------------
    // 엔티티 베이스 멤버 변수(엔티티 컬럼 정렬 순서 순)
    //------------------------------------------------------------------
    private Integer adminNo;       // 관리자 번호
    private Integer menuNo;        // 메뉴 번호
    private String  menuLink;      // 메뉴 링크
    private String  methodName;    // 메소드 명

    private String  logDesc;       // 로그 설명
    private String  ipAddr;        // 아이피 주소

    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    private LocalDateTime regDate;

    private String  menuName;      // 메뉴 명
    private String  adminId;       // 관리자 아이디
    private String  regId  ;       // 등록자 아이디

    //------------------------------------------------------------------
    // 엔티티 외 파라메터 변수
    //------------------------------------------------------------------
    private String  strFromYMD;    // 로그 검색용 (시작 일자)
    private String  strToYMD;      // 로그 검색용 (종료 일자)

}
