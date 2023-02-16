package com.sandbox.settlement.admin.dto;

import com.sandbox.settlement.common.domain.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdministratorDto extends BaseModel {

    private Integer adminNo;
    private String adminId;
    private String adminPwd;
    private String adminName;
    private Integer menuRoleNo;
    private String phoneNo;
    private String email;
    private Boolean accessIPRestrictFlag;
    private String accessIP1;
    private String accessIP2;
    private String accessIP3;
    private LocalDateTime lastLoginDate;
    private String lastLoginIP;
    private LocalDateTime pwdUpdDate;
    private Integer regAdminNo;
    private Boolean useFlag;

    //------------------------------------------------------------------
    // 비밀번호 초기화 관련
    //------------------------------------------------------------------
    private String  token;                 // 토큰
    private Boolean authFlag;              // 인증 여부 (0:미인증, 1:인증)
    private LocalDateTime authExpDate;     // 인증 만료 일시
    private String  newPwd;                // 신규 비밀번호
    private String  reNewPwd;              // 신규 비밀번호 재입력
    private String  currPwd;               // 현재 비밀번호

}
