package com.ronnaces.loong.codegen.controller;

import com.ronnaces.loong.codegen.api.entity.Datasource;
import com.ronnaces.loong.codegen.api.entity.Template;
import com.ronnaces.loong.codegen.enums.DatasourceEnum;
import com.ronnaces.loong.codegen.util.AlertUtil;
import com.ronnaces.loong.codegen.util.CheckUtil;
import com.ronnaces.loong.codegen.util.ResourceBundleUtil;
import com.ronnaces.loong.codegen.util.SqlSessionUtils;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * SQLite Controller
 *
 * @author KunLong-Luo
 * @since 2022/09/25 18:08
 */
@Slf4j
@Getter
@Setter
public class SQLiteController extends BaseController implements DatasourceStrategy {

    public TextField connectNameTextField;

    public TextField usernameTextField;

    public PasswordField passwordField;

    public Text titleText;

    public Button testButton;

    public Button confirmButton;

    public Button cancelButton;

    public TextField idTextField;

    public TextField typeTextField;

    public TableView<Template> templateTableView;

    public TextField urlTextField;

    public BorderPane rootBorderPane;

    public Button choiceButton;

    private IndexController indexController;

    private TreeItem<String> oldTreeItem;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * Initialize the connection information
     *
     * @param datasourceType datasourceType
     */
    public void init(Integer datasourceType) {
        titleText.setText(ResourceBundleUtil.getProperty("SaveConnection"));
        typeTextField.setText(datasourceType.toString());
        idTextField.setText(null);
        connectNameTextField.setText(null);
        urlTextField.setText(null);
    }

    public void edit(Datasource datasource, TreeItem<String> treeItem) {
        if (Objects.isNull(datasource)) {
            AlertUtil.warn(ResourceBundleUtil.getProperty("NullPointerException"));
            return;
        }

        try {
            CheckUtil.checkStringParam(List.of(datasource.getId(), datasource.getName(), datasource.getUrl()));
            CheckUtil.checkIntegerParam(List.of(datasource.getType()));
        } catch (IllegalArgumentException e) {
            AlertUtil.warn(ResourceBundleUtil.getProperty("NullPointerException"));
            return;
        }

        oldTreeItem = treeItem;
        titleText.setText(ResourceBundleUtil.getProperty("EditConnection"));
        idTextField.setText(datasource.getId());
        typeTextField.setText(datasource.getType().toString());
        connectNameTextField.setText(datasource.getName());
        urlTextField.setText(datasource.getUrl());
    }

    public void test() {
        log.debug("===== test connection. =====");
        try {
            typeTextField.setText(Integer.toString(DatasourceEnum.SQLITE.ordinal()));
            CheckUtil.checkStringParam(List.of(connectNameTextField.getText(), typeTextField.getText()));

            Datasource datasource = new Datasource();
            datasource.setName(connectNameTextField.getText());
            datasource.setType(Integer.parseInt(typeTextField.getText()));
            datasource.setUrl(urlTextField.getText());

            SqlSessionUtils.test(datasource);
            AlertUtil.info(ResourceBundleUtil.getProperty("Success"));
        } catch (RuntimeException e) {
            e.printStackTrace();
            AlertUtil.warn("Connection Failure.");
        }
    }

    public void confirm() {
        log.debug("===== confirm connection. =====");
        try {
            CheckUtil.checkStringParam(List.of(connectNameTextField.getText(), typeTextField.getText(), urlTextField.getText()));
        } catch (IllegalArgumentException e) {
            AlertUtil.warn(ResourceBundleUtil.getProperty("NullPointerException"));
            return;
        }
        String id = idTextField.getText();
        String connectName = connectNameTextField.getText();
        String type = typeTextField.getText();
        String url = urlTextField.getText();

        Datasource datasource = new Datasource();
        datasource.setName(connectName);
        datasource.setType(Integer.parseInt(type));
        datasource.setUrl(url);

        if (DatasourceController.isConnectionFail(datasource)) {
            return;
        }

        if (StringUtils.isEmpty(id)) {
            indexController.getDatabasesTreeView().getRoot().getChildren().remove(oldTreeItem);
        }
        DatasourceController.saveDatasource(id, datasource);
        AlertUtil.info(ResourceBundleUtil.getProperty("Success"));
        TreeView<String> treeView = indexController.getDatabasesTreeView();
        addDatasourceItem(treeView.getRoot(), datasource, Boolean.FALSE);
        closeDialogStage();
        treeView.refresh();
    }

    public void cancel() {
        log.debug("===== cancel connection.=====");
        titleText.setText(null);
        typeTextField.setText(null);
        idTextField.setText(null);
        typeTextField.setText(null);
        connectNameTextField.setText(null);
        urlTextField.setText(null);
        closeDialogStage();
    }

    public void choiceFolder() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(ResourceBundleUtil.getProperty("ChoiceFolder"));
        chooser.setInitialDirectory(new File(System.getProperty("java.io.tmpdir")));
        File file = chooser.showOpenDialog(getPrimaryStage());
        if (Objects.isNull(file)) {
            return;
        }
        this.urlTextField.setText(file.getPath());
        log.debug("folder path: {}", file.getPath());
    }

}
