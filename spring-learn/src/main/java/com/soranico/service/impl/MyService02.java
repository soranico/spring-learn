package com.soranico.service.impl;

import com.soranico.service.MyService01;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
public class MyService02 {
	@Resource
	private MyService01 myService01;

	public void service02(){
		System.err.println("MyService02  -- >service02()");
	}
}
