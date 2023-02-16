package com.sandbox.settlement.common.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.sandbox.settlement.common.annotation.CIField;
import com.sandbox.settlement.common.constants.GlobalConstants;
import com.sandbox.settlement.common.domain.BaseModel;
import com.sandbox.settlement.common.domain.BaseRes;
import com.sandbox.settlement.common.domain.TotalInfoModel;
import com.sandbox.settlement.common.exception.CustomRuntimeException;
import com.sandbox.settlement.common.exception.GlobalException;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class CommonUtil {

    private final String RET_CODE = "intRetCode";
    private final String RET_MSG  = "strRetMsg";
    private final int SALE_MONTH_START = 1;
    private final int SALE_MONTH_END  = 3;

    /**--------------------------------------------------------------------
     * ■IP 주소 조회 ■sangheon
     --------------------------------------------------------------------**/
    public String getIpAddr(HttpServletRequest request){

        String ip = request.getHeader("X-Forwarded-For");
        if (ObjectUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ObjectUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ObjectUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ObjectUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ObjectUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    /**--------------------------------------------------------------------
     * ■UUID ■sangheon
     --------------------------------------------------------------------**/
    public String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-",  "");
    }

    /**--------------------------------------------------------------------
     * ■부분 조회 여부 ■sangheon
     --------------------------------------------------------------------**/
    private boolean isPartialSearch(int intLength) {
        return (intLength > 0);
    }

    /**--------------------------------------------------------------------
     * ■권한 설정 ■sangheon
     --------------------------------------------------------------------**/
    public enum UserAuth {
        ALL(1),
        READONLY(2);

        private final int intVal;

        UserAuth(int intVal) {
            this.intVal = intVal;
        }
        public int getIntVal() {
            return intVal;
        }
    }

    public enum CIFieldType {
        Name(1),
        Account(2),
        Email(3),
        PhoneNo(4),
        CouponNo(5),
        AuthenticationKey(6);

        private int intValue;
        CIFieldType(int intValue) { this.intValue = intValue; }
        public int getIntValue() { return this.intValue; }
    }

    /**--------------------------------------------------------------------
     * ■입력 파리미터 Object 정보 출력(CIField 마스킹 처리) ■sh_shin ■2019-02-25
     --------------------------------------------------------------------**/
    public String getObjectInfo(Object object) throws Throwable {
        StringBuilder objSB = new StringBuilder();

        Class<?> orgin = object.getClass();
        Field[] fieldsArr = orgin.getDeclaredFields();
        List<Field> allFields = new ArrayList<>(Arrays.asList(fieldsArr));

        objSB.append(String.format("%s (", orgin.getSimpleName()));

        for (Field field : allFields) {
            field.setAccessible(true);
            if (isCIField(field)) {
                objSB.append(String.format("CIField %s=%s, ", field.getName(), getCIMaskedValue(field, object)));
            } else {
                objSB.append(String.format("%s=%s, ", field.getName(), field.get(object)));
            }
        }
        objSB.append(")");

        return objSB.toString();
    }

    /**--------------------------------------------------------------------
     * ■CI Field 여부 확인 ■sh_shin
     --------------------------------------------------------------------**/
    public boolean isCIField(Field field) {
        return field.getAnnotation(CIField.class) != null;
    }

    /**--------------------------------------------------------------------
     * ■입력 파리미터 CI Field 값 마스킹 처리 ■sh_shin
     --------------------------------------------------------------------**/
    public String getCIMaskedValue(Field field, Object parameter) throws Throwable {
        String      maskedValue     = null;
        CIFieldType enumCIFiledType = field.getAnnotation(CIField.class).type();

        field.setAccessible(true);

        if(field.get(parameter) != null) {
            String parameterValue  = field.get(parameter).toString();

            switch (enumCIFiledType) {
                case Name:
                    maskedValue = String.format("###%s###", parameterValue);
                    break;
                case Account:
                    maskedValue = String.format("###%s###", parameterValue);
                    break;
                case Email:
                    maskedValue = String.format("###%s###", parameterValue);
                    break;
                case PhoneNo:
                    maskedValue = String.format("###%s###", parameterValue);
                    break;
                case CouponNo:
                    maskedValue = String.format("###%s###", parameterValue);
                    break;
                case AuthenticationKey:
                    maskedValue = "#############";
                    break;

            }
        }

        return maskedValue;
    }

    /**--------------------------------------------------------------------
     * ■출력 파리미터 CI Field 값 마스킹 설정 ■sh_shin
     --------------------------------------------------------------------**/
    public void setCIMaskedValue(Object parameter) {
        try {
            String maskedValue = null;
            Class<?> orgin = parameter.getClass();
            Field[] fieldsArr = orgin.getDeclaredFields();
            List<Field> allFields = new ArrayList<>(Arrays.asList(fieldsArr));

            for (Field field : allFields) {
                if (isCIField(field)) {
                    CIFieldType enumCIFiledType = field.getAnnotation(CIField.class).type();

                    field.setAccessible(true);

                    if (field.get(parameter) != null) {
                        String parameterValue = field.get(parameter).toString();

                        switch (enumCIFiledType) {
                            case Name:
                                maskedValue = String.format("___%s___", parameterValue);
                                break;
                            case Account:
                                maskedValue = String.format("___%s___", parameterValue);
                                break;
                            case Email:
                                maskedValue = String.format("___%s___", parameterValue);
                                break;
                            case PhoneNo:
                                maskedValue = String.format("___%s___", parameterValue);
                                break;
                            case CouponNo:
                                maskedValue = String.format("###%s###", parameterValue);
                                break;
                            case AuthenticationKey:
                                maskedValue = "#############";
                                break;
                        }
                    }

                    field.set(parameter, maskedValue);
                }
            }
        }
        catch (Exception ex) { }
    }

    /**--------------------------------------------------------------------
     * ■모델 유효성 검사 ■sangheon
     --------------------------------------------------------------------**/
    public boolean isVallidCheckModel(BindingResult objBindingResult, Logger logger){
        if(objBindingResult.hasErrors()){
            List<FieldError> objFieldErrorlist = objBindingResult.getFieldErrors();

            logger.error("################################ Model Valid Error Information Start ################################");

            for (FieldError e : objFieldErrorlist)
                logger.error("Error Messsage : {}",e.getDefaultMessage());

            logger.error("################################ Model Valid Error Information End ################################");

            return false;
        }

        return true;
     }

    /**--------------------------------------------------------------------
     * ■익셉션 로깅 처리 ■sangheon
     --------------------------------------------------------------------**/
    public void globalExceptionHandle(Exception ex, Logger logger){
        logger.error("################################ Exception Information Start ################################");
        logger.error("Exception Message     : {}", ex.getMessage());
        logger.error("Exception Stack Trace : {}", (Object[]) ex.getStackTrace());
        logger.error("################################ Exception Information End   ################################");
    }

    /**--------------------------------------------------------------------
     * ■modelAndView 생성 함수 ■sangheon
     --------------------------------------------------------------------**/
    public ModelAndView getResJsonView() {
        BaseRes baseRes = new BaseRes();

        return getResJsonView(baseRes);
    }

    public ModelAndView getResJsonView(BaseRes baseRes) {
        return getResJsonView(null, baseRes);
    }

    public ModelAndView getResJsonView(Object objData) {
        BaseRes baseModelRes = new BaseRes();

        return getResJsonView(objData, baseModelRes);
    }

    public ModelAndView getResJsonView(Object object, BaseRes baseRes) {
        return getResJsonView(object, baseRes.getIntRetCode(), baseRes.getStrRetMsg());
    }

    public ModelAndView getResJsonView(int intRetCode, String strRetMsg) {
        return getResJsonView(null, intRetCode, strRetMsg);
    }

    public ModelAndView getResJsonView(Object object, int intRetCode, String strRetMsg) {
        return getResJsonView(new ModelAndView(new MappingJackson2JsonView()), object, intRetCode, strRetMsg);
    }

    public ModelAndView getResJsonView(ModelAndView mav, Object object, int intRetCode, String strRetMsg) {
        mav.addObject(RET_CODE, intRetCode);
        mav.addObject(RET_MSG,  strRetMsg);

        if(object != null)
            mav.addObject("data", object);

        return mav;
    }

    public ModelAndView getResJsonView(List<?> lstDataTable) {
        ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());

        mav.addObject("data",   lstDataTable);
        mav.addObject(RET_CODE, GlobalConstants.COMMON_SUCCEED_CODE);
        mav.addObject(RET_MSG,  GlobalConstants.COMMON_SUCCEED_MSG);

        return mav;
    }

    public ModelAndView getResJsonView(List<?> objDataTable, BaseModel baseModel) {
        ModelAndView mav = getResJsonView(objDataTable);

        mav.addObject("baseModel",       baseModel);
        mav.addObject("draw",            baseModel.getDraw());
        mav.addObject("recordsTotal",    baseModel.getRecordsTotal());
        mav.addObject("recordsFiltered", baseModel.getRecordsFiltered());

        return mav;
    }

    public ModelAndView getResJsonView(TotalInfoModel model, BaseModel baseModel) {
        ModelAndView mav = getResJsonView(model.getListData());

        mav.addObject("baseModel",       baseModel);
        mav.addObject("draw",            baseModel.getDraw());
        mav.addObject("recordsTotal",    baseModel.getRecordsTotal());
        mav.addObject("recordsFiltered", baseModel.getRecordsFiltered());
        mav.addObject("totalInfo",       model.getTotalInfo());

        return mav;
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

    /**--------------------------------------------------------------------
     * ■시간차 계산 함수 ■sangheon
     --------------------------------------------------------------------**/
    public long getDateTimeDiff(long intStartTime, long intEndTime) {
         long intDiffTime = intEndTime - intStartTime;

         return intDiffTime / (1000 * 60 * 60 * 24);
    }

    /**--------------------------------------------------------------------
     * ■날짜 형 선언 ■sangheon
     --------------------------------------------------------------------**/
    public enum DateFormat{
        YYYYMMDD,
        YYYYMM,
        YYYY,
        YYYYMMDDHH24MISS,
        YYYYMMDDHH24MISS_NOSPACE,
        HH24MISS,
        HH24,
        MI,
        SS,
        YYYYMMDDHH24MISSMS,
        YYYYMMDD2,
        YYYYMM_KOR
    }

    /**--------------------------------------------------------------------
     * ■날짜 형 문자열로 변경 함수 ■sangheon
     --------------------------------------------------------------------**/
    public String convertDate(DateFormat format, Date date) {
        String strRetValue = "";

        SimpleDateFormat simpleDateFormatV1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat simpleDateFormatV2 = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        SimpleDateFormat simpleDateFormatV3 = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat simpleDateFormatV4 = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat simpleDateFormatV5 = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat simpleDateFormatV6 = new SimpleDateFormat("yyyy");
        SimpleDateFormat simpleDateFormatV7 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        SimpleDateFormat simpleDateFormatV8 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormatV9 = new SimpleDateFormat("yyyy년 MM월");

        switch(format) {
            case YYYYMMDDHH24MISS :
                strRetValue = simpleDateFormatV2.format(date);
                break;

            case YYYYMMDDHH24MISS_NOSPACE :
                strRetValue = simpleDateFormatV3.format(date);
                break;

            case HH24MISS :
                strRetValue = simpleDateFormatV4.format(date);
                break;

            case YYYYMM :
                strRetValue = simpleDateFormatV5.format(date);
                break;

            case YYYY :
                strRetValue = simpleDateFormatV6.format(date);
                break;

            case YYYYMMDDHH24MISSMS :
                strRetValue = simpleDateFormatV7.format(date);
                break;

            case YYYYMMDD2 :
                strRetValue = simpleDateFormatV8.format(date);
                break;

            case YYYYMM_KOR :
                strRetValue = simpleDateFormatV9.format(date);
                break;

            case YYYYMMDD :
            default :
                strRetValue = simpleDateFormatV1.format(date);
                break;
        }

        return strRetValue;
    }

    public String convertDate(DateFormat format) {
        return convertDate(format, new Date());
    }

    // 수익년월 (현재-1 ~ 현재 -3, 총 3개월)
    public List<String> getSaleYearMonthList() {

        List<String> saleYearMonthList = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");

        for (int inx=SALE_MONTH_START;inx<=SALE_MONTH_END;inx++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -inx);
            String strSaleYearMonth = dateFormat.format(cal.getTime()).substring(0,6);
            saleYearMonthList.add(strSaleYearMonth);

        }

        return saleYearMonthList;
    }

    private Date add(final Date date, final int calendarField, final int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    public Date addMonths(final Date date, final int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    /**--------------------------------------------------------------------
     * ■디렉토리 확인 함수 ■sangheon
     --------------------------------------------------------------------**/
    public File checkDirByPath(String strPath) {
        StringBuilder strFullPath = new StringBuilder();
        String[]      arrPath     = strPath.split("\\/");
        File          filePath = new File(strPath);

        for(int i = 1; i < arrPath.length; i++) {
            strFullPath.append("/").append(arrPath[i]);

            filePath = new File(strFullPath.toString());

              if (!filePath.isDirectory())
                  filePath.mkdir();
        }

        return filePath;
    }

    /**--------------------------------------------------------------------
     * ■CustomRuntimeException Catch ErrCode, ErrMsg 셋팅 함수 ■sangheon
     --------------------------------------------------------------------**/
    public BaseRes setCatchResult(Exception ex) {
        BaseRes baseModelRes = new BaseRes();

        if (ex instanceof CustomRuntimeException && ((CustomRuntimeException)ex).isisCustomMsg()) {
            String strMessage = ex.getMessage();       // 구현부에서 사용자 정의 예외 처리된 메시지 획득
            String[] arrMsg   = strMessage.split(":");    // 메시지를 구분자(":")로 배열 저장

            baseModelRes.setIntRetCode(Integer.parseInt(arrMsg[0]));
            baseModelRes.setStrRetMsg(arrMsg[1]);
        }

        return baseModelRes;
    }

    /**--------------------------------------------------------------------
     * ■ 엑셀 파일 다운로드 ■ymkang ■2018-11-23
     --------------------------------------------------------------------**/
    public void getExcelDownload(HttpServletResponse response, Map<String, Object> bean, String templateFile, String[]... arrHiddenField)
            throws ParsePropertyException, IOException {
        InputStream  is       = null;
        OutputStream os       = null;
        Workbook     workbook = null;

        ClassPathResource objExcelResource  = new ClassPathResource(GlobalConstants.EXCEL_TEMPLATE_PATH + templateFile);
        InputStream excelIS                 = objExcelResource.getInputStream();
        String fileName                     = String.format(GlobalConstants.EXCEL_DOWNLOAD_FILE_NAME_KOR().get(templateFile), GetToday());
        String docName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        try {
            response.setHeader("Content-Type",         "application/octet-stream");
            response.setHeader("Content-Disposition",  "attachment; filename=" + docName);
            response.setHeader("Content-Description",  "JSP Generated Data");
            response.setHeader("Accept",               "application/vnd.ms-excel");

            Cookie fileDownload = new Cookie("fileDownload","true");
            fileDownload.setPath("/");
            response.addCookie(fileDownload);

            is = new BufferedInputStream(excelIS);
            os = response.getOutputStream();

            if(bean.isEmpty()) {
                workbook = new XSSFWorkbook(is);
            } else {
                XLSTransformer xlst = new XLSTransformer();

                if(arrHiddenField.length == 1) {
                    xlst.setColumnPropertyNamesToHide(arrHiddenField[0]);
                }

                workbook = xlst.transformXLS(is, bean);
            }

            workbook.write(os);
        } catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
            e.printStackTrace();
        } finally {
            assert is != null;
            is.close();
            assert os != null;
            os.flush();
            os.close();
            assert workbook != null;
            workbook.close();
        }
    }

    /**--------------------------------------------------------------------
     * ■ pdf 파일 생성 ■sh_shin ■2018-12-21
     --------------------------------------------------------------------**/
    public String makePDF(String strContents, String strTempPdfPath, InputStream objPDFCssIS) throws GlobalException {

        String strPDFFilePath = "";

        try {

            // PDF 파일 임시 생성
            File tempPDFFile = File.createTempFile("Attach_", ".pdf");
            OutputStream outputStream = new FileOutputStream(tempPDFFile);

            strPDFFilePath = tempPDFFile.getAbsolutePath();

            //Document 생성(용지 및 여백 설정)
            Document objDocument = new Document(PageSize.A4, 0,0,0,0);

            //PdfWriter 생성
            PdfWriter writer = PdfWriter.getInstance(objDocument, outputStream);
            objDocument.open();

            // CSS 설정
            XMLWorkerHelper objXMLWorker = XMLWorkerHelper.getInstance();
            CSSResolver objCssResolver   = new StyleAttrCSSResolver();
            CssFile ojbCssFile 	         = objXMLWorker.getCSS(objPDFCssIS);
            objCssResolver.addCss(ojbCssFile);

            File pdfFontFile = new File(strTempPdfPath + GlobalConstants.PDF_FONT_FILE_NAME);

            // PDF 폰트파일이 초기 생성되지 않은 상태인지 체크하고 생성되지 않은 경우 생성.
            if (!pdfFontFile.exists()) {

                ClassPathResource pdfFontFileResource = new ClassPathResource(GlobalConstants.PDF_FONT_FILE_PATH);
                InputStream pdfFontIS = pdfFontFileResource.getInputStream();

                File dirPdfFont = new File(strTempPdfPath);

                dirPdfFont.mkdirs();    //디렉토리 생성
                FileUtils.copyInputStreamToFile(pdfFontIS, pdfFontFile);
            }

            // 폰트 설정(fontalias - 별칭)
            XMLWorkerFontProvider objFontProvider = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
            objFontProvider.register(pdfFontFile.getAbsolutePath(), "fontalias");
            CssAppliers objCssAppliers = new CssAppliersImpl(objFontProvider);

            HtmlPipelineContext objHtmlContext = new HtmlPipelineContext(objCssAppliers);
            objHtmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());

            PdfWriterPipeline   objPdf  = new PdfWriterPipeline(objDocument, writer);
            HtmlPipeline        objHtml = new HtmlPipeline(objHtmlContext, objPdf);
            CssResolverPipeline objCss  = new CssResolverPipeline(objCssResolver, objHtml);

            XMLWorker objWorker    = new XMLWorker(objCss, true);
            XMLParser objXmlParser = new XMLParser(objWorker, StandardCharsets.UTF_8);

            objXmlParser.parse(new StringReader(strContents));

            //Document&Writer close
            objDocument.close();
            writer.close();
            outputStream.close();


        } catch (Exception objException) {
            throw new GlobalException(objException);
        }

        return strPDFFilePath;
    }

    /**--------------------------------------------------------------------
     * ■ pdf 파일 삭제 ■sh_shin ■2018-12-21
     --------------------------------------------------------------------**/
    public void deletePDF(String strPDFFileName) throws GlobalException {
        try{
            ClassPathResource   objPDFFile  = new ClassPathResource("static/htdocs/PDF/files/" + strPDFFileName);
            File                obfile      = objPDFFile.getFile();

            if (!obfile.exists()) {
                return;
            } else {
                obfile.delete();
            }
        }
        catch(Exception objException){
            throw new GlobalException(objException);
        }
    }

    /**--------------------------------------------------------------------
     * ■ pdf 파일 삭제 (복수개) ■sh_shin ■2018-12-21
     --------------------------------------------------------------------**/
    public void deletePDF(List<String> arrPDFFilePathList) throws GlobalException {
        try{
            if(arrPDFFilePathList.size() > 0) {
                for(String strPDFFilePath : arrPDFFilePathList) {
                    File objFile = new File(strPDFFilePath);

                    if (objFile.exists()) {
                        objFile.delete();
                    }
                }
            }
        }
        catch(Exception objException){
            throw new GlobalException(objException);
        }
    }

    /**--------------------------------------------------------------------
     * ■ 바로빌 적격증빙자료 코드를 홈택스 코드로 변환 ■sh_shin ■2019-02-07
     --------------------------------------------------------------------**/
    public String convertEvidenceTypecode(int intBarobillEvidenceTypeCode) throws GlobalException {
        String strHometaxEvidenceTypeCode = "";

        switch (intBarobillEvidenceTypeCode) {
            case 1:     //세금계산서
                strHometaxEvidenceTypeCode = "01";
                break;
            case 2:     //계산서
                strHometaxEvidenceTypeCode = "05";
                break;
            case 4:     //위수탁세금계산서
                strHometaxEvidenceTypeCode = "03";
                break;
            case 5:     //위수탁계산서
                strHometaxEvidenceTypeCode = "06";
                break;
            default:
                break;
        }

        if(strHometaxEvidenceTypeCode.isEmpty()){
            throw new GlobalException("지원하지 않는 적격증빙자료 유형 입니다.");
        }

        return strHometaxEvidenceTypeCode;
    }

    public String GetToday() throws GlobalException{

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar                 = Calendar.getInstance();

        return simpleDateFormat.format(calendar.getTime());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public OrderSpecifier<?> getOrderSpecifier(Class classObject, String key, String direction, String fieldName) {

        Order order;

        if (ObjectUtils.isEmpty(fieldName)) fieldName = key;

        if (ObjectUtils.isEmpty(direction)) {
            order = Order.DESC;
        } else {
            if (direction.equalsIgnoreCase("ASC")) order = Order.ASC;
            else order = Order.DESC;
        }

        PathBuilder<?> pathBuilder
                = new PathBuilder(classObject, fieldName);
        return new OrderSpecifier(order, pathBuilder);
    }

    public LocalDateTime getFromDate(String fromYmd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(fromYmd + " 00:00:00", formatter);
    }

    public LocalDateTime getToDate(String toYmd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return LocalDateTime.parse(toYmd + " 23:59:59.999", formatter);
    }

}
