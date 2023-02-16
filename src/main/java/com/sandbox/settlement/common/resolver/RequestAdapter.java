package com.sandbox.settlement.common.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandbox.settlement.common.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.util.JSONUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**--------------------------------------------------------------------
 * ■Request를 Map으로 변환하기 위한 클래스 ■sangheon
 --------------------------------------------------------------------**/
@Slf4j
public class RequestAdapter {

    /**--------------------------------------------------------------------
     * ■Request 객체에서 파라미터를 추출한 후 Map으로 전환하여 리턴해 준다 ■sangheon
     --------------------------------------------------------------------**/
    @SuppressWarnings("unchecked")
    public Map<String, Object> convert(HttpServletRequest nativeRequest) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String[]> objMap = nativeRequest.getParameterMap();
        Map<String, Object> objTmpMap = new HashMap<String, Object>();

        String contentType = nativeRequest.getHeader("content-type");

        // Header content-type이 application/json이 아닌 경우 변환
        if (contentType == null || contentType.indexOf("application/json") < 0 ) {
            objMap.forEach((strKey, objValues)-> {
                if (strKey != null && !"".equals(strKey)) {
                    try {
                        String strValue = StringUtils.arrayToCommaDelimitedString(objValues);
                        if(JSONUtils.mayBeJSON(strValue)) {
                            if (ObjectUtils.isEmpty(strValue)) {
                                objTmpMap.put("data", mapper.readValue(strKey, Map.class));
                            } else {
                                objTmpMap.put("data", mapper.readValue(strValue, Map.class));
                            }
                        } else {
                            objTmpMap.put(strKey,  strValue);
                        }
                    } catch(IOException ex) {
                        log.error("{}", ex.getMessage());
                        throw new GlobalException(ex);
                    }
                }
            });
        }

        return (objTmpMap.containsKey("data"))? (Map<String, Object>)objTmpMap.get("data") : objTmpMap;
    }
}
