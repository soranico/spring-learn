/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.annotation;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

/**
 * Convenient adapter for programmatic registration of bean classes.
 *
 * <p>This is an alternative to {@link ClassPathBeanDefinitionScanner}, applying
 * the same resolution of annotations but for explicitly registered classes only.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Sam Brannen
 * @author Phillip Webb
 * @see AnnotationConfigApplicationContext#register
 * @since 3.0
 */
public class AnnotatedBeanDefinitionReader {

	private final BeanDefinitionRegistry registry;
	/** beanName生成器 */
	private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
	/** scope解析器 */
	private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
	/** 条件解析器，在spring boot的自动装配加载类阶段大量使用 */
	private ConditionEvaluator conditionEvaluator;


	/**
	 * Create a new {@code AnnotatedBeanDefinitionReader} for the given registry.
	 * <p>If the registry is {@link EnvironmentCapable}, e.g. is an {@code ApplicationContext},
	 * the {@link Environment} will be inherited, otherwise a new
	 * {@link StandardEnvironment} will be created and used.
	 *
	 * @param registry the {@code BeanFactory} to load bean definitions into,
	 *                 in the form of a {@code BeanDefinitionRegistry}
	 * @see #AnnotatedBeanDefinitionReader(BeanDefinitionRegistry, Environment)
	 * @see #setEnvironment(Environment)
	 */
	public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
		/**
		 * 调用对应的构造方法{@link #AnnotatedBeanDefinitionReader(BeanDefinitionRegistry, Environment)}
		 * getOrCreateEnvironment()：获取或创建一个spring环境 它是根据我们使用的spring环境来的
		 * e.g 使用AnnotationConfigApplicationContext就是标准环境。使用AnnotationConfigServletWebServerApplicationContext就是web环境
		 * {@link #getOrCreateEnvironment(BeanDefinitionRegistry)}
		 */
		this(registry, getOrCreateEnvironment(registry));
	}

	/**
	 * Create a new {@code AnnotatedBeanDefinitionReader} for the given registry,
	 * using the given {@link Environment}.
	 *
	 * @param registry    the {@code BeanFactory} to load bean definitions into,
	 *                    in the form of a {@code BeanDefinitionRegistry}
	 * @param environment the {@code Environment} to use when evaluating bean definition
	 *                    profiles.
	 * @since 3.1
	 */
	public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		Assert.notNull(environment, "Environment must not be null");
		/**
		 * 设置AnnotatedBeanDefinitionReader的环境为传递的环境
		 */
		this.registry = registry;
		/**
		 * 初始化bean条件解析器，用于后序解析条件注册bean，spring boot中使用了
		 * {@link ConditionEvaluator#ConditionEvaluator(BeanDefinitionRegistry, Environment, ResourceLoader)}
		 */
		this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);
		/**
		 * registerAnnotationConfigProcessors：注册注解配置处理器，传递当前spring环境
		 * {@link AnnotationConfigUtils#registerAnnotationConfigProcessors(BeanDefinitionRegistry)}
		 */
		AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
	}


	/**
	 * Get the BeanDefinitionRegistry that this reader operates on.
	 */
	public final BeanDefinitionRegistry getRegistry() {
		return this.registry;
	}

	/**
	 * Set the {@code Environment} to use when evaluating whether
	 * {@link Conditional @Conditional}-annotated component classes should be registered.
	 * <p>The default is a {@link StandardEnvironment}.
	 *
	 * @see #registerBean(Class, String, Class...)
	 */
	public void setEnvironment(Environment environment) {
		this.conditionEvaluator = new ConditionEvaluator(this.registry, environment, null);
	}

	/**
	 * Set the {@code BeanNameGenerator} to use for detected bean classes.
	 * <p>The default is a {@link AnnotationBeanNameGenerator}.
	 */
	public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
		this.beanNameGenerator = (beanNameGenerator != null ? beanNameGenerator : new AnnotationBeanNameGenerator());
	}

	/**
	 * Set the {@code ScopeMetadataResolver} to use for registered component classes.
	 * <p>The default is an {@link AnnotationScopeMetadataResolver}.
	 */
	public void setScopeMetadataResolver(@Nullable ScopeMetadataResolver scopeMetadataResolver) {
		this.scopeMetadataResolver =
				(scopeMetadataResolver != null ? scopeMetadataResolver : new AnnotationScopeMetadataResolver());
	}


	/**
	 * Register one or more component classes to be processed.
	 * <p>Calls to {@code register} are idempotent; adding the same
	 * component class more than once has no additional effect.
	 *
	 * @param componentClasses one or more component classes,
	 *                         e.g. {@link Configuration @Configuration} classes
	 */
	public void register(Class<?>... componentClasses) {
		/**
		 * 循环将每个类注册为bd，委托调用registerBean()
		 * {@link #registerBean(Class)}
		 */
		for (Class<?> componentClass : componentClasses) {
			registerBean(componentClass);
		}
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations.
	 * 此方法将Java的类注册为一个bd
	 *
	 * @param beanClass the class of the bean
	 */
	public void registerBean(Class<?> beanClass) {
		/**
		 * 委托调用doRegisterBean()
		 * {@link #doRegisterBean(Class, Supplier, String, Class[], BeanDefinitionCustomizer...)}
		 */
		doRegisterBean(beanClass, null, null, null);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations, using the given supplier for obtaining a new
	 * instance (possibly declared as a lambda expression or method reference).
	 *
	 * @param beanClass        the class of the bean
	 * @param instanceSupplier a callback for creating an instance of the bean
	 *                         (may be {@code null})
	 * @since 5.0
	 */
	public <T> void registerBean(Class<T> beanClass, @Nullable Supplier<T> instanceSupplier) {
		doRegisterBean(beanClass, instanceSupplier, null, null);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations, using the given supplier for obtaining a new
	 * instance (possibly declared as a lambda expression or method reference).
	 *
	 * @param beanClass        the class of the bean
	 * @param name             an explicit name for the bean
	 * @param instanceSupplier a callback for creating an instance of the bean
	 *                         (may be {@code null})
	 * @since 5.0
	 */
	public <T> void registerBean(Class<T> beanClass, String name, @Nullable Supplier<T> instanceSupplier) {
		doRegisterBean(beanClass, instanceSupplier, name, null);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations.
	 *
	 * @param beanClass  the class of the bean
	 * @param qualifiers specific qualifier annotations to consider,
	 *                   in addition to qualifiers at the bean class level
	 */
	@SuppressWarnings("unchecked")
	public void registerBean(Class<?> beanClass, Class<? extends Annotation>... qualifiers) {
		doRegisterBean(beanClass, null, null, qualifiers);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations.
	 *
	 * @param beanClass  the class of the bean
	 * @param name       an explicit name for the bean
	 * @param qualifiers specific qualifier annotations to consider,
	 *                   in addition to qualifiers at the bean class level
	 */
	@SuppressWarnings("unchecked")
	public void registerBean(Class<?> beanClass, String name, Class<? extends Annotation>... qualifiers) {
		doRegisterBean(beanClass, null, name, qualifiers);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations.
	 * 此方法完成将一个Class注册成为BeanDefinition
	 *
	 * @param beanClass             the class of the bean
	 * @param instanceSupplier      a callback for creating an instance of the bean
	 *                              (may be {@code null})
	 * @param name                  an explicit name for the bean
	 * @param qualifiers            specific qualifier annotations to consider, if any,
	 *                              in addition to qualifiers at the bean class level
	 * @param definitionCustomizers one or more callbacks for customizing the
	 *                              factory's {@link BeanDefinition}, e.g. setting a lazy-init or primary flag
	 * @since 5.0
	 */
	<T> void doRegisterBean(Class<T> beanClass, @Nullable Supplier<T> instanceSupplier, @Nullable String name,
							@Nullable Class<? extends Annotation>[] qualifiers, BeanDefinitionCustomizer... definitionCustomizers) {
		/**
		 * 1.通过AnnotatedGenericBeanDefinition将一个加了注解的Class注册为bd
		 * AnnotatedGenericBeanDefinition继承了BeanDefinition，用于描述
		 * 加了注解的Class，{@link AnnotatedGenericBeanDefinition}
		 *
		 * 2.RootBeanDefinition用于描述spring内部的类
		 * {@link org.springframework.beans.factory.support.RootBeanDefinition}
		 */
		AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);

		/**
		 * 是否应该跳过注册
		 * conditionEvaluator是在spring环境初始化时
		 * 创建AnnotatedBeanDefinitionReader实例，然后调用
		 * {@link #AnnotatedBeanDefinitionReader(BeanDefinitionRegistry)}
		 * 通过一系列调用链，最终在{@link #AnnotatedBeanDefinitionReader(BeanDefinitionRegistry, Environment)}
		 * 里实例化的
		 * {@link org.springframework.context.annotation.ConditionEvaluator.ConditionContextImpl#shouldSkip(AnnotatedTypeMetadata)}
		 */
		if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
			return;
		}
		/**
		 * 生成实例的方法
		 * 指定的话，spring在创建对象的时候
		 * 会首先使用这个方法
		 *
		 * Netty在获取执行策略的时候如果没有任务就是用这个方法来选择已到达的事件
		 */
		abd.setInstanceSupplier(instanceSupplier);
		/** 设置bean模式，singleton还是prototype */
		ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
		abd.setScope(scopeMetadata.getScopeName());
		/**
		 * 生成bean的名字
		 * 通过实现{@link BeanNameGenerator}可以自定义bean的name
		 */
		String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry));
		/**
		 * 初始化bean的属性，判断bean是否加了下面的注解
		 * 如果有下面5个注解，则初始化对应属性
		 * lazy，Primary，Role，Description DependsOn
		 *
		 * 基本所有BeanDefinition都会被这个方法处理一遍
		 * {@link AnnotationConfigUtils#processCommonDefinitionAnnotations(AnnotatedBeanDefinition)}
		 */
		AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
		/**
		 * 判断传来参数中是否有继承Primary，Lazy的注解
		 * 如果有，则设置当前BeanDefinition为Primary和Lazy
		 */
		if (qualifiers != null) {
			for (Class<? extends Annotation> qualifier : qualifiers) {
				if (Primary.class == qualifier) {
					abd.setPrimary(true);
				} else if (Lazy.class == qualifier) {
					abd.setLazyInit(true);
				} else {
					abd.addQualifier(new AutowireCandidateQualifier(qualifier));
				}
			}
		}
		for (BeanDefinitionCustomizer customizer : definitionCustomizers) {
			customizer.customize(abd);
		}

		BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
		/**
		 * TODO 不懂
		 * 标记，和代理好像也没有关系
		 */
		definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
		/**
		 * 向spring的工厂里注册一个bd
		 */
		BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
	}


	/**
	 * Get the Environment from the given registry if possible, otherwise return a new
	 * StandardEnvironment.
	 * 获取spring的上下文环境
	 */
	private static Environment getOrCreateEnvironment(BeanDefinitionRegistry registry) {
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		/**
		 * 根据spring上下文的类型返回对应的环境，常用的有
		 * {@link org.springframework.web.context.support.StandardServletEnvironment}（web环境）
		 * {@link StandardEnvironment}（非web环境）
		 * 如果当前上下文没有实现EnvironmentCapable{@link EnvironmentCapable}接口的话
		 * 返回一个标准环境（非web环境）
		 * 如果当前上下文环境实现了EnvironmentCapable接口，则返回当前环境
		 */
		if (registry instanceof EnvironmentCapable) {
			return ((EnvironmentCapable) registry).getEnvironment();
		}
		return new StandardEnvironment();
	}

}
