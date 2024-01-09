package com.ronnaces.loong.codegen.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public final class AlertUtil {

    private AlertUtil() {
    }

    public static void info(String message) {
        alert(Alert.AlertType.INFORMATION, message);
    }

    public static void wait(String message) {
        showAndWait(Alert.AlertType.INFORMATION, message);
    }

    public static void warn(String message) {
        alert(Alert.AlertType.WARNING, message);
    }

    public static void error(String message) {
        alert(Alert.AlertType.ERROR, message);
    }

    public static boolean confirm(String message) {
        return confirm(Alert.AlertType.CONFIRMATION, message);
    }

    private static Boolean confirm(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        return alert.showAndWait().map(buttonType -> ButtonType.OK == buttonType).orElse(Boolean.FALSE);
    }

    private static void showAndWait(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void alert(Alert.AlertType warning, String message) {
        Alert alert = new Alert(warning);
        alert.setContentText(message);
        alert.show();
    }
}
