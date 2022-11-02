package com.loongstudio.codegen.util;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

public final class CheckUtil {

    private CheckUtil() {
    }

    public static void checkStringParam(List<String> paramList) {
        paramList.forEach(param -> {
            if (StringUtils.isEmpty(param)) {
                throw new IllegalArgumentException();
            }
        });
    }

    public static void checkIntegerParam(List<Integer> paramList) {
        paramList.forEach(param -> {
            if (Objects.isNull(param)) {
                throw new IllegalArgumentException();
            }
        });
    }

}
