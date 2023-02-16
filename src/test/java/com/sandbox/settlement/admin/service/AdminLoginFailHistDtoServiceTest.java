package com.sandbox.settlement.admin.service;

import com.sandbox.settlement.admin.entity.AdminLoginFailHist;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback
public class AdminLoginFailHistDtoServiceTest {

    @Autowired private AdminLoginFailHistService service;

    @Test
    public void getAdminLoginFailHist() throws Exception {

        AdminLoginFailHist adminLoginFailHist = service.getAdminLoginFailHist(1);

        System.out.println("adminLoginFailHist : " + adminLoginFailHist);

    }

    @Test
    public void saveAdminLoginFailHist() throws Exception {

        AdminLoginFailHist adminLoginFailHist = new AdminLoginFailHist();
        adminLoginFailHist.setAdminId("sangheon");
        adminLoginFailHist.setIpAddr("1111");
        adminLoginFailHist.setRegId("sangheon");

        adminLoginFailHist.setErrCode(1002);
        adminLoginFailHist.setErrMsg("테스트");

        AdminLoginFailHist aaaa = service.saveAdminLoginFailHist(adminLoginFailHist);

        System.out.println("aaaa : " + aaaa);

    }

}
