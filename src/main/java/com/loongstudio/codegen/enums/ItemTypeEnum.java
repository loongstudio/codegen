package com.loongstudio.codegen.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ItemTypeEnum
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022/6/7 20:19
 */
@Getter
@AllArgsConstructor
public enum ItemTypeEnum {
    /**
     * type: datasource, database, table
     */
    DATASOURCE(),
    DATABASE(),
    TABLE();

    public static ItemTypeEnum match(int code) {
        if (code >= 0 && code < values().length) {
            return values()[code];
        } else {
            throw new IllegalArgumentException("Invalid code: " + code);
        }
    }
}
