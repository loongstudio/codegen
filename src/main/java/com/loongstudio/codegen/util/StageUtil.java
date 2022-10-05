package com.loongstudio.codegen.util;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public final class StageUtil {

    private StageUtil() {
    }

    public static void init(Stage stage, Scene scene, String title, Image image) {
        stage.setScene(scene);
        stage.setTitle(title);
        stage.getIcons().add(image);
        stage.show();
    }

}
