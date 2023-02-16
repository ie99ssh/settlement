package com.sandbox.settlement.admin.service;

import com.sandbox.settlement.admin.entity.AdminMenuAccessLog;
import com.sandbox.settlement.common.constants.GlobalConstants;
import com.sandbox.settlement.menu.entity.AdminMenu;
import com.sandbox.settlement.menu.service.AdminMenuService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminMenuAccessLogDtoServiceTest {

    @Autowired private AdminMenuAccessLogService service;

    @Test
    public void saveAdminMenuAccessLog() throws Exception {

        AdminMenuAccessLog adminMenuAccessLog = new AdminMenuAccessLog();
        adminMenuAccessLog.setMenuNo(0);
        adminMenuAccessLog.setMenuLink("test");
        adminMenuAccessLog.setMethodName("testMethod");
        adminMenuAccessLog.setLogDesc("logdesc");
        adminMenuAccessLog.setAdminNo(0);
        adminMenuAccessLog.setIpAddr("1111111");
        adminMenuAccessLog.setRegDate(LocalDateTime.now());

        AdminMenuAccessLog k = service.saveAdminMenuAccessLog(adminMenuAccessLog);

        System.out.println(k);

    }

    @Test
    public void getAdminMenuAccessLogList() throws Exception {

        List<AdminMenuAccessLog> k = service.getAdminMenuAccessLogList(1);

        System.out.println(k);

    }


}
