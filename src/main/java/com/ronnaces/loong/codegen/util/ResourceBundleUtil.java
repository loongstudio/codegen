package com.ronnaces.loong.codegen.util;

import com.ronnaces.loong.codegen.constant.CodegenConstant;
import com.ronnaces.loong.core.constant.CommonConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.ResourceBundle;

/**
 * Resource Bundle Util
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * @since 2022/10/24 9:53
 */
public final class ResourceBundleUtil {

    private ResourceBundleUtil() {
    }

    public static String getProperty(String key) {
        ResourceBundle bundle = ResourceBundle.getBundle(getBasename());
        return bundle.getString(key);
    }

    public static String getBasename() {
        return StringUtils.joinWith(CommonConstant.SLASH, CodegenConstant.CODEGEN_DIRECTORY, CodegenConstant.I18N_DIRECTORY, CodegenConstant.LANGUAGE);
    }

    public static String getBasename(String path, String directory) {
        return StringUtils.joinWith(CommonConstant.SLASH, path, directory, CodegenConstant.LANGUAGE);
    }

    public static String getResource(String page) {
        return StringUtils.joinWith(CommonConstant.SLASH, CodegenConstant.FXML_DIRECTORY, page);
    }

    public static String getResource(String directory, String page) {
        return StringUtils.joinWith(CommonConstant.SLASH, directory, page);
    }

}
