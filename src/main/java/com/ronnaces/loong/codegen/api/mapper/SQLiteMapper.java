package com.ronnaces.loong.codegen.api.mapper;

import java.util.List;

/**
 * DatasourceMapper
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022-08-07
 */
public interface SQLiteMapper {

    List<String> showDatabases();

    List<String> showTables();
}

