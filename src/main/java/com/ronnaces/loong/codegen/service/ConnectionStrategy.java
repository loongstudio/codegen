package com.ronnaces.loong.codegen.service;

import com.ronnaces.loong.codegen.api.entity.Datasource;

/**
 * Connection Strategy
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022/10/27 15:14
 */
public interface ConnectionStrategy {

    void connect();

    void create(Datasource datasource);

}
