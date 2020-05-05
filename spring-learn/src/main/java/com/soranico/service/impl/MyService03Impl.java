package com.soranico.service.impl;

import com.soranico.service.MyService03;
import org.springframework.aop.SpringProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * @title com.soranico.service.impl.MyService03
 * @description
 *        <pre>
 *          实现SpringProxy
 *        </pre>
 *
 * @author soranico
 * @version 1.0
 * @date 2020/5/5
 *
 * </pre>
 */
@Service
public class MyService03Impl implements SpringProxy, MyService03 {

	@Autowired
	private MyService02 myService02;


	public void test01(){
		System.err.println("SpringProxy");
	}


}
