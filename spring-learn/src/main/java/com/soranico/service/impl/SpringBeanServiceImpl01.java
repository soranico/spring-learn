package com.soranico.service.impl;

import com.soranico.service.SpringBeanService01;

/**
 * <pre>
 * @title com.soranico.service.impl.SpringLearnServiceImpl
 * @description
 *        <pre>
 *          使用@Bean注解
 *        </pre>
 *
 * @author soranico
 * @version 1.0
 * @date 2019/12/15
 *
 * </pre>
 */
//@Service
public class SpringBeanServiceImpl01 implements SpringBeanService01 {
	/**
	 * @para null
	 * @return
	 * @description spring学习01
	 * @author soranico
	 * @date 2019/12/15
	 * @version 1.0
	 */
	@Override
	public void learn01() {
		System.err.println("bean被扫描到了");
	}
}
