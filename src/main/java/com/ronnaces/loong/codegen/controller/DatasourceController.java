package com.ronnaces.loong.codegen.controller;

import com.ronnaces.loong.codegen.App;
import com.ronnaces.loong.codegen.api.entity.Datasource;
import com.ronnaces.loong.codegen.api.entity.Template;
import com.ronnaces.loong.codegen.api.mapper.DatasourceMapper;
import com.ronnaces.loong.codegen.constant.CodegenConstant;
import com.ronnaces.loong.codegen.enums.DatasourceEnum;
import com.ronnaces.loong.codegen.util.AlertUtil;
import com.ronnaces.loong.codegen.util.CheckUtil;
import com.ronnaces.loong.codegen.util.ResourceBundleUtil;
import com.ronnaces.loong.codegen.util.SqlSessionUtils;
import com.ronnaces.loong.core.time.DatePattern;
import com.ronnaces.loong.core.time.LocalDateTimeUtil;
import com.ronnaces.loong.core.toolkit.Sequence;
import com.ronnaces.loong.core.network.IPUtil;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;

import java.net.URL;
import java.time.LocalDateTime;
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
public class DatasourceController extends BaseController implements DatasourceStrategy{

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
        typeTextField.setText(null);
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

    public void test() {
        log.debug("===== test connection. =====");

        try {
            typeTextField.setText(Integer.toString(DatasourceEnum.MYSQL.ordinal()));
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

    public void confirm() {
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

        try {
            SqlSessionUtils.test(datasource);
        } catch (RuntimeException e) {
            AlertUtil.error(ResourceBundleUtil.getProperty("Failure"));
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

    public static boolean isConnectionFail(Datasource datasource) {
        try {
            SqlSessionUtils.test(datasource);
        } catch (RuntimeException e) {
            AlertUtil.error(ResourceBundleUtil.getProperty("Failure"));
            return true;
        }
        return false;
    }

    public static void saveDatasource(String id, Datasource datasource) {
        try (SqlSession session = SqlSessionUtils.buildSessionFactory().openSession(Boolean.TRUE)) {
            DatasourceMapper mapper = session.getMapper(DatasourceMapper.class);
            String dataTime = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.NORM_DATETIME_FORMATTER);
            if (StringUtils.isEmpty(id)) {
                datasource.setId(String.valueOf(App.applicationContext.getBean(Sequence.class).nextId()));
                datasource.setCreateTime(dataTime);
                mapper.insert(datasource);
            } else {
                datasource.setId(id);
                datasource.setUpdateTime(dataTime);
                mapper.updateById(datasource);
            }
        }
    }

    public void cancel() {
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
