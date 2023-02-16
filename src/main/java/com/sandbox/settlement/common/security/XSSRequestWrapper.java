package com.sandbox.settlement.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**--------------------------------------------------------------------
 * ■XSSRequestWrapper ■sangheon
 --------------------------------------------------------------------**/
@Slf4j
public class XSSRequestWrapper extends HttpServletRequestWrapper {
    
    private byte[] bytRetRequestBody;

    private static final String ENCODING = "UTF-8";

    /**--------------------------------------------------------------------
     * ■생성자(RequestBody로 파라메터를 처리하는 경우 생성자에서 XSS 처리) ■sangheon
     --------------------------------------------------------------------**/
    @SuppressWarnings("unchecked")
    public XSSRequestWrapper(HttpServletRequest httpServletRequest) throws IOException {
        super(httpServletRequest);

        InputStream inputStream = null;
        ObjectMapper objectMapper;

        JSONObject before;
        JSONObject after;

        String strBody = "";

        try {
            inputStream = super.getInputStream();
            objectMapper = new ObjectMapper();

            bytRetRequestBody = IOUtils.toByteArray(inputStream);

            strBody  = new String(bytRetRequestBody, ENCODING);
            after = new JSONObject();

            //Json 값이 없는 경우 또는 Json 값이 아닌 경우
            if(!isJson(strBody)){
                bytRetRequestBody = strBody.getBytes(ENCODING);
                return;
            }

            //Json 값 파싱
            before = getJson(strBody);
            for (Object o : before.keySet()) {
                String strKey = (String) o;

                if (before.get(strKey) instanceof String) {
                    after.put(strKey, escapeXSS((String) before.get(strKey)));
                } else {
                    after.put(strKey, before.get(strKey));
                }
            }

            strBody = after.toJSONString();
            bytRetRequestBody = strBody.getBytes(ENCODING);
        } catch (IOException ex) {
            log.error("Exception Message     : {}", ex.getMessage());
            log.error("Exception Stack Trace : {}", (Object[]) ex.getStackTrace());
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStreamImpl(new ByteArrayInputStream(bytRetRequestBody));
    }

    @Override
    public String[] getParameterValues(String strParameter) {
        String[] arrValues        = super.getParameterValues(strParameter);
        String[] arrEncodedValues = null;

        if (StringUtils.isAnyEmpty(arrValues)) {
            return arrEncodedValues;
        }

        if (arrValues != null) {
            int intCount = arrValues.length;
            arrEncodedValues = new String[intCount];

            for (int i = 0; i < intCount; i++) {
                arrEncodedValues[i] = escapeXSS(arrValues[i]);
            }
        }
        return arrEncodedValues;
    }

    @Override
    public String getParameter(String strParameter) {
        String strValue = super.getParameter(strParameter);

        return escapeXSS(strValue);
    }

    @Override
    public String getHeader(String strName) {
        String strValue = super.getHeader(strName);

        return escapeXSS(strValue);
    }

    /**--------------------------------------------------------------------
     * ■Escape Xss ■sangheon
     --------------------------------------------------------------------**/
    private String escapeXSS(String strValue) {
        return strValue == null ? strValue : StringEscapeUtils.escapeHtml4(strValue);
    }

    public boolean isJson(String strJsonData) {
        boolean blnResult = true;

        JSONParser objParser = new JSONParser();

        try {
            objParser.parse(strJsonData);
        }catch(ParseException ex) {
            blnResult = false;
        }

        return blnResult;
    }

    public JSONObject getJson(String strJsonData){
        JSONParser objParser = new JSONParser();
        JSONObject objReturn = null;

        try {
            objReturn = (JSONObject) objParser.parse(strJsonData);
        }catch(ParseException ex) {
            objReturn = new JSONObject();
        }

        return objReturn;
    }
}

/**--------------------------------------------------------------------
 * ■Request Body 데이터 Stream Read 구현부 ■sangheon
 --------------------------------------------------------------------**/
class ServletInputStreamImpl extends ServletInputStream {
    private final InputStream inputStream;

    public ServletInputStreamImpl(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public int read(byte[] bytValue) throws IOException {
        return inputStream.read(bytValue);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener listener) {
        throw new UnsupportedOperationException();
    }

}
