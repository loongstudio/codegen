package com.ronnaces.loong.codegen.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * BaseEntity
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022-08-04
 */
@Getter
@Setter
public class BaseEntity extends ParentEntity {

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected LocalDateTime updateTime;

    /**
     * 是否删除: 0-否 1-是
     */
    @TableLogic
    @TableField(value = "whether_delete")
    protected Boolean whetherDelete;

    /**
     * 备注
     */
    @TableField(value = "description")
    protected String description;
}
