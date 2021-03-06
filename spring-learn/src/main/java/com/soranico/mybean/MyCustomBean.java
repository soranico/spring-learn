package com.soranico.mybean;

import com.soranico.service.MyService01;
import org.springframework.beans.factory.annotation.Autowired;

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
	@Autowired
	private MyBean myBean;
	private MyService01 myService01;

	public MyCustomBean(MyBean myBean) {
		System.err.println("MyCustomBean");
		this.myBean = myBean;
	}

	public MyCustomBean(MyService01 myBean) {
		System.err.println("MyCustomBean");
		this.myService01 = myBean;
	}

	public MyCustomBean() {
		System.err.println("MyCustomBean");
		this.myBean = myBean;
	}

}
