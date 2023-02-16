package com.sandbox.settlement.common.service;

import com.sandbox.settlement.common.constants.GlobalConstants;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * --------------------------------------------------------------------
 * ■메일 전송 서비스 구현부 ■sangheon
 * --------------------------------------------------------------------
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class SendMailService {

    private final JavaMailSender javaMailSender;

    @Value("${temp.file.image.path}")
    private String strImageFilePath;

    @Value("${mail.type}")
    private String strMailType;

    @Value("${mailgun.api.key}")
    private String strMailGunApiKey;

    @Value("${mailgun.api.url}")
    private String strMailGunApiUrl;

    @Value("${mail.send.flag}")
    private String strMailSendFlag;

    @Value("${spring.profiles.active}")
    private String strServerEnvironment;

    @Value("${mail.toEmail}")
    private String strTestEMailAddress;

    /**
     * --------------------------------------------------------------------
     * ■메일 발송 ■ymjo
     * --------------------------------------------------------------------
     **/
    public boolean sendMail(String strTo, String strFrom, String strSubject, String strContent, String strImagePath) throws Exception {

        if ("false".equals(strMailSendFlag)) {
            return true;
        }

        if(!"prod".equals(strServerEnvironment.toLowerCase())) {
            strTo = strTestEMailAddress;
        }

        if ("mailgun".equals(strMailType)) {
            return sendMailGun(strTo, strFrom, strSubject, strContent, strImagePath );
        } else {
            return sendMailSMTP(strTo, strFrom, strSubject, strContent, strImagePath );
        }
    }

    public boolean sendMail(String strTo, String strFrom, String strSubject, String strContent, List<String> arrFilePathList, String strImagePath) throws Exception {
        if(!"prod".equals(strServerEnvironment.toLowerCase())) {
            strTo = strTestEMailAddress;
        }

        if (!"false".equals(strMailSendFlag)) {

            if ("mailgun".equals(strMailType)) {
                return sendMailGun(strTo, strFrom, strSubject, strContent, arrFilePathList, strImagePath );
            } else {
                return sendMailSMTP(strTo, strFrom, strSubject, strContent, arrFilePathList, strImagePath );
            }

        }
        return true;
    }

    public boolean sendMailSMTP(String strTo, String strFrom, String strSubject, String strContent, String strImagePath) {

        MimeMessagePreparator objMimeMessagePreparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws AddressException, MessagingException {
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(strTo));
                mimeMessage.setFrom(new InternetAddress(strFrom));
                mimeMessage.setSubject(strSubject);

                // This mail has 2 part, the BODY, and the embedded image
                MimeMultipart multipart = new MimeMultipart("related");

                // fisrt part (the html)
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(strContent, "text/html; charset=" + GlobalConstants.SYSTEM_ENCODING);
                // add it
                multipart.addBodyPart(messageBodyPart);

                // second part (the image)
                if (strImagePath != null && !"".equals(strImagePath)) {
                    messageBodyPart = new MimeBodyPart();
                    DataSource fds = new FileDataSource(strImagePath);

                    messageBodyPart.setDataHandler(new DataHandler(fds));
                    messageBodyPart.setHeader("Content-ID", "<image>");

                    // add image to the multipart
                    multipart.addBodyPart(messageBodyPart);
                }

                mimeMessage.setContent(multipart);
            }
        };

        javaMailSender.send(objMimeMessagePreparator);

        return true;

    }

    public boolean sendMailGun(String strTo, String strFrom, String strSubject, String strContent, String strImagePath) {

        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api", strMailGunApiKey));
        WebResource webResource = client.resource("https://api.mailgun.net/v3/" + strMailGunApiUrl
                + "/messages");
        FormDataMultiPart formData = new FormDataMultiPart();
        formData.field("from", strFrom);
        formData.field("to", strTo);
        formData.field("subject", strSubject);
        formData.field("html", strContent);

        ClientResponse response = webResource.type(MediaType.MULTIPART_FORM_DATA_TYPE).post(ClientResponse.class, formData);

        log.error("mailgun send result : " + response.toString());

        return true;

    }

    /**
     * --------------------------------------------------------------------
     * ■메일 발송(첨부파일, 인라인 이미지) ■sh_shin ■2018-12-24
     * --------------------------------------------------------------------
     **/
    public boolean sendMail(String strTo, String strFrom, String strSubject, String strContent, String strFilePath, String strFileName, String strImagePath) {
        MimeMessagePreparator objMimeMessagePreparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper objMimeMessagehelper = new MimeMessageHelper(mimeMessage, true, GlobalConstants.SYSTEM_ENCODING);

                objMimeMessagehelper.setTo(new InternetAddress(strTo));
                objMimeMessagehelper.setFrom(new InternetAddress(strFrom));
                objMimeMessagehelper.setSubject(strSubject);
                objMimeMessagehelper.setText(strContent, true);

                if (strFilePath != "") {
                    FileSystemResource objFileAttachment = new FileSystemResource(new File(strFilePath));
                    objMimeMessagehelper.addAttachment(MimeUtility.encodeText(strFileName), objFileAttachment); //첨부 파일 등록
                }

                if (strImagePath != "") {
                    FileSystemResource objImagePath = new FileSystemResource(new File(strImagePath));
                    objMimeMessagehelper.addInline("logo", objImagePath);                                //메일 내 이미지 등록.
                }
            }
        };

        javaMailSender.send(objMimeMessagePreparator);

        return true;
    }

    /**
     * --------------------------------------------------------------------
     * ■메일 발송(복수개 첨부파일, 인라인 이미지) ■sh_shin ■2018-12-24
     * --------------------------------------------------------------------
     **/
    public boolean sendMailSMTP(String strTo, String strFrom, String strSubject, String strContent, List<String> arrFilePathList, String strImagePath) {

        MimeMessagePreparator objMimeMessagePreparator = new MimeMessagePreparator() {

            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper objMimeMessagehelper = new MimeMessageHelper(mimeMessage, true, GlobalConstants.SYSTEM_ENCODING);

                objMimeMessagehelper.setTo(new InternetAddress(strTo));
                objMimeMessagehelper.setFrom(new InternetAddress(strFrom));
                objMimeMessagehelper.setSubject(strSubject);
                objMimeMessagehelper.setText(strContent, true);

                if (arrFilePathList.size() > 0) {
                    for (String strFilePath : arrFilePathList) {
                        FileSystemResource objFileAttachment = new FileSystemResource(new File(strFilePath));
                        objMimeMessagehelper.addAttachment(MimeUtility.encodeText(objFileAttachment.getFilename()), objFileAttachment); //첨부 파일 등록
                    }
                }

                if(strImagePath != "") {

                    File imageFile = new File(strImageFilePath + GlobalConstants.PDF_LOGO_IMAGE_FILE_NAME);

                    // 이미지 파일이 초기 생성되지 않은 상태인지 체크하고 생성되지 않은 경우 생성.
                    if (!imageFile.exists()) {

                        ClassPathResource imageFileResource = new ClassPathResource(strImagePath);
                        InputStream imageIS = imageFileResource.getInputStream();

                        File dirImageFont = new File(strImageFilePath);

                        dirImageFont.mkdirs();    //디렉토리 생성
                        FileUtils.copyInputStreamToFile(imageIS, imageFile);
                    }


                    FileSystemResource objImageResource = new FileSystemResource(imageFile);
                    objMimeMessagehelper.addInline("logo", objImageResource);

                }
            }
        };

        javaMailSender.send(objMimeMessagePreparator);

        return true;
    }

    // mailgun 으로 전송
    private boolean sendMailGun(String strTo, String strFrom, String strSubject, String strContent, List<String> arrFilePathList, String strImagePath) throws Exception {

        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api", strMailGunApiKey));
        WebResource webResource = client.resource("https://api.mailgun.net/v3/" + strMailGunApiUrl
                + "/messages");
        FormDataMultiPart formData = new FormDataMultiPart();
        formData.field("from", strFrom);
        formData.field("to", strTo);
        formData.field("subject", strSubject);
        formData.field("html", strContent);

        if (arrFilePathList.size() > 0) {
            for (String strFilePath : arrFilePathList) {
                formData.bodyPart(new FileDataBodyPart("attachment", new File(strFilePath), MediaType.valueOf("application/pdf")));
            }
        }

        ClientResponse response = webResource.type(MediaType.MULTIPART_FORM_DATA_TYPE).post(ClientResponse.class, formData);

        log.error("mailgun send result : " + response.toString());

        return true;
    }
}
