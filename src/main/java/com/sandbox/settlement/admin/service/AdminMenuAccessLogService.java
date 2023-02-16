package com.sandbox.settlement.admin.service;

import com.sandbox.settlement.admin.dao.AdminMenuAccessLogRepository;
import com.sandbox.settlement.admin.dao.AdminMenuAccessLogRepositorySupport;
import com.sandbox.settlement.admin.dto.AdminLoginFailHistDto;
import com.sandbox.settlement.admin.dto.AdminMenuAccessLogDto;
import com.sandbox.settlement.admin.entity.AdminMenuAccessLog;
import com.sandbox.settlement.common.constants.GlobalConstants;
import com.sandbox.settlement.common.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminMenuAccessLogService {

    private final AdminMenuAccessLogRepository repository;
    private final AdminMenuAccessLogRepositorySupport repositorySupport;
    private final CommonUtil commonUtil;

    public AdminMenuAccessLog saveAdminMenuAccessLog(AdminMenuAccessLog adminMenuAccessLog) {
        return repository.save(adminMenuAccessLog);
    }

    public List<AdminMenuAccessLog> getAdminMenuAccessLogList(Integer adminNo) {
        return repository.findByAdminNo(adminNo);
    }

    public List<AdminMenuAccessLogDto> getAdminMenuAccessLogList(AdminMenuAccessLogDto adminMenuAccessLogDto) {
        return repositorySupport.findAll(adminMenuAccessLogDto);
    }

    public void excelDownload(HttpServletResponse response, AdminMenuAccessLogDto adminMenuAccessLogDto) throws Exception {
        Map<String, Object> beans = new HashMap<>();
        adminMenuAccessLogDto.setPageFechNo(0);
        adminMenuAccessLogDto.setPageSize(GlobalConstants.EXCEL_MAX_COUNT);
        beans.put("dataList", this.getAdminMenuAccessLogList(adminMenuAccessLogDto));
        commonUtil.getExcelDownload(response, beans, GlobalConstants.EXCEL_TEMPLATE_ADMIN_MENU_ACCESS_LOG);
    }

}
