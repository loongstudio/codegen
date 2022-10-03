package com.loongstudio.codegen.component.datasource;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Dynamic DataSource ContextHolder
 *
 * @author KunLong-Luo
 * @since 2020/11/9 18:08
 */
@Data
@Slf4j
public class DynamicDataSourceContextHolder {

    private static final ThreadLocal<String> CURRENT_DATASOURCE = ThreadLocal.withInitial(() -> null);

    public static void set(String name) {
        log.debug("===== switching data sources: {}. =====", name);
        CURRENT_DATASOURCE.set(name);
    }

    public static String get() {
        return CURRENT_DATASOURCE.get();
    }

    public static void remove() {
        log.debug("===== clear data sources: {}. =====", CURRENT_DATASOURCE.get());
        CURRENT_DATASOURCE.remove();
    }
}
