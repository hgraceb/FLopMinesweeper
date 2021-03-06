package com.flop.minesweeper.filepicker.utils;

import org.apache.http.impl.cookie.DateUtils;

import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dimorinny on 24.10.15.
 */
public class FileUtils {
    public static List<File> getFileListByDirPath(String path, FileFilter filter) {
        File directory = new File(path);
        File[] files = directory.listFiles(filter);

        if (files == null) {
            return new ArrayList<>();
        }

        List<File> result = Arrays.asList(files);
        Collections.sort(result, new FileComparator());
        return result;
    }

    public static String cutLastSegmentOfPath(String path) {
        if (path.length() - path.replace("/", "").length() <= 1)
            return "/";
        String newPath = path.substring(0, path.lastIndexOf("/"));
        // We don't need to list the content of /storage/emulated
        if (newPath.equals("/storage/emulated"))
            newPath = "/storage";
        return newPath;
    }

    public static String getReadableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * 获取格式化后的文件/目录创建或最后修改时间
     */
    public static String getDateTime(String path) {
        return getDateTime(path, "yyyy-MM-dd HH:mm");
    }

    /**
     * 获取格式化后的文件/目录创建或最后修改时间
     */
    public static String getDateTime(String path, String format) {
        File file = new File(path);
        return getDateTime(file, format);
    }

    /**
     * 获取格式化后的文件/目录创建或最后修改时间
     */
    public static String getDateTime(File file, String format) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(file.lastModified());
        return DateUtils.formatDate(cal.getTime(), format);
    }
}
