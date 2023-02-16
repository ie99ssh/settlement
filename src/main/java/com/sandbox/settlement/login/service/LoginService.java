package com.sandbox.settlement.login.service;

import com.sandbox.settlement.admin.entity.AdminLoginFailHist;
import com.sandbox.settlement.admin.entity.Administrator;
import com.sandbox.settlement.admin.service.AdminLoginFailHistService;
import com.sandbox.settlement.admin.service.AdministratorService;
import com.sandbox.settlement.common.constants.GlobalConstants;
import com.sandbox.settlement.common.exception.GlobalException;
import com.sandbox.settlement.common.exception.NoRollbackException;
import com.sandbox.settlement.common.util.CommonUtil;
import com.sandbox.settlement.login.dto.Login;
import com.sandbox.settlement.menu.dto.AdminMenuDto;
import com.sandbox.settlement.menu.service.AdminMenuService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/**--------------------------------------------------------------------
 * ■로그인 인터페이스 구현부 ■sangheon
 --------------------------------------------------------------------**/
@Service
@RequiredArgsConstructor
public class LoginService {

    private final AdministratorService administratorService;
    private final AdminLoginFailHistService adminLoginFailHistService;
    private final AdminMenuService adminMenuService;
    private final CommonUtil commonUtil;

    /**--------------------------------------------------------------------
     * ■일반 로그인 서비스 처리 로직 ■sangheon
     --------------------------------------------------------------------**/
    @Transactional(noRollbackFor=NoRollbackException.class)
    public Login loginProc(Login login) throws GlobalException, NoSuchAlgorithmException {

        Administrator administrator = administratorService.getAdministratorById(login.getAdminId());

        AdminLoginFailHist adminLoginFailHist = new AdminLoginFailHist();
        adminLoginFailHist.setAdminId(login.getAdminId());
        adminLoginFailHist.setIpAddr(login.getIpAddr());
        adminLoginFailHist.setRegId(login.getAdminId());

        if(ObjectUtils.isEmpty(administrator)) {
            adminLoginFailHist.setErrCode(9001);
            adminLoginFailHist.setErrMsg("등록되어 있지 않은 ID");
        } else {
            checkAdmin(login, administrator, adminLoginFailHist);
        }

        if(ObjectUtils.isEmpty(administrator) || !login.isChkAdminFlag()) {

            adminLoginFailHistService.saveAdminLoginFailHist(adminLoginFailHist);

            throw new NoRollbackException(adminLoginFailHist.getErrCode(), adminLoginFailHist.getErrMsg(), true);
        }

        administrator.setLastLoginDate(LocalDateTime.now());
        administrator.setUpdDate(LocalDateTime.now());
        administrator.setLastLoginIP(login.getIpAddr());
        administrator.setUpdId(login.getAdminId());

        return createResLogin(administrator, login);
    }

    /**--------------------------------------------------------------------
     * ■관리자 정보 체크 모듈 ■sangheon
     --------------------------------------------------------------------**/
    private void checkAdmin(Login login, Administrator administrator, AdminLoginFailHist objAdminLoginFailHist) throws NoSuchAlgorithmException {
        boolean isChkAdminFlag = true;

        // 비밀번호 확인
        BCryptPasswordEncoder objPasswordEncoder = new BCryptPasswordEncoder(10, SecureRandom.getInstance(GlobalConstants.BCRYPT_ALGORITHM));
        boolean isAdminPwdChkFlag = objPasswordEncoder.matches(login.getAdminPwd(), administrator.getAdminPwd());

        if(StringUtils.isEmpty(administrator.getAdminPwd()) || !isAdminPwdChkFlag) {
            objAdminLoginFailHist.setErrCode(9002);
            objAdminLoginFailHist.setErrMsg("비밀번호 오류");

            isChkAdminFlag = false;
        }

        // 사용 여부 확인
        if(isChkAdminFlag && !administrator.getUseFlag()) {
            objAdminLoginFailHist.setErrCode(9003);
            objAdminLoginFailHist.setErrMsg("사용 정지 된 ID");

            isChkAdminFlag = false;
        }

        // 접근 가능 IP 확인
        if(isChkAdminFlag && administrator.getAccessIPRestrictFlag() && !administrator.getAccessIP1().equals(login.getIpAddr())
                && !administrator.getAccessIP2().equals(login.getIpAddr()) && !administrator.getAccessIP3().equals(login.getIpAddr())) {
            objAdminLoginFailHist.setErrCode(9004);
            objAdminLoginFailHist.setErrMsg("로그인 할 수 없는 IP");

            isChkAdminFlag = false;
        }

        // 최근 비밀번호 변경 일자 확인
        if(isChkAdminFlag && GlobalConstants.LAST_PWD_CHG_DAYS_LIMIT < commonUtil.getDateTimeDiff(Timestamp.valueOf(administrator.getPwdUpdDate()).getTime(), new Date().getTime())) {
            objAdminLoginFailHist.setErrCode(9005);
            objAdminLoginFailHist.setErrMsg("비밀번호 미수정 일자 초과");

            isChkAdminFlag = false;
        }

        login.setChkAdminFlag(isChkAdminFlag);
    }

    /**--------------------------------------------------------------------
     * ■로그인 응답 모델 값 주입 모듈 ■sangheon
     --------------------------------------------------------------------**/
    private Login createResLogin(Administrator administrator, Login login) {
        login.setSessionKey("");
        login.setAdminNo(administrator.getAdminNo());
        login.setAdminId(administrator.getAdminId());
        login.setLastLoginDate(administrator.getLastLoginDate());
        login.setLastLoginIP(administrator.getLastLoginIP());
        login.setFirstLoginFlag("First Login".equalsIgnoreCase(login.getLastLoginIP()));

        // 관리자 로그인 시 초기 페이지 셋팅 (접근 권한을 가지고 있는 메뉴 1개)

        AdminMenuDto adminMenuDto = adminMenuService.getMenuLinkByLoginAction(administrator.getAdminId());
        String menuLink = adminMenuDto.getMenuLink();
        login.setMenuLink(menuLink);

        return login;
    }
}
