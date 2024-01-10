package com.ronnaces.loong.codegen.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

/**
 * CreateEntity
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2023/4/2 8:21
 */
@Getter
@Setter
public class CreateEntity extends BaseEntity {

    /**
     * 创建人
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    protected String createBy;

    /**
     * 更新人
     */
    @TableField(value = "update_by", fill = FieldFill.UPDATE)
    protected String updateBy;

}
