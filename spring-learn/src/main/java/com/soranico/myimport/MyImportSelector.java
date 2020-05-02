package com.soranico.myimport;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * <pre>
 * @title com.soranico.learn.annotation.myimport.MyImport
 * @description
 *        <pre>
 *          实现ImportSelector
 *        </pre>
 *
 * @author soranico
 * @version 1.0
 * @date 2020/1/5
 *
 * </pre>
 */
public class MyImportSelector implements ImportSelector, BeanFactoryAware {

	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		return new String[]{MyImportSelectorBean.class.getName()};
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

	}
}
