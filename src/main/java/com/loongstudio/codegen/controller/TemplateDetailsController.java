package com.loongstudio.codegen.controller;

import com.loongstudio.codegen.api.entity.Template;
import com.loongstudio.codegen.api.mapper.TemplateMapper;
import com.loongstudio.codegen.enums.OperationEnum;
import com.loongstudio.codegen.model.TemplateModel;
import com.loongstudio.codegen.util.ResourceBundleUtil;
import com.loongstudio.codegen.util.SqlSessionUtils;
import com.loongstudio.core.constant.CommonConstant;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.StringJoiner;

/**
 * Template Details Controller
 *
 * @author KunLong-Luo
 * @since 2022/09/25 18:08
 */
@Slf4j
@Getter
@Setter
public class TemplateDetailsController extends BaseController {

    public Button cancelButton;

    public Button confirmButton;

    public TemplateModel currentTemplateModel;

    public TextField idTextField;

    public TextField nameTextField;

    public TextField folderTextField;

    public TextField parentPackageTextField;

    public TextField moduleTextField;

    public TextField datasourceNameTextField;

    public TextField databaseNameTextField;

    public TextField tableNameTextField;

    public TextField descriptionTextField;

    public TextField createdAtTextField;

    public TextField updatedAtTextField;

    public Text titleText;

    public Button choiceButton;

    private TemplateController templateController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void init(OperationEnum operationEnum) {
        if (Objects.isNull(currentTemplateModel)) {
            idTextField.setText(null);
            nameTextField.setText(null);
            folderTextField.setText(null);
            parentPackageTextField.setText(null);
            moduleTextField.setText(null);
            createdAtTextField.setText(null);
            updatedAtTextField.setText(null);
            descriptionTextField.setText(null);
        }

        switch (operationEnum) {
            case SAVE -> {
                idTextField.setText(null);
                nameTextField.setText(null);
                folderTextField.setText(null);
                parentPackageTextField.setText(null);
                moduleTextField.setText(null);
                datasourceNameTextField.setText(null);
                databaseNameTextField.setText(null);
                tableNameTextField.setText(null);
                createdAtTextField.setText(null);
                updatedAtTextField.setText(null);
                descriptionTextField.setText(null);
            }
            case DELETE -> {
            }
            case DETAIL -> {
                confirmButton.setVisible(Boolean.FALSE);
                idTextField.setEditable(Boolean.FALSE);
                nameTextField.setEditable(Boolean.FALSE);
                folderTextField.setEditable(Boolean.FALSE);
                parentPackageTextField.setEditable(Boolean.FALSE);
                moduleTextField.setEditable(Boolean.FALSE);
                datasourceNameTextField.setEditable(Boolean.FALSE);
                databaseNameTextField.setEditable(Boolean.FALSE);
                tableNameTextField.setEditable(Boolean.FALSE);
                createdAtTextField.setEditable(Boolean.FALSE);
                updatedAtTextField.setEditable(Boolean.FALSE);
                descriptionTextField.setEditable(Boolean.FALSE);
                choiceButton.setDisable(Boolean.TRUE);

                idTextField.setText(currentTemplateModel.getId());
                nameTextField.setText(currentTemplateModel.getName());
                folderTextField.setText(currentTemplateModel.getFolder());
                parentPackageTextField.setText(currentTemplateModel.getParentPackage());
                moduleTextField.setText(currentTemplateModel.getModule());
                String[] strings = StringUtils.split(currentTemplateModel.getName(), CommonConstant.PERIOD);
                if (strings.length == 1) {
                    datasourceNameTextField.setText(strings[0]);
                } else if (strings.length == 2) {
                    datasourceNameTextField.setText(strings[0]);
                    databaseNameTextField.setText(strings[1]);
                } else if (strings.length == 3) {
                    datasourceNameTextField.setText(strings[0]);
                    databaseNameTextField.setText(strings[1]);
                    tableNameTextField.setText(strings[2]);
                }
                createdAtTextField.setText(currentTemplateModel.getCreatedAt());
                updatedAtTextField.setText(currentTemplateModel.getUpdatedAt());
                descriptionTextField.setText(currentTemplateModel.getDescription());
            }
            case EDIT -> {
                parentPackageTextField.setEditable(Boolean.TRUE);
                moduleTextField.setEditable(Boolean.TRUE);
                tableNameTextField.setEditable(Boolean.TRUE);
                descriptionTextField.setEditable(Boolean.TRUE);

                idTextField.setEditable(Boolean.FALSE);
                nameTextField.setEditable(Boolean.FALSE);
                datasourceNameTextField.setEditable(Boolean.FALSE);
                databaseNameTextField.setEditable(Boolean.FALSE);
                createdAtTextField.setEditable(Boolean.FALSE);
                updatedAtTextField.setEditable(Boolean.FALSE);

                idTextField.setDisable(Boolean.TRUE);
                nameTextField.setDisable(Boolean.TRUE);
                datasourceNameTextField.setDisable(Boolean.TRUE);
                databaseNameTextField.setDisable(Boolean.TRUE);
                createdAtTextField.setDisable(Boolean.TRUE);
                updatedAtTextField.setDisable(Boolean.TRUE);

                choiceButton.setDisable(Boolean.FALSE);
                parentPackageTextField.setDisable(Boolean.FALSE);
                moduleTextField.setDisable(Boolean.FALSE);
                tableNameTextField.setDisable(Boolean.FALSE);
                descriptionTextField.setDisable(Boolean.FALSE);

                idTextField.setText(currentTemplateModel.getId());
                nameTextField.setText(currentTemplateModel.getName());
                folderTextField.setText(currentTemplateModel.getFolder());
                parentPackageTextField.setText(currentTemplateModel.getParentPackage());
                moduleTextField.setText(currentTemplateModel.getModule());
                String[] strings = StringUtils.split(currentTemplateModel.getName(), CommonConstant.PERIOD);
                if (strings.length == 1) {
                    datasourceNameTextField.setText(strings[0]);
                } else if (strings.length == 2) {
                    datasourceNameTextField.setText(strings[0]);
                    databaseNameTextField.setText(strings[1]);
                } else if (strings.length == 3) {
                    datasourceNameTextField.setText(strings[0]);
                    databaseNameTextField.setText(strings[1]);
                    tableNameTextField.setText(strings[2]);
                }
                createdAtTextField.setText(currentTemplateModel.getCreatedAt());
                updatedAtTextField.setText(currentTemplateModel.getUpdatedAt());
                descriptionTextField.setText(currentTemplateModel.getDescription());
            }
        }
    }

    public void cancel(ActionEvent actionEvent) {
        currentTemplateModel = null;
        templateController = null;
        closeDialogStage();
    }

    public void confirm(ActionEvent actionEvent) {
        Template template = new Template();

        String id = idTextField.getText();
        if (StringUtils.isNotEmpty(id)) {
            try (SqlSession session = SqlSessionUtils.buildSessionFactory().openSession(Boolean.TRUE)) {
                TemplateMapper mapper = session.getMapper(TemplateMapper.class);
                Template old = mapper.selectById(id);
                if (Objects.nonNull(old)) {
                    template.setId(id);
                    template.setFolder(folderTextField.getText());
                    template.setParentPackage(parentPackageTextField.getText());
                    template.setModule(moduleTextField.getText());
                    template.setTableName(tableNameTextField.getText());
                    template.setDescription(descriptionTextField.getText());
                    template.setName(new StringJoiner(CommonConstant.PERIOD).add(old.getDatasourceName()).add(old.getDatabaseName()).add(tableNameTextField.getText()).toString());
                    mapper.updateById(template);
                }
            }
            closeDialogStage();
            this.templateController.listReset();
        }
    }

    public void choiceFolder(MouseEvent mouseEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(ResourceBundleUtil.getProperty("ChoiceFolder"));
        chooser.setInitialDirectory(new File(System.getProperty("java.io.tmpdir")));
        File file = chooser.showDialog(getPrimaryStage());
        if (Objects.isNull(file)) {
            return;
        }
        folderTextField.setText(file.getPath());
        log.debug("folder path: {}", file.getPath());
    }

}
