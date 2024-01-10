package com.ronnaces.loong.codegen.controller;

import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.ronnaces.loong.codegen.App;
import com.ronnaces.loong.codegen.api.entity.Datasource;
import com.ronnaces.loong.codegen.api.entity.Template;
import com.ronnaces.loong.codegen.api.mapper.DatasourceMapper;
import com.ronnaces.loong.codegen.api.mapper.SQLiteMapper;
import com.ronnaces.loong.codegen.api.mapper.TemplateMapper;
import com.ronnaces.loong.codegen.component.codegen.CodegenComponent;
import com.ronnaces.loong.codegen.constant.CodegenConstant;
import com.ronnaces.loong.codegen.enums.DatasourceEnum;
import com.ronnaces.loong.codegen.enums.FXMLPageEnum;
import com.ronnaces.loong.codegen.enums.ItemTypeEnum;
import com.ronnaces.loong.codegen.mapper.MySQLMapper;
import com.ronnaces.loong.codegen.mapper.OracleMapper;
import com.ronnaces.loong.codegen.mapper.PostgresqlMapper;
import com.ronnaces.loong.codegen.model.CodegenModel;
import com.ronnaces.loong.codegen.model.DatasourceModel;
import com.ronnaces.loong.codegen.model.TreeItemModel;
import com.ronnaces.loong.codegen.util.*;
import com.ronnaces.loong.core.constant.CommonConstant;
import com.ronnaces.loong.core.lang.StringUtil;
import com.ronnaces.loong.core.time.DatePattern;
import com.ronnaces.loong.core.time.LocalDateTimeUtil;
import com.ronnaces.loong.core.toolkit.Sequence;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
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
import java.time.LocalDateTime;
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

    public static final List<String> SUPER_ENTITY_COLUMNS = List.of("id", "createTime", "createBy", "updateTime", "updateBy", "description", "whetherDelete");

    private static final StringBuilder SEARCH_CACHE = new StringBuilder();

    public MenuItem aboutMenuItem;

    public Menu languageMenu;

    public MenuItem englishMenuItem;

    public MenuItem chineseMenuItem;

    private Template template = new Template();

    public MenuItem openMenuItem;

    public Menu openRecentMenu;

    private Datasource datasource;

    private final DatasourceModel config = new DatasourceModel();

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
     * init
     */
    private void init() {
        initTreeView();
    }

    /**
     * init tree view
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
                this.template = templateMapper.selectById(template.getId());
                this.datasource = datasourceMapper.selectById(this.getTemplate().getDatasourceId());
                menuItem.setOnAction(actionEvent -> loadCurrentTemplate());
                menuItemList.add(menuItem);
            });
        }
        this.openRecentMenu.getItems().setAll(menuItemList);
    }

    protected void loadCurrentTemplate() {
        this.folderTextField.setText(this.template.getFolder());
        this.parentPackageNameTextField.setText(this.template.getParentPackage());
        this.moduleNameTextField.setText(this.template.getModule());
        this.tableNameTextField.setText(this.template.getTableName());
        String entityName = StringUtil.firstToUpperCase(StringUtil.underlineToCamel(this.template.getTableName()));
        this.entityNameTextField.setText(entityName);
        this.mapperNameTextField.setText(StringUtils.joinWith(CommonConstant.EMPTY, entityName, ConstVal.MAPPER));
        this.serviceNameTextField.setText(StringUtils.joinWith(CommonConstant.EMPTY, entityName, ConstVal.SERVICE));
        this.controllerNameTextField.setText(StringUtils.joinWith(CommonConstant.EMPTY, entityName, ConstVal.CONTROLLER));

        AlertUtil.info(ResourceBundleUtil.getProperty("Success"));
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
        folderTextField.textProperty().addListener((observableValue, v1, v2) -> {
            String value = observableValue.getValue();
            this.template.setFolder(value);
            log.debug("========== folder: {}, {}, {} ==========", value, v1, v2);
        });
    }

    /**
     * load datasource
     */
    private void loadDatasource() {
        log.debug("===== load all datasource. =====");
        databasesTreeView.setShowRoot(Boolean.TRUE);
        ImageView imageView = ImageViewUtil.getImageView(ImageUtil.getImageUrl(CodegenConstant.ICON_CONNECTION), 20, 20);
        TreeItem<String> root = new TreeItem<>(ResourceBundleUtil.getProperty("Connection"), imageView);

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
     * load search window
     */
    private void loadSearchWindow() {
        log.debug("===== init search window. =====");
        fixedImageView.setUserData(Boolean.FALSE);
        fixedImageView.setImage(ImageUtil.getImage(CodegenConstant.ICON_FIXED));
        searchImageView.setImage(ImageUtil.getImage(CodegenConstant.ICON_SEARCH_ACTIVE));
        clearImageView.setImage(ImageUtil.getImage(CodegenConstant.ICON_CLEAR));

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
     * handle menu
     *
     * @param cell  cell
     * @param level level
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
     * handle event
     *
     * @param treeItem treeItem
     * @param event    event
     * @param level    level
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
                AlertUtil.error(ResourceBundleUtil.getProperty("Failure"));
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
     * handle datasource
     *
     * @param cell       cell
     * @param treeItem   treeItem
     * @param datasource datasource
     */
    private void handleDatasource(TreeCell<String> cell, TreeItem<String> treeItem, Datasource datasource) {
        final ContextMenu contextMenu = new ContextMenu();
        ImageView mysqlImages = (ImageView) treeItem.getGraphic();
        DatasourceEnum datasourceEnum = DatasourceEnum.match(datasource.getType());

        MenuItem openItem = new MenuItem(ResourceBundleUtil.getProperty("OpenConnection"));
        openItem.setOnAction(event1 -> {
            log.debug("===== open connection. =====");
            try {
                initDatabaseView(treeItem, datasource);
            } catch (RuntimeException e) {
                e.printStackTrace();
                AlertUtil.error(ResourceBundleUtil.getProperty("Failure"));
                return;
            }
            switch (datasourceEnum) {
                case MYSQL -> mysqlImages.setImage(ImageUtil.getImage(CodegenConstant.ICON_MYSQL_ACTIVE));
                case SQLITE -> mysqlImages.setImage(ImageUtil.getImage(CodegenConstant.ICON_SQLITE_ACTIVE));
                case MARIA_DB -> mysqlImages.setImage(ImageUtil.getImage(CodegenConstant.ICON_MARIA_DB_ACTIVE));
                case POSTGRESQL -> mysqlImages.setImage(ImageUtil.getImage(CodegenConstant.ICON_POSTGRESQL_ACTIVE));
                case ORACLE -> mysqlImages.setImage(ImageUtil.getImage(CodegenConstant.ICON_ORACLE_ACTIVE));
                default -> mysqlImages.setImage(ImageUtil.getImage(CodegenConstant.ICON_DATASOURCE_ACTIVE));
            }
            treeItem.setExpanded(Boolean.TRUE);
            changeStatus(DATASOURCE_CACHE, datasource, Boolean.TRUE);
            databasesTreeView.refresh();
        });

        MenuItem closeItem = new MenuItem(ResourceBundleUtil.getProperty("CloseConnection"));
        closeItem.setOnAction(event1 -> {
            log.debug("===== close connection. =====");
            switch (datasourceEnum) {
                case MYSQL -> mysqlImages.setImage(ImageUtil.getImage(CodegenConstant.ICON_MYSQL));
                case SQLITE -> mysqlImages.setImage(ImageUtil.getImage(CodegenConstant.ICON_SQLITE));
                case MARIA_DB -> mysqlImages.setImage(ImageUtil.getImage(CodegenConstant.ICON_MARIA_DB));
                case POSTGRESQL -> mysqlImages.setImage(ImageUtil.getImage(CodegenConstant.ICON_POSTGRESQL));
                case ORACLE -> mysqlImages.setImage(ImageUtil.getImage(CodegenConstant.ICON_ORACLE));
                default -> log.debug("===== default type: {}. =====", datasourceEnum.getSchemaName());
            }
            changeStatus(DATASOURCE_CACHE, datasource, Boolean.FALSE);
            removeCache(DATABASE_CACHE, treeItem.getChildren().stream().map(TreeItem::getValue).collect(Collectors.toList()));
            treeItem.getChildren().clear();
            treeItem.setExpanded(Boolean.FALSE);
            databasesTreeView.refresh();
        });

        MenuItem editItem = new MenuItem(ResourceBundleUtil.getProperty("EditConnection"));
        editItem.setOnAction(event1 -> {
            log.debug("===== edit connection. =====");
            switch (datasourceEnum) {
                case MYSQL -> {
                    DatasourceController controller = (DatasourceController) loadPage("Edit Connection", FXMLPageEnum.DATASOURCE, Boolean.FALSE, Boolean.FALSE);
                    controller.setIndexController(this);
                    controller.edit(datasource, treeItem);
                    controller.showDialogStage();
                }
                case SQLITE -> {
                    SQLiteController controller = (SQLiteController) loadPage("Edit Connection", FXMLPageEnum.SQLITE_DATASOURCE, Boolean.FALSE, Boolean.FALSE);
                    controller.setIndexController(this);
                    controller.edit(datasource, treeItem);
                    controller.showDialogStage();
                }
                case MARIA_DB -> {
                    MariaController controller = (MariaController) loadPage("Edit Connection", FXMLPageEnum.MARIA_DATASOURCE, Boolean.FALSE, Boolean.FALSE);
                    controller.setIndexController(this);
                    controller.edit(datasource, treeItem);
                    controller.showDialogStage();
                }
                case POSTGRESQL -> {
                    PostgreSQLController controller = (PostgreSQLController) loadPage("Edit Connection", FXMLPageEnum.POSTGRESQL_DATASOURCE, Boolean.FALSE, Boolean.FALSE);
                    controller.setIndexController(this);
                    controller.edit(datasource, treeItem);
                    controller.showDialogStage();
                }
                case ORACLE -> {
                    OracleController controller = (OracleController) loadPage("Edit Connection", FXMLPageEnum.ORACLE_DATASOURCE, Boolean.FALSE, Boolean.FALSE);
                    controller.setIndexController(this);
                    controller.edit(datasource, treeItem);
                    controller.showDialogStage();
                }
                default -> log.debug("===== default type: {}. =====", datasourceEnum.getSchemaName());
            }

            databasesTreeView.refresh();
        });

        MenuItem deleteItem = new MenuItem(ResourceBundleUtil.getProperty("DeleteConnection"));
        deleteItem.setOnAction(event1 -> {
            log.debug("===== delete connection. =====");
            if (AlertUtil.confirm(ResourceBundleUtil.getProperty("ConfirmDelete"))) {
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
     * handle database
     *
     * @param cell       cell
     * @param treeItem   treeItem
     * @param datasource datasource
     */
    private void handleDatabase(TreeCell<String> cell, TreeItem<String> treeItem, Datasource datasource) {
        final ContextMenu contextMenu = new ContextMenu();
        ImageView mysqlImages = (ImageView) treeItem.getGraphic();

        MenuItem openItem = new MenuItem(ResourceBundleUtil.getProperty("OpenDatasource"));
        openItem.setOnAction(event1 -> {
            log.debug("===== open database. =====");

            try {
                initTableView(treeItem, datasource);
            } catch (RuntimeException e) {
                AlertUtil.error(ResourceBundleUtil.getProperty("Failure"));
                return;
            }
            mysqlImages.setImage(ImageUtil.getImage(CodegenConstant.ICON_DATABASE_ACTIVE));
            treeItem.setExpanded(Boolean.TRUE);
            changeStatus(DATABASE_CACHE, datasource, Boolean.TRUE);
            databasesTreeView.refresh();
        });

        MenuItem closeItem = new MenuItem(ResourceBundleUtil.getProperty("CloseDatasource"));
        closeItem.setOnAction(event1 -> {
            log.debug("===== close database. =====");
            mysqlImages.setImage(ImageUtil.getImage(CodegenConstant.ICON_DATABASE));
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
     * init database view
     *
     * @param treeItem   treeItem
     * @param datasource datasource
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
                graphic.setImage(ImageUtil.getImage(CodegenConstant.ICON_MYSQL_ACTIVE));
            }
            case SQLITE -> {
                try (SqlSession session = SqlSessionUtils.buildSessionFactory(config).openSession(Boolean.TRUE)) {
                    databaseList = session.getMapper(SQLiteMapper.class).showDatabases();
                }
                graphic.setImage(ImageUtil.getImage(CodegenConstant.ICON_SQLITE_ACTIVE));
            }
            case MARIA_DB -> {
                try (SqlSession session = SqlSessionUtils.buildSessionFactory(config).openSession(Boolean.TRUE)) {
                    databaseList = session.getMapper(MySQLMapper.class).showDatabases();
                }
                graphic.setImage(ImageUtil.getImage(CodegenConstant.ICON_MARIA_DB_ACTIVE));
            }
            case POSTGRESQL -> {
                try (SqlSession session = SqlSessionUtils.buildSessionFactory(config).openSession(Boolean.TRUE)) {
                    databaseList = session.getMapper(PostgresqlMapper.class).showDatabases();
                }
                graphic.setImage(ImageUtil.getImage(CodegenConstant.ICON_POSTGRESQL_ACTIVE));
            }
            case ORACLE -> {
                try (SqlSession session = SqlSessionUtils.buildSessionFactory(config).openSession(Boolean.TRUE)) {
                    databaseList = session.getMapper(OracleMapper.class).showDatabases();
                }
                graphic.setImage(ImageUtil.getImage(CodegenConstant.ICON_ORACLE_ACTIVE));
            }
            default -> log.debug("===== default type: {}. =====", datasourceEnum.getSchemaName());
        }
        initView(treeItem, datasource, databaseList, CodegenConstant.ICON_DATABASE, ItemTypeEnum.DATABASE);
    }

    /**
     * init table view
     *
     * @param treeItem   treeItem
     * @param datasource datasource
     */
    private void initTableView(TreeItem<String> treeItem, Datasource datasource) {
        log.debug("===== load all tables. =====");
        DatasourceEnum datasourceEnum = DatasourceEnum.match(datasource.getType());
        SqlSessionUtils.test(datasource);

        ImageView mysqlImages = (ImageView) treeItem.getGraphic();
        mysqlImages.setImage(ImageUtil.getImage(CodegenConstant.ICON_DATABASE_ACTIVE));
        List<String> tableNameList = new ArrayList<>();
        BeanUtils.copyProperties(datasource, config);
        config.setDatabaseName(treeItem.getValue());
        switch (datasourceEnum) {
            case MYSQL, MARIA_DB -> {
                try (SqlSession session = SqlSessionUtils.buildSessionFactory(config).openSession(Boolean.TRUE)) {
                    tableNameList = session.getMapper(MySQLMapper.class).showTables();
                }
            }
            case SQLITE -> {
                try (SqlSession session = SqlSessionUtils.buildSessionFactory(config).openSession(Boolean.TRUE)) {
                    tableNameList = session.getMapper(SQLiteMapper.class).showTables();
                }
            }
            case POSTGRESQL -> {
                try (SqlSession session = SqlSessionUtils.buildSessionFactory(config).openSession(Boolean.TRUE)) {
                    tableNameList = session.getMapper(PostgresqlMapper.class).showTables();
                }
            }
            case ORACLE -> {
                try (SqlSession session = SqlSessionUtils.buildSessionFactory(config).openSession(Boolean.TRUE)) {
                    tableNameList = session.getMapper(OracleMapper.class).showTables();
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
        String url = ImageUtil.getImageUrl(image);

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
        DatasourceController controller = (DatasourceController) loadPage(ResourceBundleUtil.getProperty("SaveConnection"), FXMLPageEnum.DATASOURCE, Boolean.FALSE, Boolean.FALSE);
        controller.setIndexController(this);
        controller.init(DatasourceEnum.MYSQL.ordinal());
        controller.showDialogStage();
    }

    public void createSQLiteDatasource(ActionEvent actionEvent) {
        log.debug("===== create SQLite datasource =====");
        SQLiteController controller = (SQLiteController) loadPage(ResourceBundleUtil.getProperty("SaveConnection"), FXMLPageEnum.SQLITE_DATASOURCE, Boolean.FALSE, Boolean.FALSE);
        controller.setIndexController(this);
        controller.init(DatasourceEnum.SQLITE.ordinal());
        controller.showDialogStage();
    }

    public void createMariaDBDatasource(ActionEvent actionEvent) {
        log.debug("===== create MariaDB datasource =====");
        MariaController controller = (MariaController) loadPage(ResourceBundleUtil.getProperty("SaveConnection"), FXMLPageEnum.MARIA_DATASOURCE, Boolean.FALSE, Boolean.FALSE);
        controller.setIndexController(this);
        controller.init(DatasourceEnum.MARIA_DB.ordinal());
        controller.showDialogStage();
    }

    public void createPostgreSQLDatasource(ActionEvent actionEvent) {
        log.debug("===== create PostgreSQL datasource =====");
        PostgreSQLController controller = (PostgreSQLController) loadPage(ResourceBundleUtil.getProperty("SaveConnection"), FXMLPageEnum.POSTGRESQL_DATASOURCE, Boolean.FALSE, Boolean.FALSE);
        controller.setIndexController(this);
        controller.init(DatasourceEnum.POSTGRESQL.ordinal());
        controller.showDialogStage();
    }

    public void createOracleDatasource(ActionEvent actionEvent) {
        log.debug("===== create Oracle datasource =====");
        OracleController controller = (OracleController) loadPage(ResourceBundleUtil.getProperty("SaveConnection"), FXMLPageEnum.ORACLE_DATASOURCE, Boolean.FALSE, Boolean.FALSE);
        controller.setIndexController(this);
        controller.init(DatasourceEnum.ORACLE.ordinal());
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
            fixedImageView.setImage(ImageUtil.getImage(CodegenConstant.ICON_FIXED));
        } else {
            fixedImageView.setUserData(Boolean.TRUE);
            fixedImageView.setImage(ImageUtil.getImage(CodegenConstant.ICON_FIXED_ACTIVE));
        }
    }

    public void pressed() {
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

    public void choiceFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(ResourceBundleUtil.getProperty("ChoiceFolder"));
        if (StringUtils.isEmpty(folderTextField.getText())) {
            chooser.setInitialDirectory(new File(System.getProperty("java.io.tmpdir")));
        } else {
            chooser.setInitialDirectory(new File(folderTextField.getText()));
        }
        File file = chooser.showDialog(getPrimaryStage());
        if (Objects.isNull(file)) {
            return;
        }
        folderTextField.setText(file.getPath());
        this.template.setFolder(file.getPath());
        log.debug("folder path: {}", file.getPath());
    }

    public void save() {
        try (SqlSession session = SqlSessionUtils.buildSessionFactory().openSession(Boolean.TRUE)) {
            TemplateMapper mapper = session.getMapper(TemplateMapper.class);
            Template old = mapper.selectByName(this.template.getName());
            if (Objects.isNull(this.datasource)) {
                AlertUtil.warn(ResourceBundleUtil.getProperty("NullPointerException"));
                return;
            }
            this.template.setDatasourceId(this.datasource.getId());
            String dataTime = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.NORM_DATETIME_FORMATTER);
            this.template.setName(new StringJoiner(CommonConstant.PERIOD).add(this.datasource.getName()).add(config.getDatabaseName()).add(this.template.getTableName()).toString());
            if (Objects.nonNull(old)) {
                this.template.setId(old.getId());
                this.template.setUpdateTime(dataTime);
                mapper.updateById(template);
                AlertUtil.info(ResourceBundleUtil.getProperty("Success"));
            } else {
                template.setId(String.valueOf(App.applicationContext.getBean(Sequence.class).nextId()));
                this.template.setCreateTime(dataTime);
                mapper.insert(template);
            }
        }
        AlertUtil.info(ResourceBundleUtil.getProperty("Success"));
    }

    public void reset() {
        folderTextField.setText(CommonConstant.EMPTY);
        parentPackageNameTextField.setText(CommonConstant.EMPTY);
        moduleNameTextField.setText(CommonConstant.EMPTY);
        tableNameTextField.setText(CommonConstant.EMPTY);
        entityNameTextField.setText(CommonConstant.EMPTY);
        mapperNameTextField.setText(CommonConstant.EMPTY);
        serviceNameTextField.setText(CommonConstant.EMPTY);
        controllerNameTextField.setText(CommonConstant.EMPTY);
    }

    public void submit() {
        String parentPackage = this.template.getParentPackage();
        String module = this.template.getModule();
        if (Objects.isNull(parentPackage)) {
            parentPackage = "";
        }
        if (Objects.isNull(module)) {
            module = "";
        }

        if (Objects.isNull(this.datasource)) {
            AlertUtil.warn(ResourceBundleUtil.getProperty("NullPointerException"));
            return;
        }
        CodegenComponent component = App.applicationContext.getBean(CodegenComponent.class);
        DatasourceEnum datasourceEnum = DatasourceEnum.match(datasource.getType());
        DatasourceModel config = new DatasourceModel();
        BeanUtils.copyProperties(datasource, config);
        config.setDatabaseName(template.getDatabaseName());
        component.generate(
                CodegenModel.builder()
                        .url(SqlSessionUtils.buildDatabaseUrl(config, datasourceEnum))
                        .username(this.datasource.getUsername())
                        .password(this.datasource.getPassword())
                        .tableList(List.of(this.template.getTableName()))
                        .parent(parentPackage)
                        .moduleName(module)
                        .outputDir(this.template.getFolder())
                        .haveVue(Boolean.FALSE)
                        .superEntityColumnList(SUPER_ENTITY_COLUMNS)
                        .build());
        AlertUtil.info(ResourceBundleUtil.getProperty("Success"));
    }

    public void open(ActionEvent actionEvent) {
        TemplateController controller = (TemplateController) loadPage(ResourceBundleUtil.getProperty("TemplateManager"), FXMLPageEnum.TEMPLATE, Boolean.FALSE);
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
        AboutController controller = (AboutController) loadPage(ResourceBundleUtil.getProperty("AboutCodegen"), FXMLPageEnum.ABOUT, Boolean.FALSE, Boolean.TRUE);
        controller.setParentController(this);
        controller.showDialogStage();
    }

    public void switchChinese(ActionEvent actionEvent) {
        chineseMenuItem.setGraphic(ImageViewUtil.getImageView(ImageUtil.getImageUrl(CodegenConstant.ICON_SUBMIT), 16, 16));
        englishMenuItem.setGraphic(null);
    }

    public void switchEnglish(ActionEvent actionEvent) {
        chineseMenuItem.setGraphic(null);
        englishMenuItem.setGraphic(ImageViewUtil.getImageView(ImageUtil.getImageUrl(CodegenConstant.ICON_SUBMIT), 16, 16));
    }

}
