package com.sandbox.settlement.admin.service;

import com.sandbox.settlement.admin.dao.AdministratorRepository;
import com.sandbox.settlement.admin.dao.AdministratorRepositorySupport;
import com.sandbox.settlement.admin.dto.AdministratorDto;
import com.sandbox.settlement.admin.entity.AdminPwdChangeHist;
import com.sandbox.settlement.admin.entity.AdminPwdReset;
import com.sandbox.settlement.admin.entity.Administrator;
import com.sandbox.settlement.common.constants.GlobalConstants;
import com.sandbox.settlement.common.domain.SessionInfo;
import com.sandbox.settlement.common.exception.GlobalException;
import com.sandbox.settlement.common.service.SendMailService;
import com.sandbox.settlement.common.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdministratorService {

    private final AdministratorRepository administratorRepository;
    private final AdministratorRepositorySupport administratorRepositorySupport;
    private final AdminPwdResetService adminPwdResetService;
    private final CommonUtil commonUtil;
    private final SendMailService sendMailService;
    private final TemplateEngine templateEngine;
    private final AdminPwdChangeHistService adminPwdChangeHistService;

    @Value("${spring.profiles.active}")
    private String strServerEnvironment;

    @Value("${server.host}")
    private String strServerHost;

    @Value("${server.port}")
    private String strServerPort;

    public Administrator getAdministrator(Integer adminNo) {
        return administratorRepository.findByAdminNo(adminNo);
    }

    public Administrator getAdministratorById(String adminId) {
        return administratorRepository.findByAdminId(adminId);
    }

    public List<AdministratorDto> getAdministratorList(AdministratorDto administratorDto) {
        return administratorRepositorySupport.findAll(administratorDto);
    }

    public Administrator saveAdministrator(AdministratorDto administratorDto, SessionInfo sessionInfo) {

        if (ObjectUtils.isEmpty(administratorDto.getAdminNo())) {
            Administrator administrator = new Administrator();
            BeanUtils.copyProperties(administratorDto, administrator);
            administrator.setAuthFlag(false);
            administrator.setUseFlag(true);
            administrator.setRegId(sessionInfo.getAdminId());

            Administrator administratorRes = administratorRepository.save(administrator);
            administratorDto.setAdminNo(administratorRes.getAdminNo());

            this.resetAdministratorPwd(administratorDto);

            return administrator;

        } else {
            Administrator administrator = this.getAdministrator(administratorDto.getAdminNo());
            BeanUtils.copyProperties(administratorDto, administrator);
            administrator.setAuthFlag(false);
            administrator.setUpdDate(LocalDateTime.now());
            administrator.setUpdId(administratorDto.getAdminId());
            return administrator;
        }


    }

    public void excelDownload(HttpServletResponse response, AdministratorDto administratorDto) throws Exception {
        Map<String, Object> beans = new HashMap<>();
        administratorDto.setPageFechNo(0);
        administratorDto.setPageSize(GlobalConstants.EXCEL_MAX_COUNT);
        beans.put("dataList", this.getAdministratorList(administratorDto));
        commonUtil.getExcelDownload(response, beans, GlobalConstants.EXCEL_TEMPLATE_ADMINISTRATOR);
    }

    /**--------------------------------------------------------------------
     * ■관리자 비밀번호 초기화 서비스 ■sangheon
     --------------------------------------------------------------------**/
    public void resetAdministratorPwd(AdministratorDto administratorDto) {
        String strDomain;
        String strMailContent;
        String strToken = commonUtil.getUuid();
        administratorDto.setToken(strToken);

        // 관리자 비밀번호 초기화 정보 입력
        AdminPwdReset adminPwdReset = new AdminPwdReset();
        adminPwdReset.setRegId(administratorDto.getAdminId());
        adminPwdReset.setAuthExpDate(LocalDateTime.now());
        adminPwdReset.setAdminNo(administratorDto.getAdminNo());
        adminPwdReset.setAuthFlag(false);
        adminPwdReset.setToken(strToken);

        AdminPwdReset adminPwdResetRes = adminPwdResetService.saveAdminPwdReset(adminPwdReset);
        if(ObjectUtils.isEmpty(adminPwdResetRes)) {
            throw new GlobalException("관리자 비밀번호 수정 실패");
        }

        if(strServerEnvironment.toLowerCase().equals("local")) {
            strDomain = String.format("//%s:%s", strServerHost, strServerPort);
        } else {
            strDomain = String.format("//%s", strServerHost);
        }

        Context mailBodyContext = new Context();
        mailBodyContext.setVariable("adminId", administratorDto.getAdminId());
        mailBodyContext.setVariable("resetPasswordURL", strDomain + GlobalConstants.RESET_PWD_URI);
        mailBodyContext.setVariable("token", administratorDto.getToken());

        strMailContent = templateEngine.process(GlobalConstants.ADMIN_RESET_PWD_MAIL_TEMPLATE_PATH, mailBodyContext);

        // 관리자 비밀번호 초기화 메일 전송
        try {
            sendMailService.sendMail(administratorDto.getEmail(),
                    GlobalConstants.FROM_MAIL_ADDRESS,
                    GlobalConstants.ADMIN_RESET_PWD_MAIL_SUBJECT,
                    strMailContent,
                    null);
        } catch (Exception e) {
            log.error("메일 전송 실패 : {}", e.getMessage());
        }
    }

    /**--------------------------------------------------------------------
     * ■비밀번호 초기화 요청 처리 로직 ■sangheon
     --------------------------------------------------------------------**/
    public String checkResetPwdRequest(String strToken) throws GlobalException {
        // 관리자 비밀번호 초기화 요청 정보 조회
        AdminPwdReset adminPwdReset = adminPwdResetService.getAdminPwdResetByToken(strToken);

        // 요청 정보 확인
        if(ObjectUtils.isEmpty(adminPwdReset)) {
            throw new GlobalException("유효하지 않은 인증 정보");
        }

        // 기 인증 여부 확인
        if(adminPwdReset.getAuthFlag()) {
            throw new GlobalException("이미 인증 처리된 정보");
        }

        // 인증 만료 시간 확인
        if(GlobalConstants.TOKEN_EXPIRE_VALID_TIME < new Date().getTime() - Timestamp.valueOf(adminPwdReset.getAuthExpDate()).getTime()) {
            throw new GlobalException("인증 유효 시간 만료", true);
        }

        Administrator administrator = this.getAdministrator(adminPwdReset.getAdminNo());

        return administrator.getAdminId();
    }

    /**--------------------------------------------------------------------
     * ■비밀번호 초기화 처리 로직 ■sangheon
     --------------------------------------------------------------------**/
    public void resetPwd(AdministratorDto administratorDto) throws GlobalException {
        BCryptPasswordEncoder passwordEncoder;
        try {
            passwordEncoder
                    = new BCryptPasswordEncoder(10, SecureRandom.getInstance(GlobalConstants.BCRYPT_ALGORITHM));
        } catch (NoSuchAlgorithmException e) {
            throw new GlobalException("패스워드 인코딩 오류 [" + e.getMessage() + "]", true);
        }

        administratorDto.setNewPwd(passwordEncoder.encode(administratorDto.getNewPwd()));

        String adminId = administratorDto.getAdminId();
        String token = administratorDto.getToken();

        // 비밀번호 변경 이력 등록
        Administrator administrator = this.getAdministratorById(adminId);
        AdminPwdChangeHist adminPwdChangeHist = new AdminPwdChangeHist();
        adminPwdChangeHist.setAdminNo(administrator.getAdminNo());
        adminPwdChangeHist.setAdminPwd(administrator.getAdminPwd());
        adminPwdChangeHist.setRegId(adminId);

        AdminPwdChangeHist adminPwdChangeHistRes
                = adminPwdChangeHistService.saveAdminPwdChangeHist(adminPwdChangeHist);
        if(ObjectUtils.isEmpty(adminPwdChangeHistRes)) {
            throw new GlobalException("비밀번호 변경 이력 등록 실패");
        }

        // 신규 비밀번호 저장
        administrator.setUpdId(adminId);
        administrator.setUpdDate(LocalDateTime.now());
        administrator.setAdminPwd(administratorDto.getAdminPwd());
        administrator.setPwdUpdDate(LocalDateTime.now());

        // 비밀번호 초기화 정보 수정 (인증 정보)
        AdminPwdReset adminPwdReset = adminPwdResetService.getAdminPwdResetByToken(token);
        adminPwdReset.setAuthFlag(true);
        adminPwdReset.setUpdDate(LocalDateTime.now());
        adminPwdReset.setUpdId(adminId);
    }

    /**--------------------------------------------------------------------
     * ■비밀번호 변경 처리 로직 ■sangheon
     --------------------------------------------------------------------**/
    public void changePwd(AdministratorDto administratorDto) {

        BCryptPasswordEncoder passwordEncoder;
        try {
            passwordEncoder
                    = new BCryptPasswordEncoder(10, SecureRandom.getInstance(GlobalConstants.BCRYPT_ALGORITHM));
        } catch (NoSuchAlgorithmException e) {
            throw new GlobalException("패스워드 인코딩 오류 [" + e.getMessage() + "]", true);
        }

        String adminId = administratorDto.getAdminId();
        administratorDto.setNewPwd(passwordEncoder.encode(administratorDto.getNewPwd()));
        Administrator administrator = this.getAdministratorById(adminId);

        if (ObjectUtils.isEmpty(administrator)) {
            throw new GlobalException("관리자가 존재하지 않음 (오류 확인 필요)", true);
        }

        String adminPwd = administrator.getAdminPwd();

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(administratorDto.getCurrPwd(), adminPwd)) {
            throw new GlobalException("비밀번호 오류", true);
        }

        // 기존 비밀번호와 동일 여부 확인
        if (passwordEncoder.matches(administratorDto.getReNewPwd(), adminPwd)) {
            throw new GlobalException("현재와 동일한 비밀번호 오류", true);
        }

        // 비밀번호 변경 이력 등록
        AdminPwdChangeHist adminPwdChangeHist = new AdminPwdChangeHist();
        adminPwdChangeHist.setAdminNo(administrator.getAdminNo());
        adminPwdChangeHist.setAdminPwd(administrator.getAdminPwd());
        adminPwdChangeHist.setRegId(adminId);

        AdminPwdChangeHist adminPwdChangeHistRes
                = adminPwdChangeHistService.saveAdminPwdChangeHist(adminPwdChangeHist);
        if (ObjectUtils.isEmpty(adminPwdChangeHistRes)) {
            throw new GlobalException("비밀번호 변경 이력 등록 실패");
        }

        // 신규 비밀번호 저장
        administrator.setUpdId(adminId);
        administrator.setUpdDate(LocalDateTime.now());
        administrator.setAdminPwd(administratorDto.getNewPwd());
        administrator.setPwdUpdDate(LocalDateTime.now());
    }

}
