package com.soranico.service;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * <pre>
 * @title com.soranico.service.ConditionTest
 * @description
 *        <pre>
 *
 *        </pre>
 *
 * @author soranico
 * @version 1.0
 * @date 2020/4/25
 *
 * </pre>
 */
public class ConditionTest implements Condition {
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		return false;
	}
}
