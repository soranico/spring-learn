package com.soranico.start.annotation;

import com.soranico.mybean.MyCustomBean;
import com.soranico.myextend.factory.MyBeanDefinitionRegistryPostProcessor;
import com.soranico.myextend.factory.MyBeanFactoryPostProcessor;
import com.soranico.myfactoryBean.MyFactoryService;
import com.soranico.service.MyService01;
import com.soranico.start.annotation.config.AppConfig;
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
public class App {

	public static void main(String[] args) {
		/** 初始化spring容器环境（注解的），ClassPathXmlApplicationContext：XML的 */
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

//		applicationContext.getBean(MyStaticBean.class);
//		applicationContext.getBean(MyStaticBean.class);
		applicationContext.getBean(MyCustomBean.class);
		applicationContext.getBean(MyFactoryService.class);

//		MyService service = applicationContext.getBean(MyService.class);
		MyService01 service01 = applicationContext.getBean(MyService01.class);
//
		service01.service01();
//		service.service01();
	}

}
