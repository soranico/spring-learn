package com.soranico.service;

import org.springframework.beans.factory.FactoryBean;

/**
 * <pre>
 * @title com.soranico.service.MyFactoryBean
 * @description
 *        <pre>
 *          测试FactoryBean
 *        </pre>
 *
 * @author soranico
 * @version 1.0
 * @date 2020/1/5
 *
 * </pre>
 */
public class MyFactoryBean implements FactoryBean {
	@Override
	public Object getObject() throws Exception {
		return null;
	}

	@Override
	public Class<?> getObjectType() {
		return null;
	}
}
