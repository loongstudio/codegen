package com.ronnaces.loong.codegen.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

/**
 * ConnectorTest
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022/9/29 19:08
 */
class ConnectorTest {

    public static final String url = "jdbc:sqlite:D:/Program Data/Github/loongstudio/codegen/db/codegen.db";
    public static final String newUrl = "jdbc:sqlite:D:/Program Data/Github/loongstudio/codegen/db/db.db";

    @Test
    void connect() {
        Connection connection = Connector.connect(url);
    }

    @Test
    void createDatabase() {
        Connector.createDatabase(newUrl);
    }
}