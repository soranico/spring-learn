package com.soranico.myannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * @title com.soranico.myannotation.MyAnnotation
 * @description
 *        <pre>
 *          注解
 *        </pre>
 *
 * @author soranico
 * @version 1.0
 * @date 2020/5/5
 *
 * </pre>
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {
}
