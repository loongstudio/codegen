package com.ronnaces.loong.codegen.component.codegen;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.ronnaces.loong.codegen.entity.CreateEntity;
import com.ronnaces.loong.codegen.model.CodegenModel;
import com.ronnaces.loong.core.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Codegen Component
 *
 * @author KunLong-Luo
 * @since 2022/09/25 18:08
 */
@Slf4j
@Component
public class CodegenComponent {

    public static final String VUE_MODULE_NAME = "vue";
    public static final String TEMPLATE_PACKAGE = "templates";
    public static final String CONTROLLER_CLASS_NAME = "controller.java";
    public static final String SERVICE_IMPL_CLASS_NAME = "serviceImpl.java";
    public static final String SERVICE_CLASS_NAME = "service.java";
    public static final String MAPPER_CLASS_NAME = "mapper.java";
    public static final String MAPPER_XML_NAME = "mapper.xml";
    public static final String ENTITY_CLASS_NAME = "entity.java";
    public static final String VUE_MODAL = "modal.vue";
    public static final String VUE_LIST = "list.vue";
    public static final String AUTHOR = "KunLong-Luo";
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final String JAVA_DIR = "/src/main/java";
    private static final String TEMPLATE_FILE_SUFFIX = "vm";

    /**
     * generate
     *
     * @param model model
     */
    public void generate(CodegenModel model) {
        new AutoGenerator(this.dataSourceConfig(model.getUrl(), model.getUsername(), model.getPassword()))
                .global(this.globalConfig(model.getOutputDir()))
                .strategy(this.strategyConfig(model.getTableList(), model.getSuperEntityColumnList()))
                .packageInfo(this.packageConfig(model.getParent(), model.getModuleName()))
                .template(this.templateConfig())
                .injection(this.injectionConfig(model.getHaveVue()))
                .execute(new VelocityTemplateEngine());
    }

    /**
     * configuring Data Sources
     *
     * @param url      url
     * @param username username
     * @param password password
     * @return {@link DataSourceConfig}
     */
    private DataSourceConfig dataSourceConfig(String url, String username, String password) {
        return new DataSourceConfig.Builder(url, username, password).build();
    }

    /**
     * injection config
     *
     * @param haveVue have vue
     * @return {@link InjectionConfig}
     */
    private InjectionConfig injectionConfig(Boolean haveVue) {
        if (haveVue) {
            Map<String, String> customFile = new HashMap<>();
            customFile.put(VUE_MODAL, StringUtils.joinWith(CommonConstant.SLASH, TEMPLATE_PACKAGE, StringUtils.joinWith(CommonConstant.PERIOD, VUE_MODAL, TEMPLATE_FILE_SUFFIX)));
            customFile.put(VUE_LIST, StringUtils.joinWith(CommonConstant.SLASH, TEMPLATE_PACKAGE, StringUtils.joinWith(CommonConstant.PERIOD, VUE_LIST, TEMPLATE_FILE_SUFFIX)));
            return new InjectionConfig.Builder()
                    .beforeOutputFile((tableInfo, objectMap) -> {
                    })
                    .customFile(customFile)
                    .build();
        }

        return new InjectionConfig.Builder().build();
    }

    /**
     * template config
     *
     * @return {@link TemplateConfig}
     */
    private TemplateConfig templateConfig() {
        return new TemplateConfig.Builder()
                .controller(StringUtils.joinWith(CommonConstant.SLASH, TEMPLATE_PACKAGE, CONTROLLER_CLASS_NAME))
                .serviceImpl(StringUtils.joinWith(CommonConstant.SLASH, TEMPLATE_PACKAGE, SERVICE_IMPL_CLASS_NAME))
                .service(StringUtils.joinWith(CommonConstant.SLASH, TEMPLATE_PACKAGE, SERVICE_CLASS_NAME))
                .mapper(StringUtils.joinWith(CommonConstant.SLASH, TEMPLATE_PACKAGE, MAPPER_CLASS_NAME))
                .xml(StringUtils.joinWith(CommonConstant.SLASH, TEMPLATE_PACKAGE, MAPPER_XML_NAME))
                .entity(StringUtils.joinWith(CommonConstant.SLASH, TEMPLATE_PACKAGE, ENTITY_CLASS_NAME))
                .build();
    }

    /**
     * strategy config
     *
     * @param tableList             tableList
     * @param superEntityColumnList superEntityColumnList
     * @return {@link StrategyConfig}
     */
    private StrategyConfig strategyConfig(List<String> tableList, List<String> superEntityColumnList) {
        return new StrategyConfig.Builder()
                .addInclude(tableList)
                .enableSkipView()
                .entityBuilder()
                .superClass(CreateEntity.class)
                .naming(NamingStrategy.underline_to_camel)
                .columnNaming(NamingStrategy.underline_to_camel)
                .enableLombok()
                .enableFileOverride()
                .enableChainModel()
                .enableTableFieldAnnotation()
                .addSuperEntityColumns(superEntityColumnList)
                .addIgnoreColumns(superEntityColumnList)
                .controllerBuilder()
                .enableRestStyle()
                .enableHyphenStyle()
                .build();
    }

    /**
     * package config
     *
     * @param parent     parent package
     * @param moduleName module name
     * @return {@link PackageConfig}
     */
    private PackageConfig packageConfig(String parent, String moduleName) {
        PackageConfig.Builder builder = new PackageConfig
                .Builder();
        if (StringUtils.isNoneBlank(parent)) {
            builder.parent(parent);
        }
        if (StringUtils.isNoneBlank(moduleName)) {
            builder.moduleName(moduleName);
        }
        return builder.build();
    }

    /**
     * global config
     *
     * @param outputDir output directory
     * @return {@link GlobalConfig}
     */
    private GlobalConfig globalConfig(String outputDir) {
        if (StringUtils.isEmpty(outputDir)) {
            outputDir = TEMP_DIR + JAVA_DIR;
        } else {
            outputDir += JAVA_DIR;
        }
        return new GlobalConfig.Builder()
                .outputDir(outputDir)
                .author(AUTHOR)
                .disableOpenDir()
                .enableSpringdoc()
                .build();
    }
}