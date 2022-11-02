package com.loongstudio.codegen.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    SQLITE("jdbc:sqlite:", "org.sqlite.JDBC"),
    MARIA_DB("jdbc:mariadb:", "org.mariadb.jdbc.Driver"),
    POSTGRESQL("jdbc:postgresql:", "org.postgresql.Driver"),
    ORACLE("jdbc:oracle:thin:", "oracle.jdbc.OracleDriver");

    private final String schemaName;

    private final String driverClassName;

    private static final Map<Integer, DatasourceEnum> map = Arrays.stream(values()).collect(Collectors.toMap(Enum::ordinal, Function.identity()));

    public static Optional<DatasourceEnum> get(Integer ordinal) {
        return Optional.ofNullable(map.get(ordinal));
    }

    public static DatasourceEnum match(int code) {
        if (code >= 0 && code < values().length) {
            return values()[code];
        } else {
            throw new IllegalArgumentException("Invalid code: " + code);
        }
    }
}
