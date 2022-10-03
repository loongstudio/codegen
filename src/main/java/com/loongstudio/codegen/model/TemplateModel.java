package com.loongstudio.codegen.model;

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
public class TemplateModel implements Serializable {

    private String id;

    private String name;

    private String folder;

    private String parentPackage;

    private String module;

    private String description;

    private String createdAt;

    private String updatedAt;
}