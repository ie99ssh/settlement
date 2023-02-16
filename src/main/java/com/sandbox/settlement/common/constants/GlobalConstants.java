package com.sandbox.settlement.common.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**--------------------------------------------------------------------
 * ■전역 상수 설정 ■sh_shin
 --------------------------------------------------------------------**/
public class GlobalConstants {
    // 로그인 정보
    public static final int     LAST_PWD_CHG_DAYS_LIMIT = 90;

    public static final String  SESSION_CSRF_KEY        = "47DCP4QUB3N5EF8U";
    public static final String  SESSION_LOGIN_KEY       = "SESSION_LOGIN_KEY";
    public static final String  SESSION_CI_FIELD_FLAG   = "CIFieldFlag";

    // 시스템 관련
    public static final String  SYSTEM_AES_KEY      = "cq3pem45tjfvavjl7t45i489p9gt2u1v";
    public static final String  SYSTEM_ENCODING     = "UTF-8";                            // 기본 인코딩
    public static final String  HIDDEN_CSRF_ID      = "RequestVerificationToken";
    public static final String  BCRYPT_ALGORITHM    = "SHA1PRNG";
    public static final String  COMMON_SUCCEED_MSG  = "OK";

    public static final String  INTERNAL_ERROR_MSG  = "시스템에 문제가 발생하였습니다. 관리자에게 문의해주세요.";    // 내부 오류 메시지
    public static final int     COMMON_SUCCEED_CODE = 0;     // 정상 응답 코드
    public static final int     COMMON_FAILED_CODE  = 9999;  // 비정상 응답 코드
    public static final int     SESSION_EXPIRE_CODE = 499;   // 세션 만료 코드
    public static final String  CONTENT_TYPE        = "Content-Type";
    public static final String  ACCEPT              = "Accept";

    public static final String PATH_ERROR                    = "error";
    public static final String PATH_COMMON                   = "common/common";
    public static final String PATH_LOGIN                    = "login/login";
    public static final String PATH_ADMINISTRATOR            = "administrator/administrator";
    public static final String PATH_ADMIN_MENU_ACCESS_LOG    = "administrator/adminMenuAccessLog";

    public static final String PATH_ADMIN_LOGIN_FAIL_HIST    = "administrator/adminLoginFailHist";
    public static final String PATH_MENU                     = "menuAuthority/menu";
    public static final String PATH_MENU_GROUP               = "menuAuthority/menuGroup";
    public static final String PATH_MENU_ROLE                = "menuAuthority/menuRole";
    public static final String PATH_BASIC_CODE               = "basicCode/basicCode";

    // PDF 파일 생성 관련
    public static final String PDF_FONT_FILE_PATH           = "static/htdocs/PDF/malgun.ttf";
    public static final String PDF_FONT_FILE_NAME           = "malgun.ttf";
    public static final String PDF_LOGO_IMAGE_FILE_PATH     = "static/htdocs/PDF/logo-horizontal.png";
    public static final String PDF_LOGO_IMAGE_FILE_NAME     = "logo-horizontal.png";

    public static final String EXCEL_TEMPLATE_PATH = "static/htdocs/Excel/";
    public static final String EXCEL_TEMPLATE_ADMINISTRATOR = "TemplateAdministratorExcel.xlsx";
    public static final String EXCEL_TEMPLATE_ADMIN_LOGIN_FAIL_HIST = "TemplateAdminLoginFailHist.xlsx";
    public static final String EXCEL_TEMPLATE_ADMIN_MENU_ACCESS_LOG = "TemplateAdminMenuAccessLog.xlsx";

    public static Map<String, String> EXCEL_DOWNLOAD_FILE_NAME_KOR(){
        final Map<String, String> objMap = new HashMap<>();

        objMap.put(EXCEL_TEMPLATE_ADMINISTRATOR, "관리자 계정_%s.xlsx");

        return Collections.unmodifiableMap(objMap);
    }

    public static final int EXCEL_MAX_COUNT = 100000;  // 엑셀 최대 다운로드

    // 비밀번호 초기화 관련
    public static final String  RESET_PWD_URI                      = "/login/resetPwdRequest";
    public static final String  PATH_RESET_PWD                     = "login/adminInitPwd";
    public static final String  ADMIN_RESET_PWD_MAIL_SUBJECT       = "[관리자] 비밀번호 초기화 메일";
    public static final String  ADMIN_RESET_PWD_MAIL_TEMPLATE_PATH = "mail/common/resetPassword.html";
    public static final Integer TOKEN_EXPIRE_VALID_TIME            = 1000 * 60 * 60;  // 비밀번호 초기화 유효 시간 (60분)

    //모드 관련
    public static final String MODE_INS       = "Ins";
    public static final String MODE_UPD       = "Upd";

    // 메일 전송 관련
    public static final String FROM_MAIL_ADDRESS = "finance@sandboxnetwork.com";

}
