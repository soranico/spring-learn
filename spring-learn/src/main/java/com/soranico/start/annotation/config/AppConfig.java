package com.soranico.start.annotation.config;

import com.soranico.mybean.MyBean;
import com.soranico.mybean.MyCustomBean;
import com.soranico.myfactoryBean.MyFactoryBean;
import com.soranico.myproxy.MyAspect;
import org.springframework.context.annotation.*;

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
		"com.soranico.service"
})
@EnableAspectJAutoProxy
//@EnableTransactionManagement
@Import({MyAspect.class, MyFactoryBean.class})
//@Import({MyImport.class, MyImportSelector.class, MyImportBeanDefinitionRegistrar.class})
public class AppConfig extends AppConfigParent implements AppConfigInterface {


//	@Bean
//	public MyBean myStatic(){
//		myStaticBean();
//		return new MyBean();
//	}

	@Bean
	public MyBean myNonStatic(){
//		myNonStaticBean();
		return new MyBean();
	}
//

//	@Bean
//	public MyNonStaticBean myNonStaticBean(){
//		return new MyNonStaticBean();
//	}
//
//	@Bean
//	public static MyStaticBean myStaticBean(){
//		return new MyStaticBean();
//	}



	@Bean
	public MyCustomBean myCustomBean (MyBean myNonStatic){

		return new MyCustomBean();
	}


}
