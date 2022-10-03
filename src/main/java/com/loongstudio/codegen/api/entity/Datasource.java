package com.loongstudio.codegen.api.entity;

import com.loongstudio.codegen.entity.SqliteBaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Datasource
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022-08-07
 */
@Getter
@Setter
public class Datasource extends SqliteBaseEntity {

    private String name;

    private Integer type;

    private String url;

    private Integer ip;

    private Integer port;

    private String username;

    private String password;

}