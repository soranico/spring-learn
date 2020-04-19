package com.soranico.learn.annotation;

import com.soranico.learn.annotation.myimport.MyImport;
import com.soranico.learn.annotation.myimport.MyImportBeanDefinitionRegistrar;
import com.soranico.learn.annotation.myimport.MyImportSelector;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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
@Import({MyImport.class, MyImportSelector.class, MyImportBeanDefinitionRegistrar.class})
public class AppConfig {

//	@Bean
//	public SpringBeanService01 springBeanService01() {
//		return new SpringBeanServiceImpl01();
//	}
//
//	@Bean
//	public SpringBeanService02 springBeanService02() {
//		springBeanService01();
//		return new SpringBeanServiceImpl02();
//	}


}
