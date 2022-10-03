package com.loongstudio.codegen.util;

import com.github.pagehelper.PageInterceptor;
import com.loongstudio.codegen.api.entity.Datasource;
import com.loongstudio.codegen.component.datasource.HikariCPDatasourceFactory;
import com.loongstudio.codegen.constant.CodegenConstant;
import com.loongstudio.codegen.enums.DatasourceEnum;
import com.loongstudio.codegen.mapper.MySQLMapper;
import com.loongstudio.codegen.model.DatasourceModel;
import com.loongstudio.core.constant.CommonConstant;
import com.loongstudio.core.util.IPUtil;
import com.zaxxer.hikari.HikariConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.beans.BeanUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * SqlSessionUtils
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022/9/29 23:02
 */
public final class SqlSessionUtils {

    private static final String PRIMARY_SESSION_FACTORY_NAME = "primary";

    private SqlSessionUtils() {

    }

    private static final Map<String, SqlSessionFactory> SESSION_FACTORY_MAP = new ConcurrentHashMap<>();

    public static void test(Datasource datasource) {
        DatasourceModel config = new DatasourceModel();
        BeanUtils.copyProperties(datasource, config);
        try (SqlSession session = SqlSessionUtils.buildSessionFactory(config).openSession(Boolean.TRUE)) {
            MySQLMapper mapper = session.getMapper(MySQLMapper.class);
            mapper.test();
        }
    }

    public static SqlSessionFactory buildSessionFactory() {
        return cacheFactory(SqlSessionUtils::build);
    }

    public static SqlSessionFactory buildSessionFactory(DatasourceModel config) {
        if (StringUtils.isEmpty(config.getDatabaseName())) {
            return cacheFactory(config, SqlSessionUtils::buildDatasource);
        } else {
            return cacheFactory(config, SqlSessionUtils::buildDatabase);
        }
    }

    private static SqlSessionFactory cacheFactory(Function<String, SqlSessionFactory> function) {
        SqlSessionFactory sessionFactory = SESSION_FACTORY_MAP.get(PRIMARY_SESSION_FACTORY_NAME);
        if (Objects.nonNull(sessionFactory)) {
            return sessionFactory;
        } else {
            return function.apply(PRIMARY_SESSION_FACTORY_NAME);
        }
    }

    private static SqlSessionFactory cacheFactory(DatasourceModel config, Function<DatasourceModel, SqlSessionFactory> function) {
        String key = String.join(CommonConstant.COLON, config.getName(), StringUtils.isEmpty(config.getDatabaseName()) ? "" : config.getDatabaseName());
        SqlSessionFactory sessionFactory = SESSION_FACTORY_MAP.get(key);
        if (Objects.nonNull(sessionFactory)) {
            return sessionFactory;
        } else {
            return function.apply(config);
        }
    }

    private static SqlSessionFactory build(String key) {
        Environment environment = new Environment(key, new JdbcTransactionFactory(), new HikariCPDatasourceFactory().getDataSource());
        Configuration configuration = new Configuration(environment);
        configuration.addMappers("com.loongstudio.codegen.api.mapper");
        page(configuration);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(configuration);
        SESSION_FACTORY_MAP.put(key, factory);
        return factory;
    }

    private static SqlSessionFactory buildDatasource(DatasourceModel config) {
        DatasourceEnum datasourceEnum = DatasourceEnum.match(config.getType());

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setDriverClassName(datasourceEnum.getDriverClassName());
        hikariConfig.setJdbcUrl(buildDatasourceUrl(config, datasourceEnum));

        Environment environment = new Environment(config.getName(), new JdbcTransactionFactory(), new HikariCPDatasourceFactory(hikariConfig).getDataSource());
        Configuration configuration = new Configuration(environment);
        configuration.addMappers("com.loongstudio.codegen.mapper");
        configuration.addMappers("com.loongstudio.codegen.api.mapper");
        page(configuration);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(configuration);
        SESSION_FACTORY_MAP.put(config.getName(), factory);
        return factory;
    }

    private static SqlSessionFactory buildDatabase(DatasourceModel config) {
        DatasourceEnum datasourceEnum = DatasourceEnum.match(config.getType());

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setDriverClassName(datasourceEnum.getDriverClassName());
        hikariConfig.setJdbcUrl(buildDatabaseUrl(config, datasourceEnum));

        String key = String.join(CommonConstant.COLON, config.getName(), config.getDatabaseName());
        Environment environment = new Environment(key, new JdbcTransactionFactory(), new HikariCPDatasourceFactory(hikariConfig).getDataSource());
        Configuration configuration = new Configuration(environment);
        configuration.addMappers("com.loongstudio.codegen.mapper");
        configuration.addMappers("com.loongstudio.codegen.api.mapper");
        page(configuration);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(configuration);
        SESSION_FACTORY_MAP.put(key, factory);
        return factory;
    }

    private static void page(Configuration configuration) {
        PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("param1", "value1");
        interceptor.setProperties(properties);
        configuration.addInterceptor(interceptor);
    }

    private static String buildDatasourceUrl(DatasourceModel config, DatasourceEnum datasourceEnum) {
        switch (datasourceEnum) {
            case SQLITE -> {
                return buildSQLiteUrl(config.getUrl(), datasourceEnum.getSchemaName());
            }
            case MYSQL -> {
                return buildMySQLDatasourceUrl(config.getIp(), config.getPort(), datasourceEnum.getSchemaName());
            }
            default -> {
                return null;
            }
        }
    }

    private static String buildDatabaseUrl(DatasourceModel config, DatasourceEnum datasourceEnum) {
        switch (datasourceEnum) {
            case SQLITE -> {
                return buildSQLiteUrl(config.getUrl(), datasourceEnum.getSchemaName());
            }
            case MYSQL -> {
                return buildMySQLDatabaseUrl(config.getIp(), config.getPort(), datasourceEnum.getSchemaName(), config.getDatabaseName());
            }
            default -> {
                return null;
            }
        }
    }

    private static String buildSQLiteUrl(String url, String schemaName) {
        return StringUtils.joinWith(CommonConstant.EMPTY, schemaName, url);
    }

    private static String buildMySQLDatasourceUrl(Integer ip, Integer port, String schemaName) {
        return new StringJoiner(CommonConstant.QUESTION)
                .add(buildUrl(ip, port, schemaName))
                .add(CodegenConstant.CONNECTION_PARAMETERS)
                .toString();
    }

    public static String buildMySQLDatabaseUrl(Integer ip, Integer port, String schemaName, String databaseName) {
        return new StringJoiner(CommonConstant.SLASH)
                .add(buildUrl(ip, port, schemaName))
                .add(new StringJoiner(CommonConstant.QUESTION).add(databaseName).add(CodegenConstant.CONNECTION_PARAMETERS).toString())
                .toString();
    }

    private static String buildUrl(Integer ip, Integer port, String schemaName) {
        return new StringJoiner(CommonConstant.SLASH + CommonConstant.SLASH)
                .add(schemaName)
                .add(StringUtils.joinWith(CommonConstant.COLON, IPUtil.toString(ip), port))
                .toString();
    }

}
