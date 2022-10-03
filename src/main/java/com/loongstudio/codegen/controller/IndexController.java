package com.loongstudio.codegen.controller;

import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.loongstudio.codegen.App;
import com.loongstudio.codegen.api.entity.Datasource;
import com.loongstudio.codegen.api.entity.Template;
import com.loongstudio.codegen.api.mapper.DatasourceMapper;
import com.loongstudio.codegen.api.mapper.SQLiteMapper;
import com.loongstudio.codegen.api.mapper.TemplateMapper;
import com.loongstudio.codegen.constant.CodegenConstant;
import com.loongstudio.codegen.enums.DatasourceEnum;
import com.loongstudio.codegen.enums.FXMLPageEnum;
import com.loongstudio.codegen.enums.ItemTypeEnum;
import com.loongstudio.codegen.mapper.MySQLMapper;
import com.loongstudio.codegen.model.CodegenModel;
import com.loongstudio.codegen.model.DatasourceModel;
import com.loongstudio.codegen.model.TreeItemModel;
import com.loongstudio.codegen.util.AlertUtil;
import com.loongstudio.codegen.util.ImageViewUtil;
import com.loongstudio.codegen.util.SqlSessionUtils;
import com.loongstudio.core.constant.CommonConstant;
import com.loongstudio.core.toolkit.Sequence;
import com.loongstudio.core.util.StringUtil;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Index Controller
 *
 * @author KunLong-Luo
 * @since 2022/09/25 18:08
 */
@Slf4j
@Getter
public class IndexController extends BaseController {

    public static final List<String> SUPER_ENTITY_COLUMNS = List.of("id", "createdAt", "createdBy", "updatedAt", "updatedBy", "description", "haveDeleted");

    private static final StringBuilder SEARCH_CACHE = new StringBuilder();

    public MenuItem aboutMenuItem;

    private Template template = new Template();

    public MenuItem openMenuItem;

    public Menu openRecentMenu;

    private Datasource datasource;

    public BorderPane rootBorderPane;

    public TreeView<String> databasesTreeView;

    public TextField searchTextField;

    public ImageView searchImageView;

    public ImageView clearImageView;

    public ImageView fixedImageView;

    public VBox treeViewVBox;

    public HBox searchViewVBox;

    public TextField folderTextField;

    public Button choiceButton;

    public TextField parentPackageNameTextField;

    public TextField moduleNameTextField;

    public TextField tableNameTextField;

    public TextField entityNameTextField;

    public TextField mapperNameTextField;

    public TextField serviceNameTextField;

    public TextField controllerNameTextField;

    public Button parentPackageNameButton;

    public Button moduleNameButton;

    public Button tableNameButton;

    public Button entityNameButton;

    public Button mapperNameButton;

    public Button serviceNameButton;

    public Button controllerNameButton;

    public Button saveButton;

    public Button resetButton;

    public Button submitButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initTreeView();
    }

    /**
     * 初始化树视图
     */
    private void initTreeView() {
        log.debug("===== init left tree view. =====");
        loadTables();
        loadDatasource();
        loadSearchWindow();
        loadTemplate();
    }

    private void loadTables() {
        TemplateMapper templateMapper = App.applicationContext.getBean(TemplateMapper.class);
        DatasourceMapper datasourceMapper = App.applicationContext.getBean(DatasourceMapper.class);
        templateMapper.createTable();
        datasourceMapper.createTable();
    }

    private void loadRecentTemplateMenu() {
        this.openRecentMenu.getItems().clear();
        List<MenuItem> menuItemList = new ArrayList<>();

        try (SqlSession session = SqlSessionUtils.buildSessionFactory().openSession(Boolean.TRUE)) {
            TemplateMapper templateMapper = session.getMapper(TemplateMapper.class);
            DatasourceMapper datasourceMapper = session.getMapper(DatasourceMapper.class);

            List<Template> templates = templateMapper.selectRecently(10);
            templates.forEach(template -> {
                MenuItem menuItem = new MenuItem(template.getName());
                menuItem.setId(template.getId());
                menuItem.setOnAction(actionEvent -> loadCurrentTemplate(templateMapper, datasourceMapper, template.getId()));
                menuItemList.add(menuItem);
            });
        }
        this.openRecentMenu.getItems().setAll(menuItemList);
    }

    protected void loadCurrentTemplate(TemplateMapper templateMapper, DatasourceMapper datasourceMapper, String id) {
        log.debug("current template id = {}", id);
        this.template = templateMapper.selectById(id);
        this.datasource = datasourceMapper.selectById(this.getTemplate().getDatasourceId());

        this.folderTextField.setText(this.template.getFolder());
        this.parentPackageNameTextField.setText(this.template.getParentPackage());
        this.moduleNameTextField.setText(this.template.getModule());
        this.tableNameTextField.setText(this.template.getTableName());
        String entityName = StringUtil.firstToUpperCase(StringUtil.underlineToCamel(this.template.getTableName()));
        this.entityNameTextField.setText(entityName);
        this.mapperNameTextField.setText(StringUtils.joinWith(CommonConstant.EMPTY, entityName, ConstVal.MAPPER));
        this.serviceNameTextField.setText(StringUtils.joinWith(CommonConstant.EMPTY, entityName, ConstVal.SERVICE));
        this.controllerNameTextField.setText(StringUtils.joinWith(CommonConstant.EMPTY, entityName, ConstVal.CONTROLLER));

        AlertUtil.info("load current template success");
    }

    private void loadTemplate() {
        parentPackageNameTextField.textProperty().addListener((observableValue, v1, v2) -> {
            String value = observableValue.getValue();
            this.template.setParentPackage(value);
            log.debug("========== parentPackageName: {}, {}, {} ==========", value, v1, v2);
        });

        moduleNameTextField.textProperty().addListener((observableValue, v1, v2) -> {
            String value = observableValue.getValue();
            moduleNameTextField.setText(value);
            this.template.setModule(value);
            log.debug("========== moduleName: {}, {}, {} ==========", value, v1, v2);
        });
    }

    /**
     * 加载数据源
     */
    private void loadDatasource() {
        log.debug("===== load all datasource. =====");
        databasesTreeView.setShowRoot(Boolean.TRUE);
        ImageView imageView = ImageViewUtil.getImageView(getImageUrl(CodegenConstant.ICON_CONNECTION), 20, 20);
        TreeItem<String> root = new TreeItem<>("connection", imageView);

        databasesTreeView.setRoot(root);
        databasesTreeView.setCellFactory((TreeView<String> tv) -> handleCallback(TextFieldTreeCell.forTreeView(), tv));

        try (SqlSession session = SqlSessionUtils.buildSessionFactory().openSession(Boolean.TRUE)) {
            DatasourceMapper mapper = session.getMapper(DatasourceMapper.class);
            List<Datasource> datasourceList = mapper.selectList();
            if (CollectionUtils.isEmpty(datasourceList)) {
                return;
            }
            datasourceList.forEach(datasource -> addDatasourceItem(root, datasource, Boolean.FALSE));
        }
    }

    /**
     * 加载搜索窗
     */
    private void loadSearchWindow() {
        log.debug("===== init search window. =====");
        fixedImageView.setUserData(Boolean.FALSE);
        fixedImageView.setImage(new Image(getImageUrl(CodegenConstant.ICON_FIXED)));
        searchImageView.setImage(new Image(getImageUrl(CodegenConstant.ICON_SEARCH_ACTIVE)));
        clearImageView.setImage(new Image(getImageUrl(CodegenConstant.ICON_CLEAR)));

        searchTextField.textProperty().addListener((observableValue, v1, v2) -> {
            TreeItem<String> root = databasesTreeView.getRoot();
            ObservableList<TreeItem<String>> datasourceList = root.getChildren();
            String value = observableValue.getValue();
            if (StringUtils.isEmpty(value)) {
                datasourceList.clear();

                DATASOURCE_CACHE.forEach(datasourceCache -> {
                    Datasource datasource = datasourceCache.getDatasource();
                    String image = getDatasourceImages(datasource, datasourceCache.getExpanded());
                    addItem(root, datasource, datasource.getName(), image, datasourceCache.getExpanded());
                });

                datasourceList.forEach(datasource -> DATABASE_CACHE.forEach(databaseCache -> {
                    if (StringUtils.equals(datasource.getValue(), databaseCache.getParentName())) {
                        String image = getDatabaseImages(databaseCache.getExpanded());
                        addItem(datasource, databaseCache.getDatasource(), databaseCache.getName(), image, databaseCache.getExpanded());
                    }
                }));

                datasourceList.forEach(datasource -> datasource.getChildren().forEach(database -> TABLE_CACHE.forEach(tableCache -> {
                    if (StringUtils.equals(database.getValue(), tableCache.getParentName())) {
                        String image = getTableImages();
                        addItem(database, tableCache.getDatasource(), tableCache.getName(), image, tableCache.getExpanded());
                    }
                })));
                databasesTreeView.refresh();
                return;
            }
            List<TreeItem<String>> removeDatasourceList = new ArrayList<>();
            List<String> datasourceNameList = new ArrayList<>();
            datasourceList.forEach(datasource -> {
                ObservableList<TreeItem<String>> databaseList = datasource.getChildren();
                List<TreeItem<String>> removeDatabaseList = new ArrayList<>();
                List<String> databaseNameList = new ArrayList<>();
                databaseList.forEach(database -> {
                    ObservableList<TreeItem<String>> tableList = database.getChildren();
                    List<String> tableNameList = new ArrayList<>();
                    List<TreeItem<String>> removeTableList = new ArrayList<>();
                    tableList.forEach(table -> shunt(value, removeTableList, tableNameList, table));
                    if (CollectionUtils.isEmpty(tableNameList)) {
                        shunt(value, removeDatabaseList, databaseNameList, database);
                    } else {
                        databaseNameList.add(database.getValue());
                    }
                    tableList.removeAll(removeTableList);
                });
                if (CollectionUtils.isEmpty(databaseNameList)) {
                    shunt(value, removeDatasourceList, datasourceNameList, datasource);
                } else {
                    datasourceNameList.add(datasource.getValue());
                }
                databaseList.removeAll(removeDatabaseList);
            });
            datasourceList.removeAll(removeDatasourceList);
            databasesTreeView.refresh();
        });
    }

    private void shunt(String value, List<TreeItem<String>> removeList, List<String> reserveList, TreeItem<String> treeItem) {
        if (!StringUtils.containsIgnoreCase(treeItem.getValue(), value)) {
            removeList.add(treeItem);
        } else {
            reserveList.add(treeItem.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    private TreeCell<String> handleCallback(Callback<TreeView<String>, TreeCell<String>> defaultCellFactory, TreeView<String> tv) {
        TreeCell<String> cell = defaultCellFactory.call(tv);
        cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            int level = databasesTreeView.getTreeItemLevel(cell.getTreeItem());
            TreeCell<String> treeCell = (TreeCell<String>) event.getSource();
            TreeItem<String> treeItem = treeCell.getTreeItem();

            handleMenu(cell, level);
            handleEvent(treeItem, event, level);
        });
        return cell;
    }

    /**
     * 处理菜单
     *
     * @param cell  单元格
     * @param level 级别
     */
    private void handleMenu(TreeCell<String> cell, int level) {
        TreeItem<String> treeItem = cell.getTreeItem();
        if (treeItem == null) {
            return;
        }

        Node graphic = treeItem.getGraphic();
        if (Objects.isNull(graphic)) {
            return;
        }
        Datasource datasource = (Datasource) graphic.getUserData();
        if (Objects.isNull(datasource)) {
            return;
        }
        if (level == 1) {
            handleDatasource(cell, treeItem, datasource);
        } else if (level == 2) {
            handleDatabase(cell, treeItem, datasource);
        }
    }

    /**
     * 处理事件
     *
     * @param treeItem 树项
     * @param event    事件
     * @param level    级别
     */
    private void handleEvent(TreeItem<String> treeItem, MouseEvent event, int level) {
        if (treeItem == null) {
            return;
        }

        if (event.getClickCount() != 2) {
            return;
        }

        Datasource datasource = (Datasource) treeItem.getGraphic().getUserData();
        String value = treeItem.getValue();
        if (treeItem.isExpanded()) {
            treeItem.setExpanded(Boolean.FALSE);
            if (level == 1) {
                changeStatus(DATASOURCE_CACHE, datasource, value, Boolean.FALSE);
                removeCache(DATASOURCE_CACHE, List.of(datasource.getName()));
            } else if (level == 2) {
                changeStatus(DATABASE_CACHE, datasource, value, Boolean.FALSE);
                removeCache(DATABASE_CACHE, List.of(datasource.getName()));
            } else if (level == 3) {
                if (StringUtils.isEmpty(value)) {
                    return;
                }
                this.datasource = datasource;
                loadTemplateConfig(treeItem.getParent().getParent().getValue(), treeItem.getParent().getValue(), value);
            }
        } else {
            treeItem.setExpanded(Boolean.TRUE);
            try {
                if (level == 1) {
                    initDatabaseView(treeItem, datasource);
                    changeStatus(DATASOURCE_CACHE, datasource, value, Boolean.TRUE);
                } else if (level == 2) {
                    initTableView(treeItem, datasource);
                    changeStatus(DATABASE_CACHE, datasource, value, Boolean.TRUE);
                } else if (level == 3) {
                    if (StringUtils.isEmpty(value)) {
                        return;
                    }
                    this.datasource = datasource;
                    loadTemplateConfig(treeItem.getParent().getParent().getValue(), treeItem.getParent().getValue(), value);
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                AlertUtil.error("Connection Failure.");
            }
        }
        databasesTreeView.refresh();
    }

    private void loadTemplateConfig(String datasource, String database, String table) {
        this.template.setName(new StringJoiner(CommonConstant.PERIOD).add(datasource).add(database).add(table).toString());
        this.template.setDatasourceName(datasource);
        this.template.setDatabaseName(database);
        this.template.setTableName(table);
        tableNameTextField.setText(table);
        String entityName = StringUtil.firstToUpperCase(StringUtil.underlineToCamel(table));
        entityNameTextField.setText(entityName);
        mapperNameTextField.setText(StringUtils.joinWith(CommonConstant.EMPTY, entityName, ConstVal.MAPPER));
        serviceNameTextField.setText(StringUtils.joinWith(CommonConstant.EMPTY, entityName, ConstVal.SERVICE));
        controllerNameTextField.setText(StringUtils.joinWith(CommonConstant.EMPTY, entityName, ConstVal.CONTROLLER));
    }

    /**
     * 处理连接
     *
     * @param cell       单元格
     * @param treeItem   树项
     * @param datasource 数据源
     */
    private void handleDatasource(TreeCell<String> cell, TreeItem<String> treeItem, Datasource datasource) {
        final ContextMenu contextMenu = new ContextMenu();
        ImageView mysqlImages = (ImageView) treeItem.getGraphic();
        DatasourceEnum datasourceEnum = DatasourceEnum.match(datasource.getType());

        MenuItem openItem = new MenuItem("open connection");
        openItem.setOnAction(event1 -> {
            log.debug("===== open connection. =====");
            try {
                initDatabaseView(treeItem, datasource);
            } catch (RuntimeException e) {
                e.printStackTrace();
                AlertUtil.error("Connection Failure.");
                return;
            }
            switch (datasourceEnum) {
                case MYSQL -> mysqlImages.setImage(new Image(getImageUrl(CodegenConstant.ICON_MYSQL_ACTIVE)));
                case SQLITE -> mysqlImages.setImage(new Image(getImageUrl(CodegenConstant.ICON_SQLITE_ACTIVE)));
                default -> log.debug("===== default type: {}. =====", datasourceEnum.getSchemaName());
            }
            treeItem.setExpanded(Boolean.TRUE);
            changeStatus(DATASOURCE_CACHE, datasource, Boolean.TRUE);
            databasesTreeView.refresh();
        });

        MenuItem closeItem = new MenuItem("close connection");
        closeItem.setOnAction(event1 -> {
            log.debug("===== close connection. =====");
            switch (datasourceEnum) {
                case MYSQL -> mysqlImages.setImage(new Image(getImageUrl(CodegenConstant.ICON_MYSQL)));
                case SQLITE -> mysqlImages.setImage(new Image(getImageUrl(CodegenConstant.ICON_SQLITE)));
                default -> log.debug("===== default type: {}. =====", datasourceEnum.getSchemaName());
            }
            changeStatus(DATASOURCE_CACHE, datasource, Boolean.FALSE);
            removeCache(DATABASE_CACHE, treeItem.getChildren().stream().map(TreeItem::getValue).collect(Collectors.toList()));
            treeItem.getChildren().clear();
            treeItem.setExpanded(Boolean.FALSE);
            databasesTreeView.refresh();
        });

        MenuItem editItem = new MenuItem("edit connection");
        editItem.setOnAction(event1 -> {
            log.debug("===== edit connection. =====");
            DatasourceController controller = (DatasourceController) loadPage("Edit Connection", FXMLPageEnum.DATASOURCE, Boolean.FALSE, Boolean.FALSE);
            controller.setIndexController(this);
            controller.init(datasource, treeItem);
            controller.showDialogStage();
            databasesTreeView.refresh();
        });

        MenuItem deleteItem = new MenuItem("delete connection");
        deleteItem.setOnAction(event1 -> {
            log.debug("===== delete connection. =====");
            if (AlertUtil.confirm("Confirm to delete connection?")) {
                try (SqlSession session = SqlSessionUtils.buildSessionFactory().openSession(Boolean.TRUE)) {
                    DatasourceMapper mapper = session.getMapper(DatasourceMapper.class);
                    mapper.deleteById(datasource.getId());
                    databasesTreeView.getRoot().getChildren().remove(treeItem);
                    databasesTreeView.refresh();
                }
            }
        });

        contextMenu.getItems().addAll(openItem, closeItem, editItem, deleteItem);
        cell.setContextMenu(contextMenu);
    }

    /**
     * 处理数据库
     *
     * @param cell       单元格
     * @param treeItem   树项目
     * @param datasource 数据源
     */
    private void handleDatabase(TreeCell<String> cell, TreeItem<String> treeItem, Datasource datasource) {
        final ContextMenu contextMenu = new ContextMenu();
        ImageView mysqlImages = (ImageView) treeItem.getGraphic();

        MenuItem openItem = new MenuItem("open database");
        openItem.setOnAction(event1 -> {
            log.debug("===== open database. =====");

            try {
                initTableView(treeItem, datasource);
            } catch (RuntimeException e) {
                AlertUtil.error("Connection Failure.");
                return;
            }
            mysqlImages.setImage(new Image(getImageUrl(CodegenConstant.ICON_DATABASE_ACTIVE)));
            treeItem.setExpanded(Boolean.TRUE);
            changeStatus(DATABASE_CACHE, datasource, Boolean.TRUE);
            databasesTreeView.refresh();
        });

        MenuItem closeItem = new MenuItem("close database");
        closeItem.setOnAction(event1 -> {
            log.debug("===== close database. =====");
            mysqlImages.setImage(new Image(getImageUrl(CodegenConstant.ICON_DATABASE)));
            changeStatus(DATABASE_CACHE, datasource, treeItem.getValue(), Boolean.FALSE);
            removeCache(TABLE_CACHE, treeItem.getChildren().stream().map(TreeItem::getValue).collect(Collectors.toList()));
            treeItem.getChildren().clear();
            treeItem.setExpanded(Boolean.FALSE);
            databasesTreeView.refresh();
        });

        contextMenu.getItems().addAll(openItem, closeItem);
        cell.setContextMenu(contextMenu);
    }

    /**
     * 初始化数据库视图
     *
     * @param treeItem   树项目
     * @param datasource 数据源
     */
    private void initDatabaseView(TreeItem<String> treeItem, Datasource datasource) {
        log.debug("===== load all databases. =====");
        DatasourceEnum datasourceEnum = DatasourceEnum.match(datasource.getType());
        ImageView graphic = (ImageView) treeItem.getGraphic();

        List<String> databaseList = new ArrayList<>();
        DatasourceModel config = new DatasourceModel();
        BeanUtils.copyProperties(datasource, config);
        switch (datasourceEnum) {
            case MYSQL -> {
                try (SqlSession session = SqlSessionUtils.buildSessionFactory(config).openSession(Boolean.TRUE)) {
                    databaseList = session.getMapper(MySQLMapper.class).showDatabases();
                }
                graphic.setImage(new Image(getImageUrl(CodegenConstant.ICON_MYSQL_ACTIVE)));
            }
            case SQLITE -> {
                try (SqlSession session = SqlSessionUtils.buildSessionFactory(config).openSession(Boolean.TRUE)) {
                    databaseList = session.getMapper(SQLiteMapper.class).showDatabases();
                }
                graphic.setImage(new Image(getImageUrl(CodegenConstant.ICON_SQLITE_ACTIVE)));
            }
            default -> log.debug("===== default type: {}. =====", datasourceEnum.getSchemaName());
        }
        initView(treeItem, datasource, databaseList, CodegenConstant.ICON_DATABASE, ItemTypeEnum.DATABASE);
    }

    /**
     * 初始化表视图
     *
     * @param treeItem   树项目
     * @param datasource 数据源
     */
    private void initTableView(TreeItem<String> treeItem, Datasource datasource) {
        log.debug("===== load all tables. =====");
        DatasourceEnum datasourceEnum = DatasourceEnum.match(datasource.getType());
        SqlSessionUtils.test(datasource);

        ImageView mysqlImages = (ImageView) treeItem.getGraphic();
        mysqlImages.setImage(new Image(getImageUrl(CodegenConstant.ICON_DATABASE_ACTIVE)));
        List<String> tableNameList = new ArrayList<>();
        DatasourceModel config = new DatasourceModel();
        BeanUtils.copyProperties(datasource, config);
        config.setDatabaseName(treeItem.getValue());
        switch (datasourceEnum) {
            case MYSQL -> {
                try (SqlSession session = SqlSessionUtils.buildSessionFactory(config).openSession(Boolean.TRUE)) {
                    tableNameList = session.getMapper(MySQLMapper.class).showTables();
                }
            }
            case SQLITE -> {
                try (SqlSession session = SqlSessionUtils.buildSessionFactory(config).openSession(Boolean.TRUE)) {
                    tableNameList = session.getMapper(SQLiteMapper.class).showTables();
                }
            }
            default -> log.debug("===== default type: {}. =====", datasourceEnum.getSchemaName());
        }
        initView(treeItem, datasource, tableNameList, CodegenConstant.ICON_TABLE, ItemTypeEnum.TABLE);
    }

    private void initView(TreeItem<String> treeItem, Datasource datasource, List<String> nameList, String image, ItemTypeEnum typeEnum) {
        if (CollectionUtils.isEmpty(nameList)) {
            return;
        }

        treeItem.getChildren().clear();
        String url = getImageUrl(image);

        nameList.forEach(name -> {
            addItem(treeItem, datasource, name, url, Boolean.FALSE);
            TreeItemModel itemModel = TreeItemModel.builder().parentName(treeItem.getValue()).name(name).image(url).expanded(Boolean.FALSE).datasource(datasource).build();
            switch (typeEnum) {
                case DATABASE -> addDatabaseCache(datasource, itemModel);
                case TABLE -> addTableCache(datasource, itemModel);
            }
        });
    }

    public void createMySQLDatasource(ActionEvent actionEvent) {
        log.debug("===== create MySQL datasource =====");
        DatasourceController controller = (DatasourceController) loadPage("Sava Connection", FXMLPageEnum.DATASOURCE, Boolean.FALSE, Boolean.FALSE);
        controller.setIndexController(this);
        controller.init(null, 0);
        controller.showDialogStage();
    }

    public void clear(MouseEvent mouseEvent) {
        log.debug("===== clear search test. =====");
        if (StringUtils.isEmpty(SEARCH_CACHE.toString())) {
            searchViewVBox.setVisible(Boolean.FALSE);
        }
        searchTextField.setText(null);
        SEARCH_CACHE.delete(0, SEARCH_CACHE.length());
    }

    public void fixed(MouseEvent mouseEvent) {
        Boolean active = (Boolean) fixedImageView.getUserData();
        if (active) {
            fixedImageView.setUserData(Boolean.FALSE);
            fixedImageView.setImage(new Image(getImageUrl(CodegenConstant.ICON_FIXED)));
        } else {
            fixedImageView.setUserData(Boolean.TRUE);
            fixedImageView.setImage(new Image(getImageUrl(CodegenConstant.ICON_FIXED_ACTIVE)));
        }
    }

    public void pressed(KeyEvent keyEvent) {
        if (!searchTextField.isFocused()) {
            searchTextField.requestFocus();
        }
        if (!searchViewVBox.isVisible()) {
            searchViewVBox.setVisible(Boolean.TRUE);
        }
    }

    public void searchKeyPressed(KeyEvent keyEvent) {
        log.debug(SEARCH_CACHE.toString());
        if (KeyCode.ESCAPE == keyEvent.getCode()) {
            if (StringUtils.isEmpty(SEARCH_CACHE.toString())) {
                searchViewVBox.setVisible(Boolean.FALSE);
            }

            SEARCH_CACHE.delete(0, SEARCH_CACHE.length());
            searchTextField.setText(null);
        } else {
            searchViewVBox.setVisible(Boolean.TRUE);
            SEARCH_CACHE.append(keyEvent.getText());
        }
    }

    public void choiceFolder(MouseEvent mouseEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("choice folder");
        chooser.setInitialDirectory(new File(System.getProperty("java.io.tmpdir")));
        File file = chooser.showDialog(getPrimaryStage());
        if (Objects.isNull(file)) {
            return;
        }
        folderTextField.setText(file.getPath());
        this.template.setFolder(file.getPath());
        log.debug("folder path: {}", file.getPath());
    }

    public void save(MouseEvent mouseEvent) {
        try (SqlSession session = SqlSessionUtils.buildSessionFactory().openSession(Boolean.TRUE)) {
            TemplateMapper mapper = session.getMapper(TemplateMapper.class);
            Template old = mapper.selectByName(this.template.getName());
            if (Objects.isNull(this.datasource)) {
                AlertUtil.warn("datasource can not be empty.");
                return;
            }
            this.template.setDatasourceId(this.datasource.getId());
            if (Objects.nonNull(old)) {
                this.template.setId(old.getId());
                mapper.updateById(template);
                AlertUtil.info("edite success.");
            } else {
                template.setId(String.valueOf(App.applicationContext.getBean(Sequence.class).nextId()));
                mapper.insert(template);
            }
        }
        AlertUtil.info("save success.");
    }

    public void reset(MouseEvent mouseEvent) {
        folderTextField.setText(CommonConstant.EMPTY);
        parentPackageNameTextField.setText(CommonConstant.EMPTY);
        moduleNameTextField.setText(CommonConstant.EMPTY);
        tableNameTextField.setText(CommonConstant.EMPTY);
        entityNameTextField.setText(CommonConstant.EMPTY);
        mapperNameTextField.setText(CommonConstant.EMPTY);
        serviceNameTextField.setText(CommonConstant.EMPTY);
        controllerNameTextField.setText(CommonConstant.EMPTY);
    }

    public void submit(MouseEvent mouseEvent) {
        String parentPackage = this.template.getParentPackage();
        String module = this.template.getModule();
        if (Objects.isNull(parentPackage)) {
            parentPackage = "";
        }
        if (Objects.isNull(module)) {
            module = "";
        }

        if (Objects.isNull(this.datasource)) {
            AlertUtil.warn("datasource can not be empty");
            return;
        }
        com.loongstudio.codegen.component.codegen.CodegenComponent component = App.applicationContext.getBean(com.loongstudio.codegen.component.codegen.CodegenComponent.class);
        component.generate(
                CodegenModel.builder()
                        .url(SqlSessionUtils.buildMySQLDatabaseUrl(this.datasource.getIp(), this.datasource.getPort(), DatasourceEnum.MYSQL.getSchemaName(), this.template.getDatabaseName()))
                        .username(this.datasource.getUsername())
                        .password(this.datasource.getPassword())
                        .tableList(List.of(this.template.getTableName()))
                        .parent(parentPackage)
                        .moduleName(module)
                        .outputDir(this.template.getFolder())
                        .haveVue(Boolean.FALSE)
                        .superEntityColumnList(SUPER_ENTITY_COLUMNS)
                        .build());
    }

    public void open(ActionEvent actionEvent) {
        TemplateController controller = (TemplateController) loadPage("Template Manager", FXMLPageEnum.TEMPLATE, Boolean.FALSE);
        controller.setIndexController(this);
        controller.showDialogStage();
    }

    public void openRecentMenu(Event event) {
        loadRecentTemplateMenu();
    }

    public void focus(MouseEvent mouseEvent) {
        databasesTreeView.setFocusTraversable(Boolean.TRUE);
    }

    public void about(ActionEvent actionEvent) {
        AboutController controller = (AboutController) loadPage("About Codegen", FXMLPageEnum.ABOUT, Boolean.FALSE, Boolean.TRUE);
        controller.setParentController(this);
        controller.showDialogStage();
    }

}
