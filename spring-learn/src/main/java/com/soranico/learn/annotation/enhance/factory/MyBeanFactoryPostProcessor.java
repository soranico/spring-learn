package com.soranico.learn.annotation.enhance.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * <pre>
 * @title com.soranico.learn.annotation.enhance.factory.MyBeanFactoryPostProcessor
 * @description
 *        <pre>
 *          自定义增强spring工厂实现方式一
 *        </pre>
 *
 * @author soranico
 * @version 1.0
 * @date 2020/1/5
 *
 * </pre>
 */
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	/**
	 * 这个方法可以增强spring的工厂，会在spring工厂初始化时调用
	 * @param beanFactory the bean factory used by the application context
	 * @throws BeansException
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}
}
