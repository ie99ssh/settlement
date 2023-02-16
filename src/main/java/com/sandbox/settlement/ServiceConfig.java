package com.sandbox.settlement;

import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter;
import com.sandbox.settlement.common.resolver.ArgumentResolver;
import com.sandbox.settlement.common.security.JWTInterceptor;
import com.sandbox.settlement.common.security.LoginCheckInterceptor;
import com.sandbox.settlement.common.security.XSSFilter;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.List;

/**--------------------------------------------------------------------
 * ■ServiceConfig ■sangheon
 --------------------------------------------------------------------**/
@Configuration
@EnableAsync
public class ServiceConfig implements WebMvcConfigurer {

    @Value("${static.url}")
    private String strStaticUrl;

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();

        templateResolver.setPrefix("classpath:templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(){
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.addDialect(new LayoutDialect());
        return templateEngine;
    }

    @Override
    public void addInterceptors(InterceptorRegistry objRegistry) {

        objRegistry.addInterceptor(loginCheckInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/")
                .excludePathPatterns("/" + strStaticUrl + "/**")
                .excludePathPatterns("/error")
                .excludePathPatterns("/health")
                .excludePathPatterns("/login/login")
                .excludePathPatterns("/login/loginProc")
                .excludePathPatterns("/login/getCSRFToken")
                .excludePathPatterns("/login/adminInitPwd")
                .excludePathPatterns("/login/logout");

        objRegistry.addInterceptor(jwtAppInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/")
                .excludePathPatterns("/" + strStaticUrl + "/**")
                .excludePathPatterns("/error")
                .excludePathPatterns("/login")
                .excludePathPatterns("/health")
                .excludePathPatterns("/login/login")
                .excludePathPatterns("/login/getCSRFToken")
                .excludePathPatterns("/login/resetPwdRequest")
                .excludePathPatterns("/login/logout")
                .excludePathPatterns("/login/adminInitPwd")
                .excludePathPatterns("/administrator/administrator/resetPwd")
                .excludePathPatterns("/administrator/administrator/changePwd")
                .excludePathPatterns("/**.ico");
    }

    @Bean
    public LoginCheckInterceptor loginCheckInterceptor(){
        return new LoginCheckInterceptor();
    }

    @Bean
    public JWTInterceptor jwtAppInterceptor(){
        return new JWTInterceptor();
    }

    @Bean
    public FilterRegistrationBean setXSSFilter() {
        FilterRegistrationBean objRegistrationBean = new FilterRegistrationBean();

        objRegistrationBean.setFilter(new XSSFilter());
        objRegistrationBean.addUrlPatterns("/*");
        objRegistrationBean.setOrder(0);

        return objRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean setXSSEscapeServletFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();

        filterRegistrationBean.setFilter(new XssEscapeServletFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(1);

        return filterRegistrationBean;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> objArgumentResolvers) {
        objArgumentResolvers.add(new ArgumentResolver());
    }

    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

}
