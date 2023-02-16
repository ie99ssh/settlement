package com.sandbox.settlement.common.exception;

/**--------------------------------------------------------------------
 * ■사용자 정의 전역 오류 클래스 ■sangheon
 --------------------------------------------------------------------**/
public class GlobalException extends CustomRuntimeException {
    private static final long serialVersionUID = 3708694357653235562L;

    public GlobalException(String strMessage, boolean isCustomMsg) {
        super(strMessage, isCustomMsg);
    }

    public GlobalException(String strMessage) {
        super(strMessage);
    }

    public GlobalException(Throwable objCause) {
        super(objCause);
    }
}
