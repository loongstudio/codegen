package com.loongstudio.codegen.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DatasourceEnum
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022/6/7 20:19
 */
@Getter
@AllArgsConstructor
public enum DatasourceEnum {
    /**
     * Datasource Enum
     */
    MYSQL("jdbc:mysql:", "com.mysql.cj.jdbc.Driver"),
    SQLITE("jdbc:sqlite:", "org.sqlite.JDBC");

    private final String schemaName;

    private final String driverClassName;

    public static DatasourceEnum match(int code) {
        if (code >= 0 && code < values().length) {
            return values()[code];
        } else {
            throw new IllegalArgumentException("Invalid code: " + code);
        }
    }
}
