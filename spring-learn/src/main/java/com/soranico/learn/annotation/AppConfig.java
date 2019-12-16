package com.soranico.learn.annotation;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * @title com.soranico.learn.annotation.AppConfig
 * @description
 *        <pre>
 *          javaconfig扫描
 *        </pre>
 *
 * @author soranico
 * @version 1.0
 * @date 2019/12/15
 *
 * </pre>
 */
@Configuration
@ComponentScan(basePackages = {
		"com.soranico.service.**.**"
})
public class AppConfig {
}
