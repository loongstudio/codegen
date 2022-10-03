package com.loongstudio.codegen.component.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dynamic DataSource Service
 *
 * @author KunLong-Luo
 * @since 2020/11/9 18:08
 */
@Slf4j
public class DynamicDataSourceService {

    public static final String CLOSE_METHOD_NAME = "close";

    private static final Map<String, DataSource> DATA_SOURCE_MAP = new ConcurrentHashMap<>();

    public static synchronized void addDataSource(String ds, DataSource dataSource) {
        DataSource oldDataSource = DATA_SOURCE_MAP.put(ds, dataSource);
        if (oldDataSource != null) {
            closeDataSource(ds, oldDataSource);
        }
        DynamicDataSourceContextHolder.set(ds);
        DynamicRoutingDataSource dynamicRoutingDataSource = new AnnotationConfigApplicationContext().getBean(DynamicRoutingDataSource.class);
        dynamicRoutingDataSource.setTargetDataSources(new HashMap<>(DATA_SOURCE_MAP));
        dynamicRoutingDataSource.afterPropertiesSet();
        log.info("===== add a datasource named [{}] success. =====", ds);
    }

    private static void closeDataSource(String ds, DataSource dataSource) {
        try {
            Method closeMethod = ReflectionUtils.findMethod(dataSource.getClass(), CLOSE_METHOD_NAME);
            if (closeMethod != null) {
                closeMethod.invoke(dataSource);
            }
        } catch (Exception e) {
            log.warn("===== closed datasource named [{}] failed. =====", ds, e);
        }
    }

}
