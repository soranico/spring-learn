package com.soranico.service.impl;

import com.soranico.service.SpringBeanService02;

/**
 * <pre>
 * @title com.soranico.service.impl.SpringLearnServiceImpl02
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
public class SpringBeanServiceImpl02 implements SpringBeanService02 {
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
		System.err.println("SpringLearnServiceImpl02被扫描到了");
	}
}
