package com.ronnaces.loong.codegen.util;

import com.ronnaces.loong.codegen.api.entity.Datasource;
import com.ronnaces.loong.codegen.api.mapper.DatasourceMapper;
import com.ronnaces.loong.codegen.api.mapper.SQLiteMapper;
import com.ronnaces.loong.codegen.api.mapper.TemplateMapper;
import com.ronnaces.loong.codegen.enums.DatasourceEnum;
import com.ronnaces.loong.codegen.mapper.MySQLMapper;
import com.ronnaces.loong.codegen.model.DatasourceModel;
import com.ronnaces.loong.core.network.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * SqlSessionUtilsTest
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022/9/30 7:18
 */
@Slf4j
class SqlSessionUtilsTest {

    @Test
    void buildSessionFactory() {
        try (SqlSession session = SqlSessionUtils.buildSessionFactory().openSession(Boolean.TRUE)) {
            Assertions.assertTrue(CollectionUtils.isNotEmpty(session.getMapper(SQLiteMapper.class).showDatabases()));
            Assertions.assertTrue(CollectionUtils.isNotEmpty(session.getMapper(TemplateMapper.class).selectList()));
        }
    }

/*    @ParameterizedTest()
    @MethodSource("namedArguments")
    void buildSessionFactory(HikariConfig config) {
        try (SqlSession session = SqlSessionUtils.buildSessionFactory(config).openSession(Boolean.TRUE)) {
            TemplateMapper mapper = session.getMapper(TemplateMapper.class);
            SQLiteMapper globalMapper = session.getMapper(SQLiteMapper.class);
            List<String> stringList = globalMapper.showDatabases();
            Template template = mapper.selectByName("demo");
            log.debug("[ template ] is {}", template.getName());
        }
    }

    static Stream<Arguments> namedArguments() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:D:/Program Data/Github/loongstudio/codegen/db/codegen.db");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setPassword("");
        config.setUsername("");
        return Stream.of(
                arguments(named("An Hikari Config", config))
        );
    }*/


    @Test
    void buildDatasourceSessionFactory() {
        DatasourceModel sqliteConfig = new DatasourceModel();
        sqliteConfig.setName("demos");
        sqliteConfig.setType(DatasourceEnum.SQLITE.ordinal());
        sqliteConfig.setUrl("D:/Program Data/Github/loongstudio/codegen/db/codegen.db");
        sqliteConfig.setUsername("");
        sqliteConfig.setPassword("");
        try (SqlSession session = SqlSessionUtils.buildSessionFactory(sqliteConfig).openSession(Boolean.TRUE)) {
            DatasourceMapper mapper = session.getMapper(DatasourceMapper.class);
            List<Datasource> templates = mapper.selectList();
            Assertions.assertTrue(CollectionUtils.isNotEmpty(templates));
        }

        DatasourceModel mysqlConfig = new DatasourceModel();
        mysqlConfig.setType(DatasourceEnum.MYSQL.ordinal());
        mysqlConfig.setIp(IPUtil.toInteger("127.0.0.1"));
        mysqlConfig.setPort(3306);
        mysqlConfig.setUsername("root");
        mysqlConfig.setPassword("nogx3PIHiZm5pBsf");
        mysqlConfig.setName("demos1");
        try (SqlSession session = SqlSessionUtils.buildSessionFactory(mysqlConfig).openSession(Boolean.TRUE)) {
            MySQLMapper mapper = session.getMapper(MySQLMapper.class);
            List<String> databases = mapper.showDatabases();
            Assertions.assertTrue(CollectionUtils.isNotEmpty(databases));
        }
    }

    @Test
    void buildDatabaseSessionFactory() {
        DatasourceModel mysqlConfig = new DatasourceModel();
        mysqlConfig.setType(DatasourceEnum.MYSQL.ordinal());
        mysqlConfig.setIp(IPUtil.toInteger("127.0.0.1"));
        mysqlConfig.setPort(3306);
        mysqlConfig.setUsername("root");
        mysqlConfig.setPassword("nogx3PIHiZm5pBsf");
        mysqlConfig.setName("demoss");
        mysqlConfig.setDatabaseName("loongstudio");
        try (SqlSession session = SqlSessionUtils.buildSessionFactory(mysqlConfig).openSession(Boolean.TRUE)) {
            MySQLMapper mapper = session.getMapper(MySQLMapper.class);
            List<String> tables = mapper.showTables();
            Assertions.assertTrue(CollectionUtils.isNotEmpty(tables));
        }
    }

}