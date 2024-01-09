package com.ronnaces.loong.codegen.model;

import lombok.*;

import java.io.Serializable;

/**
 * TemplateModel
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022-09-05
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasourceModel implements Serializable {

    private String id;

    private String name;

    private Integer type;

    private String url;

    private Integer ip;

    private Integer port;

    private String username;

    private String password;

    private String databaseName;
}