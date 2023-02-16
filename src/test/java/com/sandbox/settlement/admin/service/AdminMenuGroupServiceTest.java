package com.sandbox.settlement.admin.service;

import com.sandbox.settlement.menu.entity.AdminMenuGroup;
import com.sandbox.settlement.menu.service.AdminMenuGroupService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminMenuGroupServiceTest {

    @Autowired private AdminMenuGroupService service;

    @Test
    public void getAdminMenuGroup() throws Exception {

        AdminMenuGroup adminMenuGroup = service.getAdminMenuGroup(1);

        System.out.println("adminMenuGroup : " + adminMenuGroup);

    }
}
