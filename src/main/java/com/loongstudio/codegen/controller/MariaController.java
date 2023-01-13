package com.loongstudio.codegen.controller;

import com.loongstudio.codegen.api.entity.Datasource;
import com.loongstudio.codegen.api.entity.Template;
import com.loongstudio.codegen.constant.CodegenConstant;
import com.loongstudio.codegen.enums.DatasourceEnum;
import com.loongstudio.codegen.util.AlertUtil;
import com.loongstudio.codegen.util.CheckUtil;
import com.loongstudio.codegen.util.ResourceBundleUtil;
import com.loongstudio.codegen.util.SqlSessionUtils;
import com.loongstudio.core.util.IPUtil;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Maria Controller
 *
 * @author KunLong-Luo
 * @since 2022/09/25 18:08
 */
@Slf4j
@Getter
@Setter
public class MariaController extends BaseController {

    public TextField connectNameTextField;

    public TextField ipTextField;

    public TextField portTextField;

    public TextField usernameTextField;

    public PasswordField passwordField;

    public Text titleText;

    public Button testButton;

    public Button confirmButton;

    public Button cancelButton;

    public TextField idTextField;

    public TextField typeTextField;

    public TableView<Template> templateTableView;

    private IndexController indexController;

    public BorderPane rootBorderPane;

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
        ipTextField.setText("127.0.0.1");
        portTextField.setText("3306");
        usernameTextField.setText("root");
        passwordField.setText(null);
    }

    public void edit(Datasource datasource, TreeItem<String> treeItem) {
        if (Objects.isNull(datasource)) {
            AlertUtil.warn(ResourceBundleUtil.getProperty("NullPointerException"));
            return;
        }

        try {
            CheckUtil.checkStringParam(List.of(datasource.getId(), datasource.getName(), datasource.getUsername(), datasource.getPassword()));
            CheckUtil.checkIntegerParam(List.of(datasource.getType(), datasource.getIp(), datasource.getPort()));
        } catch (IllegalArgumentException e) {
            AlertUtil.warn(ResourceBundleUtil.getProperty("NullPointerException"));
            return;
        }

        oldTreeItem = treeItem;
        titleText.setText(ResourceBundleUtil.getProperty("EditConnection"));
        idTextField.setText(datasource.getId());
        typeTextField.setText(datasource.getType().toString());
        connectNameTextField.setText(datasource.getName());
        ipTextField.setText(IPUtil.toString(datasource.getIp()));
        portTextField.setText(datasource.getPort().toString());
        usernameTextField.setText(datasource.getUsername());
        passwordField.setText(datasource.getPassword());
    }

    public void test(ActionEvent actionEvent) {
        log.debug("===== test connection. =====");

        try {
            typeTextField.setText(Integer.toString(DatasourceEnum.MARIA_DB.ordinal()));
            CheckUtil.checkStringParam(List.of(connectNameTextField.getText(), typeTextField.getText(), ipTextField.getText(), portTextField.getText(), usernameTextField.getText(), passwordField.getText()));

            Datasource datasource = new Datasource();
            datasource.setName(connectNameTextField.getText());
            datasource.setType(Integer.parseInt(typeTextField.getText()));

            String ip = ipTextField.getText();
            if (StringUtils.equals(CodegenConstant.LOCALHOST, ip)) {
                ip = CodegenConstant.IP;
            }
            datasource.setIp(IPUtil.toInteger(ip));
            datasource.setPort(Integer.parseInt(portTextField.getText()));
            datasource.setUsername(usernameTextField.getText());
            datasource.setPassword(passwordField.getText());

            SqlSessionUtils.test(datasource);
            AlertUtil.info(ResourceBundleUtil.getProperty("Success"));
        } catch (RuntimeException e) {
            e.printStackTrace();
            AlertUtil.warn("Connection Failure.");
        }
    }

    public void confirm(ActionEvent actionEvent) {
        log.debug("===== confirm connection. =====");
        try {
            CheckUtil.checkStringParam(List.of(connectNameTextField.getText(), typeTextField.getText(), ipTextField.getText(), portTextField.getText(), usernameTextField.getText(), passwordField.getText()));
        } catch (IllegalArgumentException e) {
            AlertUtil.warn(ResourceBundleUtil.getProperty("NullPointerException"));
            return;
        }
        String id = idTextField.getText();
        String connectName = connectNameTextField.getText();
        String type = typeTextField.getText();
        String ip = ipTextField.getText();
        String port = portTextField.getText();
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        if (StringUtils.equals(CodegenConstant.LOCALHOST, ip)) {
            ip = CodegenConstant.IP;
        }

        Datasource datasource = new Datasource();
        datasource.setName(connectName);
        datasource.setType(Integer.parseInt(type));
        datasource.setIp(IPUtil.toInteger(ip));
        datasource.setPort(Integer.parseInt(port));
        datasource.setUsername(username);
        datasource.setPassword(password);


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

    public void cancel(ActionEvent actionEvent) {
        log.debug("===== cancel connection.=====");
        titleText.setText(null);
        typeTextField.setText(null);
        idTextField.setText(null);
        typeTextField.setText(null);
        connectNameTextField.setText(null);
        ipTextField.setText(null);
        portTextField.setText(null);
        usernameTextField.setText(null);
        passwordField.setText(null);
        closeDialogStage();
    }

}
