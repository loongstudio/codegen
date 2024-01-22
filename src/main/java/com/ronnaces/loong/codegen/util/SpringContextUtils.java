package com.ronnaces.loong.codegen.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * SpringContextUtils
 *
 * @author Galen Luo
 * @since 2020/11/9 18:08
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private static ApplicationContext parentApplicationContext;

    public SpringContextUtils() {

    }

    /**
     * 获取applicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Assert.notNull(applicationContext, "SpringContextUtils injection ApplicationContext is null");
        SpringContextUtils.applicationContext = applicationContext;
        SpringContextUtils.parentApplicationContext = applicationContext.getParent();
    }

    public static ApplicationContext getParentApplicationContext() {
        return parentApplicationContext;
    }

    public static Object getBean(String name) {
        Assert.hasText(name, "SpringContextUtils name is null or empty");

        try {
            return SpringContextUtils.applicationContext.getBean(name);
        } catch (Exception var2) {
            return parentApplicationContext.getBean(name);
        }
    }

    public static <T> T getBean(Class<T> type) {
        Assert.notNull(type, "SpringContextUtils type is null");

        try {
            return applicationContext.getBean(type);
        } catch (Exception var2) {
            return parentApplicationContext.getBean(type);
        }
    }

    public static <T> T getBean(String name, Class<T> type) {
        Assert.notNull(type, "SpringContextUtils name is null");
        Assert.notNull(type, "SpringContextUtils type is null");

        try {
            return applicationContext.getBean(name, type);
        } catch (Exception var2) {
            return parentApplicationContext.getBean(name, type);
        }
    }

    public static <T> Map<String, T> getBeans(Class<T> type) {
        Assert.notNull(type, "SpringContextUtils type is null");

        try {
            return applicationContext.getBeansOfType(type);
        } catch (Exception var2) {
            return parentApplicationContext.getBeansOfType(type);
        }
    }

    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        Assert.notNull(annotationType, "SpringContextUtils annotationType is null");

        try {
            return applicationContext.getBeansWithAnnotation(annotationType);
        } catch (Exception var2) {
            return parentApplicationContext.getBeansWithAnnotation(annotationType);
        }
    }

}
