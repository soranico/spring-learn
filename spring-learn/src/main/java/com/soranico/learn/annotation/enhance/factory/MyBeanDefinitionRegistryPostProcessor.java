package com.soranico.learn.annotation.enhance.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * <pre>
 * @title com.soranico.learn.annotation.enhance.factory.MyBeanDefinitionRegistryPostProcessor
 * @description
 *        <pre>
 *          增强spring的工厂的实现方式二
 *        </pre>
 *
 * @author soranico
 * @version 1.0
 * @date 2020/1/5
 *
 * </pre>
 */
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	/**
	 * 这个方法是BeanDefinitionRegistryPostProcessor特有的
	 * spring工厂在初始化之前会先调用这个方法，然后再调用父接口
	 * 的方法，即下面的方法
	 * @param registry the bean definition registry used by the application context
	 * @throws BeansException
	 */
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}
}
