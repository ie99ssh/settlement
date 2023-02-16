package com.sandbox.settlement.admin.dao;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sandbox.settlement.admin.dto.AdminMenuAccessLogDto;
import com.sandbox.settlement.admin.entity.AdminMenuAccessLog;
import com.sandbox.settlement.common.util.CommonUtil;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.sandbox.settlement.admin.entity.QAdminMenuAccessLog.adminMenuAccessLog;
import static com.sandbox.settlement.admin.entity.QAdministrator.administrator;
import static com.sandbox.settlement.menu.entity.QAdminMenu.adminMenu;

@Repository
public class AdminMenuAccessLogRepositorySupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;
    private final CommonUtil commonUtil;

    public AdminMenuAccessLogRepositorySupport(JPAQueryFactory jpaQueryFactory, CommonUtil commonUtil) {
        super(AdminMenuAccessLog.class);
        this.jpaQueryFactory = jpaQueryFactory;
        this.commonUtil = commonUtil;
    }

    public List<AdminMenuAccessLogDto> findAll(AdminMenuAccessLogDto adminMenuAccessLogDto) {

        QueryResults<AdminMenuAccessLogDto> resultList = jpaQueryFactory
                .select(Projections.fields(AdminMenuAccessLogDto.class,
                        adminMenuAccessLog.accessNo,
                        adminMenuAccessLog.adminNo,
                        adminMenuAccessLog.menuNo,
                        adminMenuAccessLog.menuLink,
                        adminMenuAccessLog.logDesc,
                        adminMenuAccessLog.methodName,
                        adminMenuAccessLog.regDate,
                        adminMenuAccessLog.regId,
                        adminMenuAccessLog.ipAddr,
                        administrator.adminId,
                        administrator.adminName,
                        ExpressionUtils.as(
                                JPAExpressions.select(adminMenu.menuName)
                                        .from(adminMenu)
                                        .where(adminMenu.menuNo.eq(adminMenuAccessLog.menuNo)),
                                "menuName")
                ))
                .from(adminMenuAccessLog)
                .leftJoin(administrator).on(adminMenuAccessLog.adminNo.eq(administrator.adminNo))
                .where(menuLinkLike(adminMenuAccessLogDto.getMenuLink()),
                        methodNameLike(adminMenuAccessLogDto.getMethodName()),
                        logDescLike(adminMenuAccessLogDto.getLogDesc()),
                        regDateBetween(adminMenuAccessLogDto.getStrFromYMD(), adminMenuAccessLogDto.getStrToYMD()),
                        adminIdEq(adminMenuAccessLogDto.getAdminId()),
                        menuNameLike(adminMenuAccessLogDto.getMenuName())
                )
                .orderBy(commonUtil.getOrderSpecifier(AdminMenuAccessLogDto.class, "accessNo"
                        , adminMenuAccessLogDto.getStrSortType()
                        , adminMenuAccessLogDto.getStrSortColumn()))
                .limit(adminMenuAccessLogDto.getPageSize())
                .offset(adminMenuAccessLogDto.getPageFechNo())
                .fetchResults();

        long totalCount = resultList.getTotal();

        adminMenuAccessLogDto.setRecordsFiltered(totalCount);
        adminMenuAccessLogDto.setRecordsTotal(totalCount);

        return resultList.getResults();
    }

    private BooleanExpression menuLinkLike(String menuLink) {
        return !ObjectUtils.isEmpty(menuLink) ? adminMenuAccessLog.menuLink.like("%"+menuLink+"%") : null;
    }

    private BooleanExpression methodNameLike(String methodName) {
        return !ObjectUtils.isEmpty(methodName) ? adminMenuAccessLog.methodName.like("%"+methodName+"%") : null;
    }

    private BooleanExpression logDescLike(String logDesc) {
        return !ObjectUtils.isEmpty(logDesc) ? adminMenuAccessLog.logDesc.like("%"+logDesc+"%") : null;
    }

    private BooleanExpression regDateBetween(String fromYmd, String toYmd) {

        if (!ObjectUtils.isEmpty(fromYmd) && !ObjectUtils.isEmpty(toYmd)) {

            LocalDateTime fromDate = commonUtil.getFromDate(fromYmd);
            LocalDateTime toDate = commonUtil.getToDate(toYmd);

            return adminMenuAccessLog.regDate.between(fromDate, toDate);
        }
        return null;
    }

    private BooleanExpression adminIdEq(String adminId) {
        return !ObjectUtils.isEmpty(adminId) ? administrator.adminId.eq(adminId) : null;
    }

    private BooleanExpression menuNameLike(String menuName) {
        return !ObjectUtils.isEmpty(menuName) ? adminMenu.menuName.like("%"+menuName+"%") : null;
    }

}
