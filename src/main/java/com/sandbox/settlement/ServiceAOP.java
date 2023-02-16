package com.sandbox.settlement;

import com.sandbox.settlement.common.domain.BaseModel;
import com.sandbox.settlement.common.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**--------------------------------------------------------------------
 * ■AOP 설정(I/O 로깅) ■sangheon
 --------------------------------------------------------------------**/
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ServiceAOP {

    private final CommonUtil commonUtil;

    /**--------------------------------------------------------------------
     * ■controllerPointcut ■sangheon
     --------------------------------------------------------------------**/
    @Pointcut("within(com.sandbox.settlement..controller.*)")
    // 서비스까지 AOP 설정하는 경우
    // @Pointcut("within(templateadmin.admin..*)")
    public void controllerPointcut() {
    }

    @Pointcut("within(com.sandbox.settlement..service.*) && !within(com.sandbox.settlement.common.service.SendMailService)")
    public void servicePointcut() {
    }

    /**--------------------------------------------------------------------
     * ■컨트롤러에 대해서만 I/O 로깅 ■sangheon
     --------------------------------------------------------------------**/
    @Around("controllerPointcut()")
    public Object controllerAround(final ProceedingJoinPoint objProceedingJoinPoint) throws Throwable {
        // 메서드 실행
        Object objResult = executeMethod(objProceedingJoinPoint);

        // 응답 모델 데이터에 CI Field가 존재하면 해당 필드를 마스킹 처리 된 값으로 치환하여 반환
        if(objResult instanceof ModelAndView) {
            ModelAndView result = (ModelAndView)objResult;
            ModelMap modelMap   = result.getModelMap();
            Object modelData    = modelMap.get("data");

            if(modelData != null) {
                if(modelData instanceof ArrayList) {
                    List<?> modelDataList = (ArrayList)modelData;
                    for (Object o : modelDataList) {
                        commonUtil.setCIMaskedValue(o);
                    }
                }
            }
        }
        return objResult;
    }

    /**--------------------------------------------------------------------
     * ■서비스 AOP 처리 ■sangheon
     --------------------------------------------------------------------**/
    @Around("servicePointcut()")
    public Object servicePointcut(final ProceedingJoinPoint objProceedingJoinPoint) throws Throwable {

        for(Object object : objProceedingJoinPoint.getArgs()) {

            if (object instanceof BaseModel) {
                servicePaging((BaseModel) object);
            }
        }

        // 메서드 실행

        return executeMethod(objProceedingJoinPoint);
    }

    /**--------------------------------------------------------------------
     * ■서비스에서 페이징 실제 처리 ■sangheon
     --------------------------------------------------------------------**/
    private void servicePaging(BaseModel baseModel) {
        if (baseModel.getLength() != 0) {
            int intPageNo = (baseModel.getStart() / baseModel.getLength()) + 1;
            int intPageFetchNo = (intPageNo - 1) * baseModel.getLength();

            baseModel.setPageNo(intPageNo);
            baseModel.setPageSize(baseModel.getLength());
            baseModel.setPageFechNo(intPageFetchNo);
        }
    }

    /**--------------------------------------------------------------------
     * ■컨트롤러, 서비스 메소드 실행 AOP ■sangheon
     --------------------------------------------------------------------**/
    private Object executeMethod(ProceedingJoinPoint objProceedingJoinPoint) throws Throwable {

        String methodInfo = String.format("%s/%s",objProceedingJoinPoint.getSignature().getDeclaringTypeName(), objProceedingJoinPoint.getSignature().getName());
        // 메서드 실행 전
        log.info("### {} : Start", methodInfo);

        StringBuilder objSB = new StringBuilder();
        int intArgs = objProceedingJoinPoint.getArgs().length;

        for(Object object : objProceedingJoinPoint.getArgs()) {
            if(object != null) {
                objSB.append(commonUtil.getObjectInfo(object));
            } else {
                objSB.append((Object) null);
            }

            objSB.append(--intArgs != 0 ? ", " : "");
        }

        log.info("### {} : ARGS - {}", methodInfo, objSB.toString());

        // 메서드 실행
        Object objResult = objProceedingJoinPoint.proceed();

        // 메서드 실행 후
        log.info("### {} : RESULT - {}", methodInfo,  objResult);
        log.info("### {} : End\n\n", methodInfo);

        return objResult;
    }
}
