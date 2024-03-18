package com.ronnaces.loong.codegen.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * CodegenModel
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
public class CodegenModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;

    private String username;

    private String password;

    private String parent;

    private String moduleName;

    private String outputDir;

    private List<String> superEntityColumnList;

    private List<String> tableList;

    private Boolean haveVue;

    private Boolean haveRequest;
}
