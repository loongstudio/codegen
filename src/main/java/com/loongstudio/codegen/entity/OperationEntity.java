package com.loongstudio.codegen.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * OperationEntity
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022-08-04
 */
@Getter
@Setter
public class OperationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    private Integer pageNo = 1;

    /**
     * 页面大小
     */
    private Integer pageSize = 10;

    /**
     * 字段列表
     */
    private List<String> fieldList;

    /**
     * 查询列表
     */
    private List<Query> queryList;

    /**
     * 排序列表
     */
    private List<Sort> sortList;

    /**
     * Excel
     */
    private Excel excel;

    @Getter
    @Setter
    public static class Sort implements Serializable {

        /**
         * 类型
         */
        private Boolean type = Boolean.FALSE;

        /**
         * 字段列表
         */
        private List<String> fieldList;
    }

    @Getter
    @Setter
    public static class Excel implements Serializable {

        /**
         * 名称
         */
        private String name;

        /**
         * 字段列表
         */
        private List<String> fieldList;
    }

    @Getter
    @Setter
    public static class Query implements Serializable {

        /**
         * 字段名
         */
        private String fieldName;

        /**
         * 字段值
         */
        private String fieldValue;

        /**
         * 运算符
         */
        private String operator;
    }

}
