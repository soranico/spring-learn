package com.soranico.start.annotation.config;

import com.soranico.mybean.MyInterfaceBean;
import org.springframework.context.annotation.Bean;

/**
 * <pre>
 * @title com.soranico.start.annotation.config.AppParent
 * @description
 *        <pre>
 *
 *        </pre>
 *
 * @author soranico
 * @version 1.0
 * @date 2020/5/1
 *
 * </pre>
 */
public interface AppConfigInterface {

	@Bean
	default MyInterfaceBean myInterfaceBean() {
		return new MyInterfaceBean();
	}
}
