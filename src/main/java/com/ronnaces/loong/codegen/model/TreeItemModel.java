package com.ronnaces.loong.codegen.model;

import com.ronnaces.loong.codegen.api.entity.Datasource;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * TreeItemModel
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022-08-04
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeItemModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String parentName;

    private String name;

    private String image;

    private Boolean expanded;

    private Datasource datasource;

}
