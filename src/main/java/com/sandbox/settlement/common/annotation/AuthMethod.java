package com.sandbox.settlement.common.annotation;

import com.sandbox.settlement.common.util.CommonUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**--------------------------------------------------------------------
 * ■권한 클래스 ■sangheon
 --------------------------------------------------------------------**/
@Target(value=ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthMethod {
    CommonUtil.UserAuth hasAuth() default CommonUtil.UserAuth.READONLY;
}
