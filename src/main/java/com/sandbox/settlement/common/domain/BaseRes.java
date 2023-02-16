package com.sandbox.settlement.common.domain;

import com.sandbox.settlement.common.constants.GlobalConstants;
import lombok.Data;

/**--------------------------------------------------------------------
 * ■처리 결과 응답 모델 ■sangheon
 --------------------------------------------------------------------**/
@Data
public class BaseRes {
    private String strRetMsg;
    private int intRetCode;

    public BaseRes() {
        strRetMsg  = GlobalConstants.COMMON_SUCCEED_MSG;
        intRetCode = GlobalConstants.COMMON_SUCCEED_CODE;
    }
}
