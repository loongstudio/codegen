package com.ronnaces.loong.codegen.database;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;

/**
 * Connector
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022-09-29 17:33
 */
@Slf4j
public class Connector {

    public static Connection connect(String url) {
        try (Connection connection = DriverManager.getConnection(url)) {
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createDatabase(String url) {
        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection != null) {
                DatabaseMetaData meta = connection.getMetaData();
                log.debug("The driver name is {}.", meta.getDriverName());
                log.debug("A new database has been created.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createTable(String url, String tableName) {
        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection != null) {
                Statement statement = connection.createStatement();
//                statement.execute()
                DatabaseMetaData meta = connection.getMetaData();
                log.debug("The driver name is {}.", meta.getDriverName());
                log.debug("A new database has been created.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
