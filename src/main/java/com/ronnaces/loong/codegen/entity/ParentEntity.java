package com.ronnaces.loong.codegen.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * ParentEntity
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022-08-04
 */
@Getter
@Setter
public class ParentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    protected String id;

}
