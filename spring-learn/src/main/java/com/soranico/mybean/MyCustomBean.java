package com.soranico.mybean;

/**
 * <pre>
 * @title com.soranico.learn.annotation.mybean.MyBean
 * @description
 *        <pre>
 *          普通类
 *        </pre>
 *
 * @author soranico
 * @version 1.0
 * @date 2020/5/1
 *
 * </pre>
 */
public class MyCustomBean {
	private MyBean myBean;
	public MyCustomBean(MyBean myBean){
		System.err.println("MyCustomBean");
		this.myBean=myBean;
	}


}