package com.loongstudio.codegen.util;

import javafx.scene.image.ImageView;

/**
 * ImageViewUtil
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * 2022/9/6 21:57
 */
public final class ImageViewUtil {

    private ImageViewUtil() {
    }

    public static ImageView getImageView(String image, double height, double width) {
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        return imageView;
    }

    public static ImageView getImageView(String image, double height, double width, Object datasource) {
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        imageView.setUserData(datasource);
        return imageView;
    }

}
