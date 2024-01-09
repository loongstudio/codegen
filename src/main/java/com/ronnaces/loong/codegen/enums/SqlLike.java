/*
 * Copyright (c) 2011-2022, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ronnaces.loong.codegen.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * SQL like
 */
@Getter
@AllArgsConstructor
public enum SqlLike {
    /**
     * %值
     */
    LEFT,
    /**
     * 值%
     */
    RIGHT,
    /**
     * %值%
     */
    DEFAULT;

    private static final Map<Integer, SqlLike> map = Arrays.stream(values()).collect(Collectors.toMap(Enum::ordinal, Function.identity()));

    public static Optional<SqlLike> get(Integer ordinal) {
        return Optional.ofNullable(map.get(ordinal));
    }

}
