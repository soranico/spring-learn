package com.soranico.service.impl;

import com.soranico.myannotation.MyAnnotation;
import com.soranico.service.MyService;
import com.soranico.service.MyService01;
import com.soranico.service.MyServiceMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
@Transactional
public class MyService01Impl implements MyService01 {
	@Autowired
	private MyService02 myService02;

	@Resource
	private MyService myService;

	@Autowired
	private Map<String ,MyServiceMap> myServiceMap;
	@Autowired
	private List<MyServiceMap> myServiceMaps;
	@Autowired
	private MyServiceMap[] myServiceMapArray;

	/**
	 * 默认构造器
	 */
//	private MyService01Impl(){
//
//	}

	/**
	 * rawCandidates.length == 1 && rawCandidates[0].getParameterCount() > 0
	 * 这个条件，注释其他构造器
	 * 有参
	 *
	 * @param name
	 */
//	public MyService01Impl(String name){
//
//	}
//	@Autowired(required = false)
//	public MyService01Impl(String name1,String name2){
//
//	}
//	@Autowired(required = false)
//	public MyService01Impl(String name1,String name2,String name3){
//
//	}
	@Override
	public void service01() {
		System.err.println("Letter Song");
	}

	@PostConstruct
	public void init() {
		System.err.println("MyService01Impl init");
	}

	@PreDestroy
	public void destroy() {
		System.err.println("MyService01Impl destroy");
	}

	@Resource
	private void test01(MyService02 defaultStr) {
		System.err.println("MyService01Impl defaultStr = " + defaultStr);
	}

	@MyAnnotation
	public void service02() {
		System.err.println("坚持吧");
	}
}
