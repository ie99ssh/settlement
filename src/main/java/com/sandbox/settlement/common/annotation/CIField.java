package com.sandbox.settlement.common.annotation;

import com.sandbox.settlement.common.util.CommonUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**--------------------------------------------------------------------
 * ■CI Field 어노테이션 ■sh_shin
 --------------------------------------------------------------------**/
@Target(value= ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CIField {
    CommonUtil.CIFieldType type();
}
