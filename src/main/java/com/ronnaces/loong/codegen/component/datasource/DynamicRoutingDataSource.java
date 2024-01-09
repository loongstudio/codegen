package com.ronnaces.loong.codegen.component.datasource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Dynamic Routing DataSource
 *
 * @author KunLong-Luo
 * @since 2020/11/9 18:08
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    public static final String DEFAULT_DATASOURCE = "primary";

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSource = DynamicDataSourceContextHolder.get();
        if (StringUtils.isEmpty(dataSource)) {
            return DEFAULT_DATASOURCE;
        }
        return dataSource;
    }
}
