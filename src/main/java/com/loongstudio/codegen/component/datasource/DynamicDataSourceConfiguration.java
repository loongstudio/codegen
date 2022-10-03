package com.loongstudio.codegen.component.datasource;

/**
 * Dynamic DataSource Configuration
 *
 * @author KunLong-Luo
 * @since 2020/11/9 18:08
 */
//@Slf4j
//@Configuration
public class DynamicDataSourceConfiguration {

/*    @Bean
    public DynamicRoutingDataSource dynamicMultipleDataSource() {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        dataSource.setDefaultTargetDataSource(defaultTargetDataSource());
        dataSource.setTargetDataSources(targetDataSources);
        return dataSource;
    }

    private HikariDataSource defaultTargetDataSource() {
        String url = StringUtils.joinWith(CommonConstant.EMPTY, DatasourceEnum.SQLITE.getSchemaName(), StringUtils.joinWith(CommonConstant.BACKSLASH, System.getProperty("user.dir"), CodegenConstant.DB_DIRECTORY, CodegenConstant.DB_NAME));
        HikariConfig primaryConfig = new HikariConfig();
        primaryConfig.setJdbcUrl(url);
        primaryConfig.setDriverClassName(DatasourceEnum.SQLITE.getDriverClassName());
        return new HikariDataSource(primaryConfig);
    }*/

}
