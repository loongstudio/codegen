package com.ronnaces.loong.codegen.mybatis.config;

import com.baomidou.mybatisplus.core.toolkit.Sequence;
import com.ronnaces.loong.codegen.component.datasource.HikariCPDatasourceFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

/**
 * Mybatis Config
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022-08-04
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.ronnaces.loong.**.mapper")
public class MybatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(new HikariCPDatasourceFactory().getDataSource());
        Properties properties = new Properties();
        properties.setProperty("mapperLocations", "classpath*:com/loongstudio/**/*.xml");
        factoryBean.setConfigurationProperties(properties);
        return factoryBean.getObject();
    }

    @Bean
    public Sequence sequence() {
        return new Sequence(null);
    }

}
