package com.sandbox.settlement.login.dto;

import com.sandbox.settlement.common.domain.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**--------------------------------------------------------------------
 * ■로그인 모델 (메일에서의 진입) ■sangheon
 --------------------------------------------------------------------**/
@EqualsAndHashCode(callSuper = true)
@Data
public class Login extends BaseModel implements Serializable{
    private static final long serialVersionUID = 1L;

    private Integer adminNo;           // 관리자 번호
    private String  adminId;           // 관리자 아이디
    private String  adminPwd;          // 관리자 비밀번호

    private LocalDateTime lastLoginDate;  // 마지막 로그인 일시
    private String  lastLoginIP;       // 마지막 로그인 아이피
    private String  sessionKey;        // Session Key
    private String  loginMsg;          // 로그인 메시지
    private String  ipAddr;            // 로그인 아이피

    private String  menuLink;          // 최초 접근 메뉴
    private Boolean firstLoginFlag;    // 최초 로그인 여부
    private String  returnUrl;         // 반환 URL
    private boolean ChkAdminFlag;      // 로그인 체크
}
