package com.soranico.learn.annotation.myimport;

import com.soranico.service.impl.MyImportSelectorServiceImpl;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * <pre>
 * @title com.soranico.learn.annotation.myimport.MyImport
 * @description
 *        <pre>
 *          实现ImportSelector
 *        </pre>
 *
 * @author soranico
 * @version 1.0
 * @date 2020/1/5
 *
 * </pre>
 */
public class MyImportSelector implements ImportSelector {

	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		return new String[]{MyImportSelectorServiceImpl.class.getName()};
	}
}
