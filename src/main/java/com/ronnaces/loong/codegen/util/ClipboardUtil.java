package com.ronnaces.loong.codegen.util;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * ClipboardUtil
 *
 * @author KunLong-Luo
 * @version 1.0.0
 * 2022/9/18 9:11
 */
public final class ClipboardUtil {

    private ClipboardUtil() {
    }

    /**
     * clip
     *
     * @return {@link String}
     */
    public static String clip() {
        return Clipboard.getSystemClipboard().getString();
    }

    /**
     * copy
     *
     * @param text text
     */
    public static void copy(String text) {
        Clipboard cb = Clipboard.getSystemClipboard();
        ClipboardContent ct = new ClipboardContent();
        ct.putString(text);
        cb.setContent(ct);
    }
}
