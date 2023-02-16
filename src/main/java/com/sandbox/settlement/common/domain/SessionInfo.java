package com.sandbox.settlement.common.domain;

import lombok.Data;

/**--------------------------------------------------------------------
 * ■세션 정보 모델 ■sangheon
 --------------------------------------------------------------------**/
@Data
public class SessionInfo {
    private int     adminNo;
    private String  adminId;
    private int     menuGroupNo;
    private String  menuGroupName;
    private int     menuNo;

    private String  menuName;
    private int     authCode;
    private String  ipAddr;
    private Boolean ciReadFlag;
    private Boolean dnAvailFlag;
}
