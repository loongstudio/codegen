package com.loongstudio.codegen.mapper;

import java.util.List;

/**
 * PostgresqlMapper
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022-08-07
 */
public interface PostgresqlMapper {

    List<String> showDatabases();

    List<String> showTables();

    Integer test();

}

