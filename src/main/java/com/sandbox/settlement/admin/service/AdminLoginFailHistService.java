package com.sandbox.settlement.admin.service;

import com.sandbox.settlement.admin.dao.AdminLoginFailHistRepository;
import com.sandbox.settlement.admin.dao.AdminLoginFailHistRepositorySupport;
import com.sandbox.settlement.admin.dto.AdminLoginFailHistDto;
import com.sandbox.settlement.admin.entity.AdminLoginFailHist;
import com.sandbox.settlement.common.constants.GlobalConstants;
import com.sandbox.settlement.common.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminLoginFailHistService {

    private final AdminLoginFailHistRepository repository;
    private final AdminLoginFailHistRepositorySupport repositorySupport;
    private final CommonUtil commonUtil;

    public AdminLoginFailHist getAdminLoginFailHist(Integer failNo) {
        return repository.findByFailNo(failNo);
    }

    public List<AdminLoginFailHistDto> getAdminLoginFailHistList(AdminLoginFailHistDto adminLoginFailHistDto) {
        return repositorySupport.findAll(adminLoginFailHistDto);
    }

    public AdminLoginFailHist saveAdminLoginFailHist(AdminLoginFailHist adminLoginFailHist) {
        return repository.save(adminLoginFailHist);
    }

    public void excelDownload(HttpServletResponse response, AdminLoginFailHistDto adminLoginFailHistDto) throws Exception {
        Map<String, Object> beans = new HashMap<>();
        adminLoginFailHistDto.setPageFechNo(0);
        adminLoginFailHistDto.setPageSize(GlobalConstants.EXCEL_MAX_COUNT);
        beans.put("dataList", this.getAdminLoginFailHistList(adminLoginFailHistDto));

        commonUtil.getExcelDownload(response, beans, GlobalConstants.EXCEL_TEMPLATE_ADMIN_LOGIN_FAIL_HIST);
    }

}
