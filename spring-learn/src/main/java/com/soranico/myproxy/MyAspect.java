package com.soranico.myproxy;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * <pre>
 * @title com.soranico.myproxy.MyAspect
 * @description
 *        <pre>
 *          代理切面
 *        </pre>
 *
 * @author soranico
 * @version 1.0
 * @date 2020/5/2
 *
 * </pre>
 */
@Aspect
public class MyAspect {
	@Around("execution(* com.soranico.service.impl.*.*(..))")
//	@Around("@annotation(com.soranico.myannotation.MyAnnotation)")
	public void proxy() {
		System.err.println("忆夏思乡");
	}
}
