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

import com.ronnaces.loong.codegen.conditions.ISqlSegment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * wrapper
 */
@Getter
@AllArgsConstructor
public enum WrapperKeyword implements ISqlSegment {
    /**
     * 只用作于辨识,不用于其他
     */
    APPLY(null);

    private final String keyword;

    @Override
    public String getSqlSegment() {
        return keyword;
    }

    private static final Map<Integer, WrapperKeyword> map = Arrays.stream(values()).collect(Collectors.toMap(Enum::ordinal, Function.identity()));

    public static Optional<WrapperKeyword> get(Integer ordinal) {
        return Optional.ofNullable(map.get(ordinal));
    }
}
