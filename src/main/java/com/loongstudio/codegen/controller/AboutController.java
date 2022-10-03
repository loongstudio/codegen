package com.loongstudio.codegen.controller;

import com.loongstudio.codegen.util.ClipboardUtil;
import com.loongstudio.core.constant.CommonConstant;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringJoiner;

@Slf4j
@Getter
@Setter
public class AboutController extends BaseController {

    public Button okButton;

    public Button copyButton;

    public Label versionLabel;

    public Label dateLabel;

    public Label copyrightLabel;

    public Label jdkVersionLabel;

    public Label jdkSystemLabel;

    private IndexController parentController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void ok(ActionEvent actionEvent) {
        closeDialogStage();
    }

    public void copy(ActionEvent actionEvent) {
        StringJoiner joiner = new StringJoiner(CommonConstant.CRLF);
        joiner.add(versionLabel.getText());
        joiner.add(dateLabel.getText());
        joiner.add(copyrightLabel.getText());
        joiner.add(jdkVersionLabel.getText());
        joiner.add(jdkSystemLabel.getText());

        log.debug("clipboard context: \n{}", joiner);
        ClipboardUtil.copy(joiner.toString());
    }
}
