package com.sandbox.settlement.common.exception;

import com.sandbox.settlement.common.constants.GlobalConstants;

/**--------------------------------------------------------------------
 * ■사용자 정의 런타임 오류 클래스 ■sangheon
 --------------------------------------------------------------------**/
public class CustomRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -4905188315768559766L;

    private final boolean isCustomMsg;

    public CustomRuntimeException(String strMessage) {
        super(GlobalConstants.COMMON_FAILED_CODE + ":" + strMessage);
        this.isCustomMsg = false;
    }

    public CustomRuntimeException(String strMessage, boolean isCustomMsg) {
        super(GlobalConstants.COMMON_FAILED_CODE + ":" + strMessage);
        this.isCustomMsg = isCustomMsg;
    }

    public CustomRuntimeException(Integer intCode, String strMessage, boolean isCustomMsg) {
        super(intCode + ":" + strMessage);
        this.isCustomMsg = isCustomMsg;
    }

    public CustomRuntimeException(Throwable objCause) {
        super(objCause);
        this.isCustomMsg = false;
    }

    public boolean isisCustomMsg() {
        return isCustomMsg;
    }
}
