package com.loongstudio.codegen.controller;

import com.loongstudio.codegen.CodegenApplication;
import com.loongstudio.codegen.api.entity.Datasource;
import com.loongstudio.codegen.api.entity.TemplateExample;
import com.loongstudio.codegen.constant.CodegenConstant;
import com.loongstudio.codegen.entity.OperationEntity;
import com.loongstudio.codegen.enums.DatasourceEnum;
import com.loongstudio.codegen.enums.FXMLPageEnum;
import com.loongstudio.codegen.enums.SqlKeyword;
import com.loongstudio.codegen.model.TreeItemModel;
import com.loongstudio.codegen.util.AlertUtil;
import com.loongstudio.codegen.util.ImageUtil;
import com.loongstudio.codegen.util.ImageViewUtil;
import com.loongstudio.codegen.util.ResourceBundleUtil;
import com.loongstudio.core.constant.CommonConstant;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * BaseController
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022/6/7 7:42
 */

@Slf4j
@Getter
@Setter
public abstract class BaseController implements Initializable {

    private Stage primaryStage;

    private Stage dialogStage;

    protected static final List<TreeItemModel> DATASOURCE_CACHE = new ArrayList<>();

    protected static final List<TreeItemModel> DATABASE_CACHE = new ArrayList<>();

    protected static final List<TreeItemModel> TABLE_CACHE = new ArrayList<>();

    private static final ConcurrentMap<FXMLPageEnum, SoftReference<? extends BaseController>> PAGE_MAP = new ConcurrentHashMap<>();

    /**
     * load page
     *
     * @param title title
     * @param fxml  fxml
     * @param cache have cache
     * @return controller
     */
    public BaseController loadPage(String title, FXMLPageEnum fxml, boolean cache) {
        return loadPage(title, fxml, Boolean.TRUE, cache);
    }

    public BaseController loadPage(String title, FXMLPageEnum fxml, boolean resize, boolean cache) {
        SoftReference<? extends BaseController> parentNodeRef = PAGE_MAP.get(fxml);
        if (cache && Objects.nonNull(parentNodeRef)) {
            return parentNodeRef.get();
        }
        String resource = ResourceBundleUtil.getResource(fxml.getFxml());
        String i18nResource = ResourceBundleUtil.getBasename();
        FXMLLoader loader = new FXMLLoader(CodegenApplication.class.getResource(resource), ResourceBundle.getBundle(i18nResource));

        dialogStage = new Stage();
        try {
            dialogStage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            AlertUtil.error(e.getMessage());
        }
        dialogStage.setTitle(title);
        dialogStage.setMaximized(Boolean.FALSE);
        dialogStage.setResizable(resize);
        dialogStage.getIcons().add(ImageUtil.getImage(CodegenConstant.ICON_LOGO));
        dialogStage.initOwner(getPrimaryStage());
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.show();

        BaseController controller = loader.getController();
        controller.setDialogStage(dialogStage);

        SoftReference<? extends BaseController> softReference = new SoftReference<>(controller);
        PAGE_MAP.put(fxml, softReference);

        return controller;
    }

    public void showDialogStage() {
        if (dialogStage != null) {
            dialogStage.show();
        }
    }

    public void closeDialogStage() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    protected void changeStatus(List<TreeItemModel> databaseCache, Datasource datasource, Boolean expanded) {
        databaseCache.forEach(cache -> {
            if (StringUtils.equals(cache.getName(), datasource.getName())) {
                cache.setExpanded(expanded);
                cache.setImage(getDatasourceImages(datasource, expanded));
            }
        });
    }

    protected void changeStatus(List<TreeItemModel> databaseCache, Datasource datasource, String value, Boolean expanded) {
        databaseCache.forEach(cache -> {
            if (StringUtils.equals(cache.getName(), value)) {
                cache.setExpanded(expanded);
                cache.setImage(getDatasourceImages(datasource, expanded));
            }
        });
    }

    protected void addDatasourceItem(TreeItem<String> root, Datasource datasource, Boolean expanded) {
        String image = getDatasourceImages(datasource, expanded);
        addDatasourceCache(root, datasource, image);
        addItem(root, datasource, datasource.getName(), image, Boolean.FALSE);
    }

    protected void addDatabaseCache(Datasource datasource, TreeItemModel itemModel) {
        removeCache(DATABASE_CACHE, List.of(datasource.getName()));
        DATABASE_CACHE.add(itemModel);
    }

    protected void addTableCache(Datasource datasource, TreeItemModel itemModel) {
        removeCache(TABLE_CACHE, List.of(datasource.getName()));
        TABLE_CACHE.add(itemModel);
    }

    private void addDatasourceCache(TreeItem<String> root, Datasource datasource, String image) {
        TreeItemModel itemModel = TreeItemModel.builder().parentName(root.getValue()).name(datasource.getName()).image(image).expanded(Boolean.FALSE).datasource(datasource).build();
        removeCache(DATASOURCE_CACHE, List.of(datasource.getName()));
        DATASOURCE_CACHE.add(itemModel);
    }

    protected void removeCache(List<TreeItemModel> cacheList, List<String> names) {
        List<TreeItemModel> removed = new ArrayList<>();
        cacheList.forEach(cache -> {
            if (names.contains(cache.getName())) {
                removed.add(cache);
            }
        });

        cacheList.removeAll(removed);
    }

    /**
     * add item
     *
     * @param parentItem parent item
     * @param datasource datasource
     * @param name       name
     * @param image      image
     */
    protected void addItem(TreeItem<String> parentItem, Datasource datasource, String name, String image, Boolean expanded) {
        TreeItem<String> treeItem = initItem(datasource, name, image, expanded);
        parentItem.getChildren().add(treeItem);
    }

    private TreeItem<String> initItem(Datasource datasource, String name, String image, Boolean expanded) {
        ImageView imageView = ImageViewUtil.getImageView(image, 20, 20, datasource);

        TreeItem<String> treeItem = new TreeItem<>();
        treeItem.setValue(name);
        treeItem.setExpanded(expanded);
        treeItem.setGraphic(imageView);
        return treeItem;
    }

    protected String getDatasourceImages(Datasource datasource, Boolean expanded) {
        String image = null;
        switch (DatasourceEnum.match(datasource.getType())) {
            case MYSQL ->
                    image = ImageUtil.getImageUrl(expanded ? CodegenConstant.ICON_MYSQL_ACTIVE : CodegenConstant.ICON_MYSQL);
            case SQLITE ->
                    image = ImageUtil.getImageUrl(expanded ? CodegenConstant.ICON_SQLITE_ACTIVE : CodegenConstant.ICON_SQLITE);
            default -> log.debug("===== default type. =====");
        }
        return image;
    }

    protected String getDatabaseImages(Boolean expanded) {
        return ImageUtil.getImageUrl(expanded ? CodegenConstant.ICON_DATABASE_ACTIVE : CodegenConstant.ICON_DATABASE);
    }

    protected String getTableImages() {
        return ImageUtil.getImageUrl(CodegenConstant.ICON_TABLE);
    }

    protected String getImageUrl(String image) {
        return Objects.requireNonNull(CodegenApplication.class.getResource(StringUtils.joinWith(CommonConstant.SLASH, CodegenConstant.STATIC_DIRECTORY, CodegenConstant.ICON_DIRECTORY, image))).toExternalForm();
    }

    protected void appendQuery(TemplateExample.Criteria queryWrapper, OperationEntity.Query query) {
        String fieldName = query.getFieldName();
        Object fieldValue = query.getFieldValue();
        String operator = query.getOperator();

        if (StringUtils.isEmpty(operator) || StringUtils.isEmpty(fieldName)) {
            return;
        }

        SqlKeyword sqlKeyword = this.match(operator);
        if (Objects.isNull(sqlKeyword)) {
            return;
        }

        if (sqlKeyword == SqlKeyword.LIKE) {
            queryWrapper.andNameLike(CommonConstant.PERCENT + fieldValue + CommonConstant.PERCENT);
        }
    }

    private SqlKeyword match(String operator) {
        for (SqlKeyword value : SqlKeyword.values()) {
            if (StringUtils.equals(value.getSqlSegment(), operator)) {
                return value;
            }
        }
        return null;
    }

}
