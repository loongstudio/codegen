package com.loongstudio.codegen.controller;

import com.loongstudio.codegen.api.entity.Datasource;
import javafx.scene.control.TreeItem;

/**
 * DatasourceStrategy
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2023/1/17 8:37
 */
public interface DatasourceStrategy {

    /**
     * Initialize the connection information
     *
     * @param datasourceType datasourceType
     */
    void init(Integer datasourceType);

    /**
     * edition datasource
     *
     * @param datasource datasource
     * @param treeItem   treeItem
     */
    void edit(Datasource datasource, TreeItem<String> treeItem);

    /**
     * test connection
     */
    void test();

    /**
     * confirm connection
     */
    void confirm();

    /**
     * cancel connection
     */
    void cancel();

}
