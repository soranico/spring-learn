package com.soranico.learn.annotation;

import com.soranico.service.SpringLearnService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <pre>
 * @title com.soranico.learn.annotation.SpringBeanInitConfig
 * @description
 *        <pre>
 *          使用javaconfig方式初始化bean
 *        </pre>
 *
 * @author soranico
 * @version 1.0
 * @date 2019/12/15
 *
 * </pre>
 */
public class SpringBeanInitConfig {

	public static void main(String[] args) {
		/** 初始化spring容器环境（注解的），ClassPathXmlApplicationContext：XML的 */
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
		SpringLearnService bean = applicationContext.getBean(SpringLearnService.class);
		bean.learn01();
	}

}
