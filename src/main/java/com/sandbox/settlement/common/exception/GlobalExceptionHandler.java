package com.sandbox.settlement.common.exception;

import com.sandbox.settlement.common.constants.GlobalConstants;
import com.sandbox.settlement.common.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**--------------------------------------------------------------------
 * ■전역 예외처리 핸들러 ■sangheon
 --------------------------------------------------------------------**/
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final CommonUtil commonUtil;

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ModelAndView commonResException(HttpServletRequest objHttpServletRequest, HttpServletResponse objHttpServletResponse, Throwable objThrowable) throws IOException {
        log.error("Throwable Exception Stack Trace: {}", ExceptionUtils.getStackTrace(objThrowable));

        String strContentType = StringUtils.isEmpty(objHttpServletRequest.getHeader(GlobalConstants.CONTENT_TYPE)) ?
                objHttpServletRequest.getHeader(GlobalConstants.ACCEPT).replaceAll(" ", "")
                : objHttpServletRequest.getHeader(GlobalConstants.CONTENT_TYPE).replaceAll(" ", "");

        int intRetCode   = GlobalConstants.COMMON_FAILED_CODE;
        String strRetMsg = "내부 오류가 발생하였습니다.";

        try{
            if(!StringUtils.isEmpty(strContentType) && (strContentType.startsWith(MediaType.APPLICATION_JSON_VALUE))) {
                if (objThrowable instanceof CustomRuntimeException && ((CustomRuntimeException)objThrowable).isisCustomMsg()) {
                    String strMessage = objThrowable.getMessage(); // 구현부에서 사용자 정의 예외 처리된 메시지 획득
                    String[] arrMsg   = strMessage.split(":");     // 메시지를 구분자(":")로 배열 저장

                    intRetCode = Integer.parseInt(arrMsg[0]);
                    strRetMsg  = arrMsg[1];
                }

                objHttpServletResponse.setStatus(HttpStatus.OK.value());
            } else {
                if (objThrowable instanceof CustomRuntimeException && ((CustomRuntimeException)objThrowable).isisCustomMsg()) {
                    String strMessage = objThrowable.getMessage(); // 구현부에서 사용자 정의 예외 처리된 메시지 획득
                    String[] arrMsg   = strMessage.split(":");     // 메시지를 구분자(":")로 배열 저장

                    intRetCode = Integer.parseInt(arrMsg[0]);
                    strRetMsg  = arrMsg[1];

                    objHttpServletResponse.setStatus(HttpStatus.OK.value());
                } else {
                    objHttpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }

            }
        } catch(Exception ex) {
            log.error("GlobalExceptionHandler Exception : {}", ex.getMessage());

            if(StringUtils.isEmpty(strContentType) || (!strContentType.startsWith(MediaType.APPLICATION_JSON_VALUE))){
                objHttpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }

        return commonUtil.getResJsonView(intRetCode, strRetMsg);
    }
}
