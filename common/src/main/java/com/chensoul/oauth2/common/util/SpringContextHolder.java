package com.chensoul.oauth2.common.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Lazy(false)
@SuppressWarnings("all")
public class SpringContextHolder implements BeanFactoryPostProcessor, ApplicationContextAware {
	private static ConfigurableListableBeanFactory beanFactory;

	private static ApplicationContext applicationContext;

	private SpringContextHolder() {
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		SpringContextHolder.applicationContext = applicationContext;
	}

	public static ConfigurableListableBeanFactory getConfigurableBeanFactory() {
		ConfigurableListableBeanFactory factory;
		if (null != beanFactory) {
			factory = beanFactory;
		} else {
			if (!(applicationContext instanceof ConfigurableApplicationContext)) {
				throw new RuntimeException("No ConfigurableListableBeanFactory from context!");
			}

			factory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
		}

		return factory;
	}

	public static AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
		return applicationContext.getAutowireCapableBeanFactory();
	}

	public static BeanFactory getParentBeanFactory() {
		return applicationContext.getParentBeanFactory();
	}

	public static boolean containsLocalBean(String name) {
		return applicationContext.containsLocalBean(name);
	}

	public static boolean containsBeanDefinition(String beanName) {
		return applicationContext.containsLocalBean(beanName);
	}

	public static String getProperty(String key) {
		return null == applicationContext ? null : applicationContext.getEnvironment().getProperty(key);
	}

	public static String getApplicationName() {
		return getProperty("spring.application.name");
	}

	public static String[] getActiveProfiles() {
		return null == applicationContext ? null : applicationContext.getEnvironment().getActiveProfiles();
	}

	public static String getActiveProfile() {
		String[] activeProfiles = getActiveProfiles();
		return ArrayUtils.isNotEmpty(activeProfiles) ? activeProfiles[0] : null;
	}

	public static int getBeanDefinitionCount() {
		return applicationContext.getBeanDefinitionCount();
	}

	public static String[] getBeanDefinitionNames() {
		return applicationContext.getBeanDefinitionNames();
	}

	public static String[] getBeanNamesForType(ResolvableType type) {
		return applicationContext.getBeanNamesForType(type);
	}

	public static String[] getBeanNamesForType(Class<?> type) {
		return applicationContext.getBeanNamesForType(type);
	}

	public static String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
		return applicationContext.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
	}

	public static <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
		return applicationContext.getBeansOfType(type);
	}

	public static <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
		throws BeansException {
		return applicationContext.getBeansOfType(type, includeNonSingletons, allowEagerInit);
	}

	public static String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
		return applicationContext.getBeanNamesForAnnotation(annotationType);
	}

	public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType)
		throws BeansException {
		return applicationContext.getBeansWithAnnotation(annotationType);
	}

	public static <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
		throws NoSuchBeanDefinitionException {
		return applicationContext.findAnnotationOnBean(beanName, annotationType);
	}

	public static <T> T getBean(String name) throws BeansException {
		return (T) applicationContext.getBean(name);
	}

	public static <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		return applicationContext.getBean(name, requiredType);
	}

	public static <T> T getBean(String name, Object... args) throws BeansException {
		return (T) applicationContext.getBean(name, args);
	}

	public static <T> T getBean(Class<T> requiredType) throws BeansException {
		return applicationContext.getBean(requiredType);
	}

	public static <T> void getBeanIfExist(Class<T> requiredType, Consumer<T> consumer) throws BeansException {
		try {
			T bean = applicationContext.getBean(requiredType);
			consumer.accept(bean);
		} catch (NoSuchBeanDefinitionException ignore) {
		}
	}

	public static <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
		return applicationContext.getBean(requiredType, args);
	}

	public static <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
		return applicationContext.getBeanProvider(requiredType);
	}

	public static <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
		return applicationContext.getBeanProvider(requiredType);
	}

	public static boolean containsBean(String name) {
		return applicationContext.containsBean(name);
	}

	public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		return applicationContext.isSingleton(name);
	}

	public static boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
		return applicationContext.isPrototype(name);
	}

	public static boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
		return applicationContext.isTypeMatch(name, typeToMatch);
	}

	public static boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
		return applicationContext.isTypeMatch(name, typeToMatch);
	}

	public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		return applicationContext.getType(name);
	}

	public static String[] getAliases(String name) {
		return applicationContext.getAliases(name);
	}

	public static void publishEvent(Object event) {
		applicationContext.publishEvent(event);
	}

	public static void publishEvent(ApplicationEvent event) {
		applicationContext.publishEvent(event);
	}

	public static String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		return applicationContext.getMessage(code, args, defaultMessage, locale);
	}

	public static String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
		return applicationContext.getMessage(code, args, locale);
	}

	public static String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		return applicationContext.getMessage(resolvable, locale);
	}

	public static Resource[] getResources(String locationPattern) throws IOException {
		return applicationContext.getResources(locationPattern);
	}

	public static Resource getResource(String location) {
		return applicationContext.getResource(location);
	}

	public static ClassLoader getClassLoader() {
		return applicationContext.getClassLoader();
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		SpringContextHolder.beanFactory = beanFactory;
	}

}
