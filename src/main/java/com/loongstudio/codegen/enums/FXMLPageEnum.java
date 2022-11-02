package com.loongstudio.codegen.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FXML Page
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022/6/7 20:19
 */
@Getter
@AllArgsConstructor
public enum FXMLPageEnum {
    /**
     * Main page
     */
    INDEX("Index.fxml"),
    DATASOURCE("Datasource.fxml"),
    MARIA_DATASOURCE("MariaDatasource.fxml"),

    SQLITE_DATASOURCE("SQLiteDatasource.fxml"),

    POSTGRESQL_DATASOURCE("PostgreSQLDatasource.fxml"),

    ORACLE_DATASOURCE("OracleDatasource.fxml"),
    TEMPLATE("Template.fxml"),
    TEMPLATE_DETAILS("TemplateDetails.fxml"),
    ABOUT("About.fxml"),
    ;

    private final String fxml;
}
