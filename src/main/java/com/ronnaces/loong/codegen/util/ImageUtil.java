package com.ronnaces.loong.codegen.util;

import com.ronnaces.loong.codegen.CodegenApplication;
import com.ronnaces.loong.codegen.constant.CodegenConstant;
import com.ronnaces.loong.core.constant.CommonConstant;
import javafx.scene.image.Image;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * ImageUtil
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * 2022/9/6 21:57
 */
public final class ImageUtil {

    private ImageUtil() {
    }

    public static Image getImage(String image) {
        return new Image(getImageUrl(image));
    }

    public static Image getImage(String image, String resourceDir) {
        return new Image(getImageUrl(image, resourceDir));
    }

    public static String getImageUrl(String image) {
        return Objects.requireNonNull(CodegenApplication.class.getResource(StringUtils.joinWith(CommonConstant.SLASH, CodegenConstant.STATIC_DIRECTORY, CodegenConstant.ICON_DIRECTORY, image))).toExternalForm();
    }

    public static String getImageUrl(String image, String resourceDir) {
        return Objects.requireNonNull(CodegenApplication.class.getResource(StringUtils.joinWith(CommonConstant.SLASH, resourceDir, image))).toExternalForm();
    }

}
