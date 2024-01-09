package com.ronnaces.loong.codegen.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

/**
 * BaseEntity
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022-08-04
 */
@Getter
@Setter
public class SqliteBaseEntity extends ParentEntity {

    protected String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected String createTime;

    protected String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected String updateTime;

    protected Boolean whetherDelete = Boolean.FALSE;

    protected String description;

}
