package com.sandbox.settlement.admin.dao;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sandbox.settlement.admin.dto.AdminMenuAccessLogDto;
import com.sandbox.settlement.admin.dto.AdministratorDto;
import com.sandbox.settlement.admin.entity.Administrator;
import com.sandbox.settlement.common.util.CommonUtil;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.sandbox.settlement.admin.entity.QAdministrator.administrator;

@Repository
public class AdministratorRepositorySupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;
    private final CommonUtil commonUtil;

    public AdministratorRepositorySupport(JPAQueryFactory jpaQueryFactory, CommonUtil commonUtil) {
        super(Administrator.class);
        this.jpaQueryFactory = jpaQueryFactory;
        this.commonUtil = commonUtil;
    }

    public List<AdministratorDto> findAll(AdministratorDto administratorDto) {

        QueryResults<AdministratorDto> resultList =
                jpaQueryFactory.select(Projections.fields(AdministratorDto.class,
                         administrator.adminId
                        ,administrator.adminNo
                        ,administrator.adminName
                        ,administrator.phoneNo
                        ,administrator.email
                        ,administrator.regId
                        ,administrator.regDate
                        ,administrator.useFlag
                        ,administrator.menuRoleNo
                        ,administrator.accessIPRestrictFlag
                        ,administrator.accessIP1
                        ,administrator.accessIP2
                        ,administrator.accessIP3
                ))
                .from(administrator)
                .where(adminNameLike(administratorDto.getAdminName()),
                        adminIdEq(administratorDto.getAdminId()),
                        phoneNoEq(administratorDto.getPhoneNo()),
                        emailLike(administratorDto.getEmail()),
                        useFlagEq(administratorDto.getUseFlag())
                ).orderBy(commonUtil.getOrderSpecifier(AdminMenuAccessLogDto.class, "adminNo"
                , administratorDto.getStrSortType()
                , administratorDto.getStrSortColumn()))
                .limit(administratorDto.getPageSize())
                .offset(administratorDto.getPageFechNo())
                .fetchResults();

        long totalCount = resultList.getTotal();

        administratorDto.setRecordsFiltered(totalCount);
        administratorDto.setRecordsTotal(totalCount);

        return resultList.getResults();
    }

    private BooleanExpression adminNameLike(String adminName) {
        return !ObjectUtils.isEmpty(adminName) ? administrator.adminName.like("%"+adminName+"%") : null;
    }

    private BooleanExpression adminIdEq(String adminId) {
        return !ObjectUtils.isEmpty(adminId) ? administrator.adminId.eq(adminId) : null;
    }

    private BooleanExpression phoneNoEq(String phoneNo) {

        if (ObjectUtils.isEmpty(phoneNo)) {
            return null;
        }

        phoneNo = phoneNo.replace("-", "");
        return administrator.phoneNo.eq(phoneNo);
    }

    private BooleanExpression emailLike(String email) {
        return !ObjectUtils.isEmpty(email) ? administrator.email.like("%"+email+"%") : null;
    }

    private BooleanExpression useFlagEq(Boolean useFlag) {
        return !ObjectUtils.isEmpty(useFlag) ? administrator.useFlag.eq(useFlag) : null;
    }

}
