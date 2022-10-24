package com.loongstudio.codegen.controller;

import com.loongstudio.codegen.App;
import com.loongstudio.codegen.api.entity.Datasource;
import com.loongstudio.codegen.api.entity.Template;
import com.loongstudio.codegen.api.mapper.DatasourceMapper;
import com.loongstudio.codegen.util.AlertUtil;
import com.loongstudio.codegen.util.ResourceBundleUtil;
import com.loongstudio.codegen.util.SqlSessionUtils;
import com.loongstudio.core.toolkit.Sequence;
import com.loongstudio.core.util.IPUtil;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Datasource Controller
 *
 * @author KunLong-Luo
 * @since 2022/09/25 18:08
 */
@Slf4j
@Getter
@Setter
public class DatasourceController extends BaseController {

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

    public void init(Datasource datasource, TreeItem<String> treeItem) {
        if (Objects.isNull(datasource)) {
            return;
        }

        oldTreeItem = treeItem;
        initDatasource(datasource);

        titleText.setText(ResourceBundleUtil.getProperty("EditConnection"));
    }

    public void init(Datasource datasource, Integer type) {
        titleText.setText(ResourceBundleUtil.getProperty("SaveConnection"));
        if (Objects.isNull(datasource)) {
            typeTextField.setText(type.toString());
            return;
        }

        initDatasource(datasource);
    }

    private void initDatasource(Datasource datasource) {
        try {
            this.checkStringParam(List.of(datasource.getId(), datasource.getName(), datasource.getUsername(), datasource.getPassword()));
            this.checkIntegerParam(List.of(datasource.getType(), datasource.getIp(), datasource.getPort()));
        } catch (IllegalArgumentException e) {
            AlertUtil.warn(ResourceBundleUtil.getProperty("NullPointerException"));
            return;
        }

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
            this.checkStringParam(List.of(connectNameTextField.getText(), typeTextField.getText(), ipTextField.getText(), portTextField.getText(), usernameTextField.getText(), passwordField.getText()));

            Datasource datasource = new Datasource();
            datasource.setName(connectNameTextField.getText());
            datasource.setType(Integer.parseInt(typeTextField.getText()));
            datasource.setIp(IPUtil.toInteger(ipTextField.getText()));
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
            this.checkStringParam(List.of(connectNameTextField.getText(), typeTextField.getText(), ipTextField.getText(), portTextField.getText(), usernameTextField.getText(), passwordField.getText()));
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

        Datasource datasource = new Datasource();
        datasource.setName(connectName);
        datasource.setType(Integer.parseInt(type));
        datasource.setIp(IPUtil.toInteger(ip));
        datasource.setPort(Integer.parseInt(port));
        datasource.setUsername(username);
        datasource.setPassword(password);

        try {
            SqlSessionUtils.test(datasource);
        } catch (RuntimeException e) {
            AlertUtil.error(ResourceBundleUtil.getProperty("Failure"));
            return;
        }
        try (SqlSession session = SqlSessionUtils.buildSessionFactory().openSession(Boolean.TRUE)) {
            DatasourceMapper mapper = session.getMapper(DatasourceMapper.class);
            if (StringUtils.isEmpty(id)) {
                datasource.setId(String.valueOf(App.applicationContext.getBean(Sequence.class).nextId()));
                mapper.insert(datasource);
                AlertUtil.info(ResourceBundleUtil.getProperty("Success"));
            } else {
                datasource.setId(id);
                mapper.updateById(datasource);
                indexController.getDatabasesTreeView().getRoot().getChildren().remove(oldTreeItem);
                AlertUtil.info(ResourceBundleUtil.getProperty("Success"));
            }
        }
        TreeView<String> treeView = indexController.getDatabasesTreeView();
        addDatasourceItem(treeView.getRoot(), datasource, Boolean.FALSE);
        closeDialogStage();
        treeView.refresh();
    }

    private void checkStringParam(List<String> paramList) {
        paramList.forEach(param -> {
            if (StringUtils.isEmpty(param)) {
                throw new IllegalArgumentException();
            }
        });
    }

    private void checkIntegerParam(List<Integer> paramList) {
        paramList.forEach(param -> {
            if (Objects.isNull(param)) {
                throw new IllegalArgumentException();
            }
        });
    }

    public void cancel(ActionEvent actionEvent) {
        log.debug("===== cancel connection.=====");
        closeDialogStage();
    }

}
