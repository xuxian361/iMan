package com.sundy.iman.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;

import com.sundy.iman.MainApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * 文件工具类
 * Created by sundy on 17/9/26.
 */

public class FileUtils {

    private static final String FileName = "iMan-User";

    /**
     * 删除某文件夹下所有文件
     *
     * @param root
     */
    public static void deleteAllFiles(File root) {
        try {
            File files[] = root.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) { // 判断是否为文件夹
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (f.exists()) { // 判断是否存在
                            deleteAllFiles(f);
                            try {
                                f.delete();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除图片及URI
     *
     * @param context
     * @param imageFile
     * @return
     */
    public static boolean delImage(Context context, File imageFile) {
        ContentResolver mContentResolver = context.getContentResolver();
        mContentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{imageFile.getAbsolutePath()});
        return imageFile.delete();
    }

    /**
     * 获取附件单位大小
     *
     * @param fileS
     * @return 文件大小
     */
    public static String formatFileSize(long fileS) {
        // 转换文件大小
        DecimalFormat df = new DecimalFormat("0.00");
        String fileSizeString = "";
        if (fileS == 0) {
            fileSizeString = "0k";
        } else if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 是否存在SD卡
     *
     * @return
     */
    public static boolean existSD() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    /**
     * 获取sd根部目录
     *
     * @return app在sd卡上的根目录
     */
    public static String getRootDir() {
        String dir = "";
        if (existSD()) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + FileName + File.separator + "iman");
            if (!file.exists()) {
                file.mkdirs();
            }
            dir = file.getAbsolutePath();
        } else {
            File file = new File(Environment.getDataDirectory() + File.separator + FileName + File.separator + "iman");
            if (!file.exists()) {
                file.mkdirs();
            }
            dir = file.getAbsolutePath();
        }
        return dir;
    }

    /**
     * 获取图片缓存文件夹地址
     *
     * @return app在sd上的图片缓存文件夹地址
     */
    public static String getImageCache() {
        String rootDir = getRootDir();
        File file = new File(rootDir, "image");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsoluteFile().toString();
    }

    /**
     * 得到头像缓存文件夹地址
     *
     * @return app在sd上的头像缓存文件夹地址
     */

    public static String getPortraitCache() {
        String rootDir = getRootDir();
        File file = new File(rootDir, "portrait");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsoluteFile().toString();
    }

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @return double值的大小
     */
    public static String getFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatFileSize(blockSize);
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatFileSize(blockSize);
    }

    /**
     * 获取指定文件大小
     *
     * @param
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 将assets中的文件写入手机中
     */
    public static void copyFromAssets(String FileName) {
        try {
            File file = new File(getRootDir(), FileName);
            if (file.exists()) {
                file.delete();
            }
            InputStream is = MainApp.getInstance().getAssets().open(FileName);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //读文件
    public static String readSDFile(String fileName) throws IOException {
        String res = "";
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        int length = fis.available();
        byte[] buffer = new byte[length];
        fis.read(buffer);
        res = new String(buffer, "UTF-8");
        fis.close();
        return res;
    }

    //写文件
    public static void writeSDFile(String fileName, String write_str) throws IOException {
        File file = new File(fileName);
        FileOutputStream fos = new FileOutputStream(file);
        byte[] bytes = write_str.getBytes();
        fos.write(bytes);
        fos.close();
    }

    //获取文件后缀名
    public static String getFileExtension(String filePath) {
        int lastDot = filePath.lastIndexOf(".");
        if (lastDot < 0)
            return null;
        return filePath.substring(lastDot + 1);
    }

    //清理文件
    public static void clearFileCache(String fileDir) {
        try {
            //清理图片压缩文件夹
            File file = new File(fileDir);
            if (file.exists()) {
                FileUtils.deleteAllFiles(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //保存Bitmap 到缓存文件夹
    public static String saveBitmapToSD(Bitmap bitmap) {
        String path = "";
        try {
            File file = new File(getImageCache(), System.currentTimeMillis() + ".jpg");
            path = file.getPath();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }


}
