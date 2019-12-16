package com.soranico.service.impl;

import com.soranico.service.SpringLearnService;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * @title com.soranico.service.impl.SpringLearnServiceImpl
 * @description
 *        <pre>
 *          spring学习的service实现
 *        </pre>
 *
 * @author soranico
 * @version 1.0
 * @date 2019/12/15
 *
 * </pre>
 */
@Service
public class SpringLearnServiceImpl implements SpringLearnService {
	/**
	 * @param null
	 * @return
	 * @description spring学习01
	 * @author soranico
	 * @date 2019/12/15
	 * @version 1.0
	 */
	@Override
	public void learn01() {
		System.err.println("bean被扫描到了");
	}
}
