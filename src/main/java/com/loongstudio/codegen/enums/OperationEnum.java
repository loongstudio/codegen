package com.loongstudio.codegen.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * OperationEnum
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022/6/7 20:19
 */
@Getter
@AllArgsConstructor
public enum OperationEnum {
    SAVE,
    DELETE,
    DETAIL,
    EDIT;

    private static final Map<Integer, OperationEnum> map = Arrays.stream(values()).collect(Collectors.toMap(Enum::ordinal, Function.identity()));

    public static Optional<OperationEnum> get(Integer ordinal) {
        return Optional.ofNullable(map.get(ordinal));
    }
}
