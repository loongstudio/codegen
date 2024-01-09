package com.ronnaces.loong.codegen;

import com.ronnaces.loong.codegen.constant.CodegenConstant;
import com.ronnaces.loong.codegen.enums.FXMLPageEnum;
import com.ronnaces.loong.codegen.util.ImageUtil;
import com.ronnaces.loong.codegen.util.ResourceBundleUtil;
import com.ronnaces.loong.codegen.util.StageUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class CodegenApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        String resource = ResourceBundleUtil.getResource(FXMLPageEnum.INDEX.getFxml());
        String i18nResource = ResourceBundleUtil.getBasename();
        FXMLLoader loader = new FXMLLoader(CodegenApplication.class.getResource(resource), ResourceBundle.getBundle(i18nResource));
        Scene scene = new Scene(loader.load());
        StageUtil.init(stage, scene, "Codegen", ImageUtil.getImage(CodegenConstant.ICON_LOGO));
    }

}