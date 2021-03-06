/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.context.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * Delegate for AbstractApplicationContext's post-processor handling.
 *
 * @author Juergen Hoeller
 * @since 4.0
 */
final class PostProcessorRegistrationDelegate {

	private PostProcessorRegistrationDelegate() {
	}


	public static void invokeBeanFactoryPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

		// Invoke BeanDefinitionRegistryPostProcessors first, if any.
		Set<String> processedBeans = new HashSet<>();
		/**
		 * 当前bean工厂是不是BeanDefinitionRegister类型
		 * BeanDefinitionRegister是spring提供的公共bean工厂接口
		 * {@link BeanDefinitionRegistry}
		 */
		if (beanFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
			/**
			 * regularPostProcessors：存放实现了BeanFactoryPostProcessor的类
			 * registryProcessors：存放是实现了子接口BeanDefinitionRegistryPostProcessor的类
			 */
			List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

			/**
			 * 首先将自定义的spring工厂增强类放入集合
			 * 这里需要注意，如果增强类实现的是BeanDefinitionRegistryPostProcessor接口
			 * 则会先执行postProcessBeanDefinitionRegistry()，然后再存放到集合
			 *
			 * 注意：
			 * 当前只是执行方法，没有放入到spring的容器中
			 */
			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					BeanDefinitionRegistryPostProcessor registryProcessor =
							(BeanDefinitionRegistryPostProcessor) postProcessor;
					/** 是BeanDefinitionRegistryPostProcessor接口实现类，先执行，在存放 */
					registryProcessor.postProcessBeanDefinitionRegistry(registry);
					registryProcessors.add(registryProcessor);
				} else {
					regularPostProcessors.add(postProcessor);
				}
			}

			// Do not initialize FactoryBeans here: We need to leave all regular beans
			// uninitialized to let the bean factory post-processors apply to them!
			// Separate between BeanDefinitionRegistryPostProcessors that implement
			// PriorityOrdered, Ordered, and the rest.
			/**
			 *存放spring内部的和@Component标记的实现BeanDefinitionRegistryPostProcessor的类
			 */
			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

			// First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered.
			/**
			 * 从bean工厂里获取实现了BeanDefinitionRegistryPostProcessor的类
			 * 非手动调用方法注册的
			 */
			String[] postProcessorNames =
					beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			/**
			 * 当前bean是否实现了PriorityOrdered接口
			 * spring内部添加的ConfigurationClassPostProcessor实现了这个接口
			 * {@link PriorityOrdered}
			 */
			for (String ppName : postProcessorNames) {
				/**
				 * spring的singletonObjects里有3个bean
				 * 1.systemEnvironment
				 * 2.environment
				 * 3.systemProperties
				 */
				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
					/**
					 * 从singletonObjects中取出BeanDefinitionRegistryPostProcessor类型的bean
					 * 没有则创建并放入singletonObjects
					 * spring内部的会放入到容器中，手动添加的不会放入
					 * 那么@Component的呢？TODO
					 */
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}


			/** 对BeanDefinitionRegistryPostProcessor进行排序 */
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			/** 将外部添加的和spring内部的BeanDefinitionRegistryPostProcessor合并 */
			registryProcessors.addAll(currentRegistryProcessors);

			/**
			 * 排序后依次执行实现了BeanDefinitionRegistryPostProcessor接口的类
			 * 执行方法为postProcessBeanDefinitionRegistry()
			 * 注意：此处执行的是spring内部的，包括使用@Component交给spring的
			 * 使用addBeanFactoryPostProcessor()手动添加的已经执行完了
			 * {@link #invokeBeanDefinitionRegistryPostProcessors(Collection, BeanDefinitionRegistry)}
			 */
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);

			/** 清空集合，释放内存 */
			currentRegistryProcessors.clear();

			// Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered.
			/**
			 * 从bean工厂里获取所有实现了BeanDefinitionRegistryPostProcessor的类
			 * 非手动调用方法注册
			 * 使用注解扫描的之前的扫描解析中已经放入到BDMap，所以可以在这里
			 * 获取到
			 */
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			/**
			 * bean实现了Ordered接口，没有实现PriorityOrdered接口
			 * 并且之前没有解析
			 */
			for (String ppName : postProcessorNames) {
				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}
			/**
			 * 排序后依次执行bean工厂后置处理器
			 */
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			registryProcessors.addAll(currentRegistryProcessors);
			/**
			 * 执行没有实现PriorityOrder并且实现了Ordered没有处理的后置处理器
			 */
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			currentRegistryProcessors.clear();

			// Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
			boolean reiterate = true;
			while (reiterate) {
				reiterate = false;
				/**
				 * 取出所有实现了BeanDefinitionRegistryPostProcessor接口
				 * 并且没有实现PriorityOrdered和Ordered接口的类
				 */
				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
				for (String ppName : postProcessorNames) {
					if (!processedBeans.contains(ppName)) {
						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
						processedBeans.add(ppName);
						reiterate = true;
					}
				}
				sortPostProcessors(currentRegistryProcessors, beanFactory);
				registryProcessors.addAll(currentRegistryProcessors);
				// 执行postProcessBeanDefinitionRegistry()
				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
				currentRegistryProcessors.clear();
			}

			// Now, invoke the postProcessBeanFactory callback of all processors handled so far.
			/**
			 * 调用所有实现了BeanDefinitionRegistryPostProcessor接口的类
			 * 调用的方法是父接口的方法，之前调用方法为子接口特有的方法
			 * {@link #invokeBeanFactoryPostProcessors(Collection, ConfigurableListableBeanFactory)}
			 */
			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);

			/**
			 * 调用所有实现了BeanFactoryPostProcessor接口的类
			 * 调用当前接特有的方法
			 * {@link #invokeBeanFactoryPostProcessors(Collection, ConfigurableListableBeanFactory)}
			 */
			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
		} else {
			// Invoke factory processors registered with the context instance.
			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
		}

		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let the bean factory post-processors apply to them!
		/**
		 * 之前取出来的都是实现BeanDefinitionRegistryPostProcessor的类
		 * 实现BeanFactoryPostProcessor并没有取出来，所以要取出来执行
		 * 取出使用非手动注册的，因为手动注册的没有放入到BDMap中
		 * 交给spring管理的和spring内部的都在BDMap中
		 */
		String[] postProcessorNames =
				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

		// Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		// 实现PriorityOrdered接口的
		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		// 实现Ordered接口的
		List<String> orderedPostProcessorNames = new ArrayList<>();
		// 两者都没有实现
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			/**
			 * 之前没有执行过的
			 */
			if (processedBeans.contains(ppName)) {
				// skip - already processed in first phase above
			} else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
			} else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			} else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
		// 排序
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		// 执行postProcessorBeanFactory()
		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

		// Next, invoke the BeanFactoryPostProcessors that implement Ordered.
		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>();
		/**
		 * 取出Bean 执行，不存在实例化Bean
		 */
		for (String postProcessorName : orderedPostProcessorNames) {
			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

		// Finally, invoke all other BeanFactoryPostProcessors.
		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String postProcessorName : nonOrderedPostProcessorNames) {
			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}// 执行EventListenerMethodProcessor的
		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

		// Clear cached merged bean definitions since the post-processors might have
		// modified the original metadata, e.g. replacing placeholders in values...
		beanFactory.clearMetadataCache();
	}

	public static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {
		/**
		 * 获取初始化spring环境，即实例AnnotatedBeanDefinitionReader
		 * 的时候调用AnnotationConfigUtils#registerAnnotationConfigProcessors()
		 * 添加的
		 * AutowiredAnnotationBeanPostProcessor继承MergedBeanDefinitionPostProcessor 继承InstantiationAwareBeanPostProcessor 实现PriorityOrdered接口
		 * CommonAnnotationBeanPostProcessor继承InstantiationAwareBeanPostProcessor 间接实现PriorityOrdered接口
		 * 代理的话 还会有代理的BD
		 * 这步很重要
		 */
		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

		// Register BeanPostProcessorChecker that logs an info message when
		// a bean is created during BeanPostProcessor instantiation, i.e. when
		// a bean is not eligible for getting processed by all BeanPostProcessors.
		/**
		 * beanFactory.getBeanPostProcessorCount() 从beanPostProcessors(List)获取
		 * 在准备spring工厂后AbstractApplicationContext#prepareBeanFactory()添加的
		 *
		 * ApplicationContextAwareProcessor实现BeanPostProcessor
		 *
		 * ApplicationListenerDetector继承MergedBeanDefinitionPostProcessor和DestructionAwareBeanPostProcessor
		 *
		 * 在执行beanFactory的ConfigurationClassPostProcessor#postProcessBeanFactory()添加的一个
		 *
		 * ImportAwareBeanPostProcessor继承InstantiationAwareBeanPostProcessor和SmartInstantiationAwareBeanPostProcessor
		 * 注意 ： 都没有经过bean过程，而是直接添加
		 */
		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
		/**
		 * 添加后置处理器 并没有经过bean的过程
		 * BeanPostProcessorChecker实现BeanPostProcessor
		 */
		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

		// Separate between BeanPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.

		// 存放实现PriorityOrdered接口的
		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();

		// 存放实现PriorityOrdered和MergedBeanDefinitionPostProcessor
		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();

		// 存到实现Ordered
		List<String> orderedPostProcessorNames = new ArrayList<>();

		// 存放普通的
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();

		for (String ppName : postProcessorNames) {
			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				/**
				 * 从spring单例池singletonObjects获取
				 * bean后置处理器，不存在则实例化，在将后置处理器放入beanFactory之前先实例为bean
				 */
				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
				priorityOrderedPostProcessors.add(pp);
				if (pp instanceof MergedBeanDefinitionPostProcessor) {
					internalPostProcessors.add(pp);
				}
			} else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {// 实现Ordered接口
				orderedPostProcessorNames.add(ppName);
			} else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, register the BeanPostProcessors that implement PriorityOrdered.
		// 排序
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		/**
		 * 1.执行实现PriorityOrdered的
		 * AutowiredAnnotationBeanPostProcessor
		 * CommonAnnotationBeanPostProcessor
		 */
		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

		// Next, register the BeanPostProcessors that implement Ordered.
		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>();
		for (String ppName : orderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			orderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		/**
		 * 2.注册实现Ordered接口的到beanFactory
		 */
		registerBeanPostProcessors(beanFactory, orderedPostProcessors);

		// Now, register all regular BeanPostProcessors.
		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String ppName : nonOrderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			nonOrderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}

		/**
		 * 3.都没有实现的
		 */
		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);

		// Finally, re-register all internal BeanPostProcessors.
		sortPostProcessors(internalPostProcessors, beanFactory);
		/**
		 * 4.注册MergedBeanDefinitionPostProcessor 这两个之前注册过，先移除再添加到list
		 * AutowiredAnnotationBeanPostProcessor
		 * CommonAnnotationBeanPostProcessor
		 */
		registerBeanPostProcessors(beanFactory, internalPostProcessors);

		// Re-register post-processor for detecting inner beans as ApplicationListeners,
		// moving it to the end of the processor chain (for picking up proxies etc).
		/**
		 * 5.重新添加后置处理器ApplicationListenerDetector
		 * 到最后
		 * TODO 不懂 为什么要添加到最后
		 */
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
	}

	private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		Comparator<Object> comparatorToUse = null;
		if (beanFactory instanceof DefaultListableBeanFactory) {
			comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
		}
		if (comparatorToUse == null) {
			comparatorToUse = OrderComparator.INSTANCE;
		}
		postProcessors.sort(comparatorToUse);
	}

	/**
	 * 循环执行实现了BeanDefinitionRegistryPostProcessor
	 * 接口里的postProcessBeanDefinitionRegistry()
	 * Invoke the given BeanDefinitionRegistryPostProcessor beans.
	 */
	private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {

		/**
		 * 循环调用实现类的postProcessBeanDefinitionRegistry()
		 * 1.ConfigurationClassPostProcessor
		 *    {@link org.springframework.context.annotation.ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry(BeanDefinitionRegistry)}
		 */
		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanDefinitionRegistry(registry);
		}
	}

	/**
	 * 调用实现了BeanFactoryPostProcessor接口的类
	 * Invoke the given BeanFactoryPostProcessor beans.
	 */
	private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {

		/**
		 * 循环调用实现了BeanFactoryPostProcessor接口的类
		 * 1.spring内部实现有
		 * {@link org.springframework.context.annotation.ConfigurationClassPostProcessor#postProcessBeanFactory(ConfigurableListableBeanFactory)}
		 */
		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

	/**
	 * Register the given BeanPostProcessor beans.
	 */
	private static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {
		/** 注册BeanPostProcessor */
		for (BeanPostProcessor postProcessor : postProcessors) {
			beanFactory.addBeanPostProcessor(postProcessor);
		}
	}


	/**
	 * BeanPostProcessor that logs an info message when a bean is created during
	 * BeanPostProcessor instantiation, i.e. when a bean is not eligible for
	 * getting processed by all BeanPostProcessors.
	 */
	private static final class BeanPostProcessorChecker implements BeanPostProcessor {

		private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);

		private final ConfigurableListableBeanFactory beanFactory;

		private final int beanPostProcessorTargetCount;

		public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
			this.beanFactory = beanFactory;
			this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) &&
					this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount) {
				if (logger.isInfoEnabled()) {
					logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() +
							"] is not eligible for getting processed by all BeanPostProcessors " +
							"(for example: not eligible for auto-proxying)");
				}
			}
			return bean;
		}

		private boolean isInfrastructureBean(@Nullable String beanName) {
			if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
				BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
				return (bd.getRole() == RootBeanDefinition.ROLE_INFRASTRUCTURE);
			}
			return false;
		}
	}

}
