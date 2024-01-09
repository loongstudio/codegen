package com.ronnaces.loong.codegen.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    DATASOURCE,
    DATABASE,
    TABLE;

    private static final Map<Integer, ItemTypeEnum> map = Arrays.stream(values()).collect(Collectors.toMap(Enum::ordinal, Function.identity()));

    public static Optional<ItemTypeEnum> get(Integer ordinal) {
        return Optional.ofNullable(map.get(ordinal));
    }
}
