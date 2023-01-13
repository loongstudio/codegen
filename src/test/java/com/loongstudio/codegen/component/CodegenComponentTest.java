package com.loongstudio.codegen.component;

import com.loongstudio.codegen.component.codegen.CodegenComponent;
import com.loongstudio.codegen.model.CodegenModel;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * CodegenComponentTest
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022-08-09 15:07
 */
class CodegenComponentTest {

    public static final String URL = "jdbc:mysql://localhost:3306/loongstudio?useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai";

    // 数据库用户
    public static final String USERNAME = "root";

    // 数据库密码
    public static final String PASSWORD = "nogx3PIHiZm5pBsf";

    public static final String MODULE_NAME = "business";

    private static final String PARENT = "com.loongstudio.codegen";

    //    private static final String OUTPUT_DIR = "D:\\Program Data\\Gitee\\Galen\\LoongStudio\\java-fx\\loongstudio-codegen\\src\\main\\java";
    private static final String OUTPUT_DIR = "D:\\Program Data\\Temp";

    public static final List<String> SUPER_ENTITY_COLUMNS = List.of("id", "createTime", "createBy", "updateTime", "updateBy", "description", "whetherDelete");

    // 需要生成的表
    public static final List<String> TABLE_NAMES = List.of("template");

    @Test
    void generate() {
        CodegenComponent component = new CodegenComponent();
        component.generate(
                CodegenModel.builder()
                        .url(URL)
                        .username(USERNAME)
                        .password(PASSWORD)
                        .tableList(TABLE_NAMES)
                        .parent(PARENT)
                        .moduleName(MODULE_NAME)
                        .outputDir(OUTPUT_DIR)
                        .haveVue(Boolean.FALSE)
                        .superEntityColumnList(SUPER_ENTITY_COLUMNS)
                        .build()
        );
    }

}