package com.sandbox.settlement.common.exception;

/**--------------------------------------------------------------------
 * ■롤백 실행 방지 Exception 클래스 ■sangheon
 --------------------------------------------------------------------**/
public class NoRollbackException extends CustomRuntimeException {
    private static final long serialVersionUID = -635987732920434150L;

    public NoRollbackException(Integer intCode, String strMessage, boolean isCustomMsg) {
        super(intCode, strMessage, isCustomMsg);
    }

    public NoRollbackException(String strMessage, boolean isCustomMsg) {
        super(strMessage, isCustomMsg);
    }

    public NoRollbackException(String strMessage) {
        super(strMessage);
    }

    public NoRollbackException(Throwable objCause) {
        super(objCause);
    }

}
