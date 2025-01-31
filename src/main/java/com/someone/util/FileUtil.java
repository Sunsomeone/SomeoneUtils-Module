package com.someone.util;

import android.content.res.AssetManager;

import androidx.annotation.Nullable;

import com.someone.debug.LogReceiver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Someone
 * @Date 2024/09/08 13:10
 */
public class FileUtil {
    private static final File dataFilesDir  = GlobalUtilSetting.getContext().getFilesDir();
    private static final File dataCacheDir = GlobalUtilSetting.getContext().getCacheDir();
    private static final File externalFilesDir = GlobalUtilSetting.getContext().getExternalFilesDir(null);
    private static final File externalCacheDir = GlobalUtilSetting.getContext().getExternalCacheDir();
    private static final String dataFilesPath = GlobalUtilSetting.getContext().getFilesDir().getAbsolutePath();
    private static final String dataCachePath = GlobalUtilSetting.getContext().getCacheDir().getAbsolutePath();
    private static final String externalFilesPath = Objects.requireNonNull(GlobalUtilSetting.getContext().getExternalFilesDir(null)).getAbsolutePath();
    private static final String externalCachePath = Objects.requireNonNull(GlobalUtilSetting.getContext().getExternalCacheDir()).getAbsolutePath();

    public static File getDataFilesDir() {
        return dataFilesDir;
    }

    public static File getDataCacheDir() {
        return dataCacheDir;
    }

    public static File getExternalFilesDir() {
        return externalFilesDir;
    }

    public static File getExternalCacheDir() {
        return externalCacheDir;
    }

    public static String getDataFilesPath() {
        return dataFilesPath;
    }

    public static String getDataCachePath() {
        return dataCachePath;
    }

    public static String getExternalFilesPath() {
        return externalFilesPath;
    }

    public static String getExternalCachePath() {
        return externalCachePath;
    }

    public static byte[] readAssetsFile(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            AssetManager assetManager = GlobalUtilSetting.getContext().getAssets();
            try {
                assetManager = GlobalUtilSetting.getContext().getAssets();
                if (assetManager == null) {
                    LogReceiver.e("AssetManager 没有实例");
                    return new byte[0];
                }
            } catch (NullPointerException e) {
                LogReceiver.e("获取 AssetManager 为null");
                return new byte[0];
            }
            InputStream inputStream;
            try {
                inputStream = assetManager.open(filePath);
            } catch (IOException e) {
                try {
                    inputStream = new FileInputStream(filePath);
                } catch (IOException e1) {
                    LogReceiver.w("不能读取文件：" + filePath);
                    return new byte[0];
                }
            }
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                reader.close();
                inputStream.close();
            } catch (IOException e) {
                LogReceiver.e(e);
            }
            return builder.toString().getBytes();
        } else {
            LogReceiver.w("传入的文件路径为空");
            return new byte[0];
        }
    }

    public static File createFile(String path, String fileName) throws IOException {
        File file = new File(path, fileName);
        if (!file.exists()) {
            if (file.createNewFile()) {
                return file;
            } 
        }
        return null;
    }

    public static void copyDirectory(String originPath, String targetPath) {
        File originDir = new File(originPath);
        File targetDir = new File(targetPath);
        if (!originDir.exists()) {
            return;
        }
        if (!originDir.isDirectory()) {
            return;
        }
        if (!targetDir.exists()) {
            if (!targetDir.mkdirs()) {
                return;
            }
        }
        if (!targetDir.isDirectory()) {
            return;
        }
        File[] files = originDir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            File targetFile = new File(targetDir, file.getName());
            if (file.isDirectory()) {
                copyDirectory(file.getAbsolutePath(), targetFile.getAbsolutePath());
            } else {
                copyFile(file.getAbsolutePath(), targetFile.getAbsolutePath());
            }
        }
    }

    private static void copyFile(String originPath, String targetPath) {
        File originFile = new File(originPath);
        File targetFile = new File(targetPath);
        try {
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            try (FileChannel originChannel = new FileInputStream(originFile).getChannel();
            FileChannel targetChannel = new FileOutputStream(targetFile).getChannel()) {
                targetChannel.transferFrom(originChannel, 0, originChannel.size());
            }
        } catch (IOException e) {
            LogReceiver.e("创建文件失败; IO异常");
        }
    }

    public static void writeFile(File file, String content) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write(content);
        bw.close();
    }

    public static String readFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line;
        StringBuilder text = new StringBuilder();
        while ((line = br.readLine()) != null) {
            text.append(line);
            text.append('\n');
        }
        br.close();
        return text.toString();
    }

    public static File findFileByCurrentDirectory(String path, String matching, boolean isRegex) {
        if (!isRegex) {
            String filePath = (path.endsWith("/") ? path : path + "/") + matching;
            File file = new File(filePath);
            if (file.exists()) {
                LogReceiver.i("FileUtil", "找到文件; 路径: " + file.getAbsolutePath());
                return file;
            } else {
                LogReceiver.i("FileUtil", "未找到文件; 路径: " + file.getAbsolutePath());
            }
        } else {
            File directory = new File(path);
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    Pattern pattern = Pattern.compile(matching);
                    Matcher matcher = pattern.matcher(file.getName());
                    if (matcher.find()) {
                        LogReceiver.i("FileUtil", "找到文件; 路径: " + file.getAbsolutePath());
                        return file;
                    } else {
                        LogReceiver.i("FileUtil", "未找到文件; 路径: " + file.getAbsolutePath());
                    }
                }
            }
        }
        return null;
    }

    public static File[] findFilesByChildDirectory(String path, String matching, boolean isRegex) {
        File directory = new File(path);
        List<File> fileList = findFilesInDirectory(directory, matching, isRegex);
        if (!fileList.isEmpty()) {
            File[] files = new File[fileList.size()];
            for (int i = 0; i < fileList.size(); i++) {
                files[i] = fileList.get(i);
            }
            LogReceiver.i("FileUtil", "通过当前目录及子目录找到文件; 文件数量: " + fileList.size() + " 路径: " + path + " 文件名: " + matching);
            return files;
        } else {
            LogReceiver.i("FileUtil", "通过当前目录及子目录未找到文件; 路径: " + path + " 文件名: " + matching);
        }
        return null;
    }

    private static List<File> findFilesInDirectory(File directory, String matching, boolean isRegex) {
        List<File> foundFiles = new ArrayList<>();
        if (!isRegex) {
            if (directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            if (file.getName().equals(matching)) {
                                foundFiles.add(file);
                            }
                            foundFiles.addAll(findFilesInDirectory(file, matching, false));
                        } else if (file.getName().equals(matching)) {
                            foundFiles.add(file);
                        }
                    }
                }
            }
        } else {
            if (directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        Pattern pattern = Pattern.compile(matching);
                        Matcher matcher = pattern.matcher(file.getName());
                        if (file.isDirectory()) {
                            if (matcher.find()) {
                                foundFiles.add(file);
                            }
                            foundFiles.addAll(findFilesInDirectory(file, matching, true));
                        } else if (matcher.find()) {
                            foundFiles.add(file);
                        }
                    }
                }
            }
        }
        return foundFiles;
    }
}
