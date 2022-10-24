package com.loongstudio.codegen.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.loongstudio.codegen.api.entity.Template;
import com.loongstudio.codegen.api.entity.TemplateExample;
import com.loongstudio.codegen.api.mapper.DatasourceMapper;
import com.loongstudio.codegen.api.mapper.TemplateMapper;
import com.loongstudio.codegen.constant.CodegenConstant;
import com.loongstudio.codegen.entity.OperationEntity;
import com.loongstudio.codegen.enums.FXMLPageEnum;
import com.loongstudio.codegen.enums.OperationEnum;
import com.loongstudio.codegen.enums.SqlKeyword;
import com.loongstudio.codegen.model.TemplateModel;
import com.loongstudio.codegen.util.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.BeanUtils;

import java.net.URL;
import java.util.*;

/**
 * Template Controller
 *
 * @author KunLong-Luo
 * @since 2022/09/25 18:08
 */
@Slf4j
@Getter
@Setter
public class TemplateController extends BaseController {

    public Pagination pagination;

    public TableView<TemplateModel> templateTableView;

    public Text titleText;

    public BorderPane rootBorderPane;

    public TableColumn<TemplateModel, String> nameTableColumn;

    public TableColumn<TemplateModel, String> folderTableColumn;

    public TableColumn<TemplateModel, String> parentPackageTableColumn;

    public TableColumn<TemplateModel, String> moduleTableColumn;

    public TableColumn<TemplateModel, String> createdAtTableColumn;

    public TableColumn<TemplateModel, String> updatedAtTableColumn;

    public TableColumn<TemplateModel, String> operationTableColumn;

    public TableColumn<TemplateModel, String> noTableColumn;

    public TableColumn<TemplateModel, Boolean> checkTableColumn;

    public TableColumn<TemplateModel, String> idTableColumn;

    public CheckBox checkCheckBox;

    public Button refreshButton;

    public Button batchDeleteButton;

    public TextField nameQueryTextField;

    public Button queryButton;

    public Button resetButton;

    public List<CheckBox> checkCheckBoxListCache = new ArrayList<>();

    public Set<String> selectedIdList = new TreeSet<>();

    private IndexController indexController;

    private OperationEntity operationEntity;

    private Template template;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
    }

    protected void init() {
        this.pagination.setMaxPageIndicatorCount(10);
        this.pagination.currentPageIndexProperty().addListener((observableValue, v1, v2) -> {
            Number value = observableValue.getValue();
            this.operationEntity.setPageNo(value.intValue() + 1);
            listReset(this.template, this.operationEntity);
        });

        this.operationEntity = new OperationEntity();
        init(this.template, this.operationEntity);
    }

    private void init(Template template, OperationEntity operationEntity) {
        try (SqlSession session = SqlSessionUtils.buildSessionFactory().openSession(Boolean.TRUE)) {
            TemplateMapper mapper = session.getMapper(TemplateMapper.class);

            TemplateExample example = new TemplateExample();
            TemplateExample.Criteria criteria = example.createCriteria();
            criteria.andHaveDeletedEqualTo(0);
            List<OperationEntity.Query> queryList = operationEntity.getQueryList();
            if (CollectionUtils.isNotEmpty(queryList)) {
                queryList.forEach(query -> super.appendQuery(criteria, query));
            }
            example.setOrderByClause("id DESC");
            PageHelper.startPage(operationEntity.getPageNo(), operationEntity.getPageSize());
            List<Template> templates = mapper.selectByExample(example);
            PageInfo<Template> pageInfo = new PageInfo<>(templates);
            List<TemplateModel> modelList = templates.stream().map(recode -> {
                TemplateModel model = TemplateModel.builder().build();
                BeanUtils.copyProperties(recode, model);
                return model;
            }).toList();

            this.pagination.setCurrentPageIndex(operationEntity.getPageNo() - 1);
            this.pagination.setPageCount(pageInfo.getPageNum());
            this.pagination.setMaxPageIndicatorCount(pageInfo.getPageNum());
            this.templateTableView.getItems().addAll(modelList);
        }


        // 获取选中的行序号
        int selectedIndex = this.templateTableView.getSelectionModel().getSelectedIndex();
        // 获取选中的单行数据
        TemplateModel selectedItem = this.templateTableView.getSelectionModel().getSelectedItem();
        // 获取选中的多行数据
        ObservableList<TemplateModel> selectedItems = this.templateTableView.getSelectionModel().getSelectedItems();
        // 取消选择
        this.templateTableView.getSelectionModel().clearSelection();
        this.checkCheckBox.setSelected(Boolean.FALSE);

        // 设置多选模式
        this.templateTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.idTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        this.nameTableColumn.setCellValueFactory(new PropertyValueFactory<>(ResourceBundleUtil.getProperty("Name")));
        this.folderTableColumn.setCellValueFactory(new PropertyValueFactory<>(ResourceBundleUtil.getProperty("Folder")));
        this.parentPackageTableColumn.setCellValueFactory(new PropertyValueFactory<>(ResourceBundleUtil.getProperty("ParentPackage")));
        this.moduleTableColumn.setCellValueFactory(new PropertyValueFactory<>(ResourceBundleUtil.getProperty("Module")));
        this.createdAtTableColumn.setCellValueFactory(new PropertyValueFactory<>(ResourceBundleUtil.getProperty("CreatedAt")));
        this.noTableColumn.setCellFactory((col) -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                this.setText(null);
                this.setGraphic(null);
                if (!empty) {
                    this.setText(String.valueOf(this.getIndex() + 1));
                }
            }
        });
        this.checkTableColumn.setCellFactory((col) -> {
            final CheckBox checkBox = new CheckBox();
            TableCell<TemplateModel, Boolean> tableCell = new TableCell<>() {
                @Override
                protected void updateItem(Boolean var1, boolean var2) {
                    super.updateItem(var1, var2);
                    HBox hBox = new HBox();
                    hBox.getChildren().add(checkBox);
                    hBox.setAlignment(Pos.CENTER);
                    hBox.setPrefHeight(16);
                    hBox.setPrefWidth(58);
                    hBox.setSpacing(10);

                    if (var2) {
                        this.setText(null);
                        this.setGraphic(null);
                    } else {
                        this.setGraphic(hBox);
                    }
                }
            };

            checkBox.selectedProperty().addListener((observableValue, v1, v2) -> {
                TemplateModel templateModel = templateTableView.getItems().get(tableCell.getIndex());
                Boolean selected = observableValue.getValue();
                if (selected) {
                    selectedIdList.add(templateModel.getId());
                } else {
                    selectedIdList.remove(templateModel.getId());
                }
            });

            checkCheckBoxListCache.add(checkBox);
            return tableCell;
        });
        this.operationTableColumn.setCellFactory((col) -> {
            final ImageView loadImageView = ImageViewUtil.getImageView(ImageUtil.getImageUrl(CodegenConstant.ICON_LOAD), 16, 16);
            final ImageView detailsImageView = ImageViewUtil.getImageView(ImageUtil.getImageUrl(CodegenConstant.ICON_DETAILS), 16, 16);
            final ImageView editImageView = ImageViewUtil.getImageView(ImageUtil.getImageUrl(CodegenConstant.ICON_EDIT), 16, 16);
            final ImageView deleteImageView = ImageViewUtil.getImageView(ImageUtil.getImageUrl(CodegenConstant.ICON_DELETE), 16, 16);
            TableCell<TemplateModel, String> tableCell = new TableCell<>() {
                @Override
                protected void updateItem(String var1, boolean var2) {
                    super.updateItem(var1, var2);
                    HBox hBox = new HBox();
                    hBox.getChildren().addAll(loadImageView, detailsImageView, editImageView, deleteImageView);
                    hBox.setAlignment(Pos.CENTER);
                    hBox.setPrefHeight(16);
                    hBox.setPrefWidth(58);
                    hBox.setSpacing(10);

                    if (var2) {
                        this.setText(null);
                        this.setGraphic(null);
                    } else {
                        this.setGraphic(hBox);
                    }
                }
            };
            loadImageView.setOnMouseClicked(evenHandler -> {
                TemplateModel templateModel = templateTableView.getItems().get(tableCell.getIndex());
                try (SqlSession session = SqlSessionUtils.buildSessionFactory().openSession(Boolean.TRUE)) {
                    TemplateMapper templateMapper = session.getMapper(TemplateMapper.class);
                    DatasourceMapper datasourceMapper = session.getMapper(DatasourceMapper.class);
                    this.getIndexController().loadCurrentTemplate(templateMapper, datasourceMapper, templateModel.getId());
                }
                closeDialogStage();
            });
            detailsImageView.setOnMouseClicked(evenHandler -> {
                TemplateDetailsController controller = (TemplateDetailsController) loadPage(ResourceBundleUtil.getProperty("TemplateDetails"), FXMLPageEnum.TEMPLATE_DETAILS, Boolean.FALSE, Boolean.FALSE);
                TemplateModel templateModel = templateTableView.getItems().get(tableCell.getIndex());
                controller.setCurrentTemplateModel(templateModel);
                controller.setTemplateController(this);
                controller.getTitleText().setText("Template Details");
                controller.init(OperationEnum.DETAIL);
                controller.showDialogStage();
            });
            editImageView.setOnMouseClicked(evenHandler -> {
                TemplateDetailsController controller = (TemplateDetailsController) loadPage(ResourceBundleUtil.getProperty("TemplateEdit"), FXMLPageEnum.TEMPLATE_DETAILS, Boolean.FALSE, Boolean.FALSE);
                TemplateModel templateModel = templateTableView.getItems().get(tableCell.getIndex());
                controller.setCurrentTemplateModel(templateModel);
                controller.setTemplateController(this);
                controller.getTitleText().setText(ResourceBundleUtil.getProperty("TemplateEdit"));
                controller.init(OperationEnum.EDIT);
                controller.showDialogStage();
            });
            deleteImageView.setOnMouseClicked(evenHandler -> {
                if (AlertUtil.confirm(ResourceBundleUtil.getProperty("ConfirmDelete"))) {
                    TemplateModel templateModel = templateTableView.getItems().get(tableCell.getIndex());
                    try (SqlSession session = SqlSessionUtils.buildSessionFactory().openSession(Boolean.TRUE)) {
                        TemplateMapper templateMapper = session.getMapper(TemplateMapper.class);
                        templateMapper.deleteById(templateModel.getId());
                    }
                    listReset();
                }
            });
            return tableCell;
        });

        checkCheckBox.selectedProperty().addListener((observableValue, v1, v2) -> {
            if (observableValue.getValue()) {
                checkCheckBoxListCache.stream().filter(checkBox -> !checkBox.isSelected()).forEach(checkBox -> checkBox.setSelected(Boolean.TRUE));
            } else {
                checkCheckBoxListCache.stream().filter(CheckBox::isSelected).forEach(checkBox -> checkBox.setSelected(Boolean.FALSE));
            }
        });

        queryButton.setGraphic(ImageViewUtil.getImageView(ImageUtil.getImageUrl(CodegenConstant.ICON_SEARCH), 16, 16));
        resetButton.setGraphic(ImageViewUtil.getImageView(ImageUtil.getImageUrl(CodegenConstant.ICON_RESET), 16, 16));
        refreshButton.setGraphic(ImageViewUtil.getImageView(ImageUtil.getImageUrl(CodegenConstant.ICON_REFRESH), 16, 16));
        batchDeleteButton.setGraphic(ImageViewUtil.getImageView(ImageUtil.getImageUrl(CodegenConstant.ICON_BATCH_DELETE), 16, 16));
    }

    protected void listReset() {
        templateTableView.getItems().clear();
        init();
    }

    protected void listReset(Template template, OperationEntity operationEntity) {
        templateTableView.getItems().clear();
        init(template, operationEntity);
    }

    public void refresh(ActionEvent actionEvent) {
        listReset();
        AlertUtil.info(ResourceBundleUtil.getProperty("Success"));
    }

    public void batchDelete(ActionEvent actionEvent) {
        if (AlertUtil.confirm(ResourceBundleUtil.getProperty("ConfirmDelete"))) {
            if (CollectionUtils.isEmpty(selectedIdList)) {
                AlertUtil.info(ResourceBundleUtil.getProperty("Success"));
                return;
            }
            try (SqlSession session = SqlSessionUtils.buildSessionFactory().openSession(Boolean.TRUE)) {
                TemplateMapper templateMapper = session.getMapper(TemplateMapper.class);
                templateMapper.deleteBatchIds(selectedIdList);
            }
            listReset();
            AlertUtil.info(ResourceBundleUtil.getProperty("Success"));
        }
    }

    public void query(ActionEvent actionEvent) {
        String name = this.nameQueryTextField.getText();
        if (StringUtils.isEmpty(name)) {
            return;
        }
        this.template = new Template();

        OperationEntity.Query query = new OperationEntity.Query();
        query.setFieldName("name");
        query.setFieldValue(name);
        query.setOperator(SqlKeyword.LIKE.getSqlSegment());
        this.operationEntity = new OperationEntity();
        this.operationEntity.setQueryList(List.of(query));
        listReset(template, operationEntity);
    }

    public void reset(ActionEvent actionEvent) {
        this.nameQueryTextField.clear();
        this.operationEntity.setQueryList(null);
        listReset();
    }

}
