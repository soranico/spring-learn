package com.soranico.service;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Service;

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
@Service
public class MyFactoryBean implements FactoryBean {
	@Override
	public Object getObject() throws Exception {
		return new MyFactoryBean();
	}

	@Override
	public Class<?> getObjectType() {
		return MyFactoryBean.class;
	}
}
