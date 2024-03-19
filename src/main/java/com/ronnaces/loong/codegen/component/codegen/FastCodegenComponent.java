package com.ronnaces.loong.codegen.component.codegen;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.ronnaces.loong.codegen.entity.CreateEntity;
import com.ronnaces.loong.codegen.model.CodegenModel;
import com.ronnaces.loong.core.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FastCodegenComponent {

    public static final String TEMPLATE_PACKAGE = "templates";
    public static final String CONTROLLER_CLASS_NAME = "controller.java";
    public static final String SERVICE_IMPL_CLASS_NAME = "serviceImpl.java";
    public static final String SERVICE_CLASS_NAME = "service.java";
    public static final String MAPPER_CLASS_NAME = "mapper.java";
    public static final String MAPPER_XML_NAME = "mapper.xml";
    public static final String ENTITY_CLASS_NAME = "entity.java";
    public static final String REQUEST_NAME = "request.java";
    public static final String RESPONSE_NAME = "response.java";
    public static final String BEAN_PACKAGE_NAME = "bean";
    public static final String REQUEST_PACKAGE_NAME = "bean.request";
    public static final String RESPONSE_PACKAGE_NAME = "bean.response";
    public static final String REQUEST_NAME_FORMAT = "Request";
    public static final String RESPONSE_NAME_FORMAT = "Request";
    public static final String AUTHOR = "KunLong-Luo";
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final String JAVA_DIR = "/src/main/java";
    private static final String TEMPLATE_FILE_SUFFIX = "vm";

    public static void generate(CodegenModel model) {
        FastAutoGenerator.create(model.getUrl(), model.getUsername(), model.getPassword())
                .globalConfig(builder -> globalConfig(model, builder))
                .strategyConfig(builder -> strategyConfig(model, builder))
                .packageConfig(builder -> packageConfig(model, builder))
                .templateConfig(FastCodegenComponent::templateConfig)
                .injectionConfig(builder -> injectionConfig(model, builder))
                .templateEngine(new VelocityTemplateEngine())
                .execute();
    }

    private static void injectionConfig(CodegenModel model, InjectionConfig.Builder builder) {
        if (model.getHaveBean()) {
            builder.beforeOutputFile((tableInfo, stringObjectMap) -> {
                        stringObjectMap.put("requestBeanPackage", StringUtils.joinWith(CommonConstant.PERIOD, model.getParent(), model.getModuleName(), REQUEST_PACKAGE_NAME));
                        stringObjectMap.put("responseBeanPackage", StringUtils.joinWith(CommonConstant.PERIOD, model.getParent(), model.getModuleName(), RESPONSE_PACKAGE_NAME));
                    })
                    .customFile(builderFile -> builderFile.fileName(".java")
                            .packageName(REQUEST_PACKAGE_NAME)
                            .templatePath(StringUtils.joinWith(CommonConstant.SLASH, TEMPLATE_PACKAGE, StringUtils.joinWith(CommonConstant.PERIOD, REQUEST_NAME, TEMPLATE_FILE_SUFFIX)))
                            .formatNameFunction(tableInfo -> tableInfo.getEntityName() + REQUEST_NAME_FORMAT))
                    .customFile(builderFile -> builderFile.fileName(".java")
                            .packageName(RESPONSE_PACKAGE_NAME)
                            .templatePath(StringUtils.joinWith(CommonConstant.SLASH, TEMPLATE_PACKAGE, StringUtils.joinWith(CommonConstant.PERIOD, RESPONSE_NAME, TEMPLATE_FILE_SUFFIX)))
                            .formatNameFunction(tableInfo -> tableInfo.getEntityName() + RESPONSE_NAME_FORMAT))
            ;
        }
    }

    private static void templateConfig(TemplateConfig.Builder builder) {
        builder.controller(StringUtils.joinWith(CommonConstant.SLASH, TEMPLATE_PACKAGE, CONTROLLER_CLASS_NAME))
                .serviceImpl(StringUtils.joinWith(CommonConstant.SLASH, TEMPLATE_PACKAGE, SERVICE_IMPL_CLASS_NAME))
                .service(StringUtils.joinWith(CommonConstant.SLASH, TEMPLATE_PACKAGE, SERVICE_CLASS_NAME))
                .mapper(StringUtils.joinWith(CommonConstant.SLASH, TEMPLATE_PACKAGE, MAPPER_CLASS_NAME))
                .xml(StringUtils.joinWith(CommonConstant.SLASH, TEMPLATE_PACKAGE, MAPPER_XML_NAME))
                .entity(StringUtils.joinWith(CommonConstant.SLASH, TEMPLATE_PACKAGE, ENTITY_CLASS_NAME));
    }

    private static void packageConfig(CodegenModel model, PackageConfig.Builder builder) {
        String parent = model.getParent();
        String moduleName = model.getModuleName();
        if (StringUtils.isNoneBlank(parent)) {
            builder.parent(parent);
        }
        if (StringUtils.isNoneBlank(moduleName)) {
            builder.moduleName(moduleName);
        }
    }

    private static void strategyConfig(CodegenModel model, StrategyConfig.Builder builder) {
        builder.addInclude(model.getTableList())
                .enableSkipView()
                .entityBuilder()
                .superClass(CreateEntity.class)
                .naming(NamingStrategy.underline_to_camel)
                .columnNaming(NamingStrategy.underline_to_camel)
                .enableLombok()
                .enableFileOverride()
                .enableChainModel()
                .enableTableFieldAnnotation()
                .addSuperEntityColumns(model.getSuperEntityColumnList())
                .addIgnoreColumns(model.getSuperEntityColumnList())
                .controllerBuilder()
                .enableRestStyle()
                .enableHyphenStyle();
    }

    private static void globalConfig(CodegenModel model, GlobalConfig.Builder builder) {
        String outputDir = model.getOutputDir();
        if (StringUtils.isEmpty(outputDir)) {
            outputDir = TEMP_DIR + JAVA_DIR;
        } else {
            outputDir += JAVA_DIR;
        }
        builder.outputDir(outputDir)
                .author(AUTHOR)
                .disableOpenDir()
                .enableSpringdoc();
    }
}