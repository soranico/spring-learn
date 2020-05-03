package com.soranico.service.impl;

import com.soranico.service.MyService01;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * @title com.soranico.service.impl.MyService01
 * @description
 *        <pre>
 *          循环依赖1
 *        </pre>
 *
 * @author soranico
 * @version 1.0
 * @date 2020/5/3
 *
 * </pre>
 */
@Service
public class MyService01Impl implements MyService01 {
	@Autowired
	private MyService02 myService02;

	@Override
	public void service01() {
		System.err.println("Letter Song");
	}
}
