package com.loongstudio.codegen;

import com.loongstudio.codegen.constant.CodegenConstant;
import com.loongstudio.codegen.enums.FXMLPageEnum;
import com.loongstudio.core.constant.CommonConstant;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class CodegenApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        String resource = StringUtils.joinWith(CommonConstant.SLASH, CodegenConstant.FXML_DIRECTORY, FXMLPageEnum.INDEX.getFxml());
        FXMLLoader fxmlLoader = new FXMLLoader(CodegenApplication.class.getResource(resource));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Codegen");
        stage.setScene(scene);
        stage.show();
    }
}