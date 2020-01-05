package com.soranico.learn.annotation;

import com.soranico.learn.annotation.enhance.factory.MyBeanDefinitionRegistryPostProcessor;
import com.soranico.learn.annotation.enhance.factory.MyBeanFactoryPostProcessor;
import com.soranico.service.SpringLearnService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
//		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
		AnnotationConfigApplicationContext applicationContext=new AnnotationConfigApplicationContext();
		/**
		 * 外部注册的spring工厂的增强方式，外部注册的需要满足两个条件
		 * 1.一个类implement了BeanFactoryPostProcessor或者
		 * 其子接口BeanDefinitionRegistryPostProcessor
		 * 2.这个类手动调用addBeanFactoryPostProcessor()
		 * 而不是使用@Component交给spring管理
		 *
		 * 注意：如果手动注册，则需要重新调用refresh(),并且使用
		 * AnnotationConfigApplicationContext的空构造方法
		 */
		applicationContext.addBeanFactoryPostProcessor(new MyBeanDefinitionRegistryPostProcessor());
		applicationContext.addBeanFactoryPostProcessor(new MyBeanFactoryPostProcessor());
		applicationContext.register(AppConfig.class);
		applicationContext.refresh();
		SpringLearnService bean = applicationContext.getBean(SpringLearnService.class);
		bean.learn01();
	}

}
