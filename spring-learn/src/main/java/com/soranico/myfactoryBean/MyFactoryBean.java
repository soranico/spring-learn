package com.soranico.myfactoryBean;

import org.springframework.beans.factory.FactoryBean;

/**
 * <pre>
 * @title com.soranico.myfactoryBean.MyFactoryBean
 * @description
 *        <pre>
 *          factoryBean
 *        </pre>
 *
 * @author soranico
 * @version 1.0
 * @date 2020/5/4
 *
 * </pre>
 */
public class MyFactoryBean implements FactoryBean {
	@Override
	public Object getObject() throws Exception {
		return new MyFactoryService();
	}

	@Override
	public Class<?> getObjectType() {
		return MyFactoryService.class;
	}
}
