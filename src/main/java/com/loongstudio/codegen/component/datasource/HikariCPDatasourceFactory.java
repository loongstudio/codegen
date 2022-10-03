package com.loongstudio.codegen.component.datasource;

import com.loongstudio.codegen.constant.CodegenConstant;
import com.loongstudio.codegen.enums.DatasourceEnum;
import com.loongstudio.core.constant.CommonConstant;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.io.Resources;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * HikariCPDatasourceFactory
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022/9/29 22:19
 */
@Slf4j
public class HikariCPDatasourceFactory extends PooledDataSourceFactory {

    public HikariCPDatasourceFactory() {
        String dbDir = StringUtils.joinWith(CommonConstant.BACKSLASH, System.getProperty("user.dir"), CodegenConstant.DB_DIRECTORY);
        try {
            FileUtils.forceMkdir(new File(dbDir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String url = StringUtils.joinWith(CommonConstant.EMPTY, DatasourceEnum.SQLITE.getSchemaName(), StringUtils.joinWith(CommonConstant.BACKSLASH, dbDir, CodegenConstant.DB_NAME));
        log.debug("connection url = {}", url);

        HikariConfig config = new HikariConfig();
        config.setPassword("");
        config.setUsername("");
        config.setDriverClassName(DatasourceEnum.SQLITE.getDriverClassName());
        config.setJdbcUrl(url);
        this.dataSource = new HikariDataSource(config);
    }

    public HikariCPDatasourceFactory(String properties) {
        Properties resourceAsProperties;
        try {
            resourceAsProperties = Resources.getResourceAsProperties(properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HikariConfig config = new HikariConfig(resourceAsProperties);
        this.dataSource = new HikariDataSource(config);
    }

    public HikariCPDatasourceFactory(HikariConfig config) {
        this.dataSource = new HikariDataSource(config);
    }

}
