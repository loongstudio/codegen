package com.loongstudio.codegen.api.entity;

import com.loongstudio.codegen.entity.SqliteBaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Template
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022-09-05
 */
@Getter
@Setter
public class Template extends SqliteBaseEntity {

    private String datasourceId;

    private String name;

    private String folder;

    private String parentPackage;

    private String module;

    private String datasourceName;

    private String databaseName;

    private String tableName;

}