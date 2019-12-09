package com.fy.upload.utils;

import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.io.FileWriter;

/**
 * 文件工具类
 * Created by fangs on 2017/3/22.
 */
public class FileUtils {

    public static String cache = "fy.cache";
    /** 文件下载目录 */
    public static String DOWN = "fy.down";
    /** 图片保存目录 */
    public static String IMG = "fy.picture";
    /** 压缩文件目录 */
    public static String ZIP = "fy.zip";

    private FileUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断SDCard是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取 内置SD卡路径
     *
     * @return "/mnt/sdcard
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 获取 内置SD卡 指定目录类型的 路径
     * @param directoryTpye 目录类型 如：Environment.DIRECTORY_DCIM --》 /storage/sdcard0/DCIM
     * @return
     */
    public static String getSDCardDirectoryTpye(String directoryTpye){
        return Environment.getExternalStoragePublicDirectory(directoryTpye)
                .getAbsolutePath() + File.separator;
    }


    /**
     * 获取应用 在SD卡保存数据的私有路径
     * @return "SDCard/Android/data/你的应用的包名/files/ "
     */
    public static String getExternalFiles(){
        return ConfigUtils.getAppCtx().getExternalFilesDir(null)
                .getAbsolutePath() + File.separator;
    }

    /**
     * 获取应用 在SD卡缓存数据的私有路径
     * @return "SDCard/Android/data/你的应用包名/cache/"
     */
    public static String getExternalCacheDir(){
        return ConfigUtils.getAppCtx().getExternalCacheDir()
                .getAbsolutePath() + File.separator;
    }

    /**
     * 获取系统存储路径
     * @return "/system/"
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory()
                .getAbsolutePath() + File.separator;
    }

    /**
     * 获取应用 内部存储 路径
     * @return "/data/"
     */
    public static String getDataDirectoryPath(){
        return Environment.getDataDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 获取应用 内部缓存 路径
     * @return "/cache/"
     */
    public static String getCacheDirectoryPath(){
        return Environment.getDownloadCacheDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 获取应用 内部具体数据存储目录
     * @return "/data/data/你的应用包名/files/"
     */
    public static String getFilesDir(){
        return ConfigUtils.getAppCtx().getFilesDir()
                .getAbsolutePath() + File.separator;
    }

    /**
     * 获取应用 内部具体缓存目录
     * @return "/data/data/你的应用包名/cache/"
     */
    public static String getCacheDir() {
        return ConfigUtils.getAppCtx().getCacheDir()
                .getAbsolutePath() + File.separator;
    }


    /**
     * 到得文件的放置路径
     *
     * @param aModuleName 模块名字 (如："head.img.temp")
     * @param type        类型 0：应用私有存储路径；1：应用私有缓存路径 （是否存在SD卡，存在则表示 应用SD卡，否则表示应用内部存储）
     * @return
     */
    public static String getPath(String aModuleName, int type) {
        String modulePath = aModuleName.replace(".", File.separator);
        String fDirStr = File.separator + modulePath + File.separator;

        File dirpath;
        if (isSDCardEnable())
            switch (type) {
                case 0:
                    dirpath = new File(getExternalFiles(), fDirStr);
                    break;
                case 1:
                    dirpath = new File(getExternalCacheDir(), fDirStr);
                    break;
                default:
                    dirpath = new File(getSDCardPath(), fDirStr);//需要存储权限
                    break;
            }
        else
            switch (type) {
                case 0:
                    dirpath = new File(getFilesDir(), fDirStr);
                    break;
                case 1:
                    dirpath = new File(getCacheDir(), fDirStr);
                    break;
                default:
                    dirpath = new File(getDataDirectoryPath(), fDirStr);
                    break;
            }

        return dirpath.getPath();
    }

    /**
     * 判断指定路径的 文件夹 是否存在，不存在创建文件夹 (内部执行了 getPath())
     *
     * @param filePath
     * @return
     */
    public static File folderIsExists(String filePath, int type) {
        File folder = new File(getPath(filePath, type));
        try {
            if (!folder.isDirectory()) {
                folder.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return folder;
    }

    /**
     * 判断指定路径的 文件 是否存在，不存在创建文件
     *
     * @param filePath
     * @return
     */
    public static File fileIsExists(String filePath) {
        File file = new File(filePath);
        try {
            if (!file.isFile()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    /**
     * 判断文件是否存在
     */
    public static boolean fileIsExist(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public static void recursionDeleteFile(File file) {

        if (!file.exists()) return;

        if (file.isFile()) {
            deleteFileSafely(file);//删除当前 文件
            return;
        }

        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (null == childFile || childFile.length == 0) {
                deleteFileSafely(file);//删除 当前目录
                return;
            }

            for (File f : childFile) {
                recursionDeleteFile(f);
            }

            deleteFileSafely(file);//删除 当前目录
        }
    }

    /**
     * 安全删除文件
     *
     * @param file
     * @return
     */
    public static boolean deleteFileSafely(File file) {
        if (null != file) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }

    /**
     * 根据系统时间、前缀、后缀产生一个文件
     *
     * @param folderPath 目标文件所在的 文件夹（目录）
     * @param prefix 目标文件的 前缀 (如：IMG_)
     * @param suffix 目标文件的 后缀名（如：.jpg）
     * @return
     */
    public static File createFile(String folderPath, String prefix, String suffix, int type) {
        File folder = folderIsExists(folderPath, type);

        String name = TimeUtils.Long2DataString(System.currentTimeMillis(), "yyyyMMdd_HHmmss");
        String filename = prefix + name + suffix;

        return fileIsExists(new File(folder, filename).getPath());
    }

    /**
     * 生成临时文件
     * @param url
     * @param filePath
     * @return
     */
    public static File createTempFile(String url, String filePath) {
        String md5 = EncryptUtils.getMD5(url) + ".temp";
        return FileUtils.fileIsExists(filePath + "/" + md5);
    }

    /**
     * 获取临时文件 名称
     * @param url
     * @param filePath
     * @return
     */
    public static File getTempFile(String url, String filePath) {
        String md5 = EncryptUtils.getMD5(url) + ".temp";
        return new File(filePath + "/" + md5);
    }

    /**
     * 根据 url 重命名 指定的 文件（路径）
     * @param url
     * @param oldPath 路径
     */
    public static boolean reNameFile(String url, String oldPath){
        String fileName  = getFileName(url);

        File oldFile = new File(oldPath);

        return oldFile.renameTo(new File(oldFile.getParent(), fileName));
    }

    /**
     * 根据 url 和 指定的路径 在本地生成一个文件
     * @param url 下载 url【如：http://img5q.duitang.com/uploads/item/201505/01/20150501113308_QNmsf.jpeg】
     * @return
     */
    public static File createFile(String url, String path) {
        File file = getFile(url, path);
        return FileUtils.fileIsExists(file.getPath());
    }

    /**
     * 根据 url 和 指定的路径，获取文件
     * @param url
     * @param path
     * @return
     */
    public static File getFile(String url, String path) {
        String fileName = getFileName(url);
        return new File(path, fileName);
    }

    /**
     * 根据 url 生成 文件名
     * @param url
     */
    private static String getFileName(String url){
        String fileName;

        String md5Name = EncryptUtils.getMD5(url);
        if (url.indexOf("?") == -1){
            fileName = md5Name + url.substring(url.lastIndexOf("."));
        } else {
            String temp = url.substring(0, url.indexOf("?"));
            if (temp.indexOf(".") == -1){
                fileName = md5Name + ".temp";
            } else {
                fileName = md5Name + temp.substring(temp.lastIndexOf("."));
            }
        }

        return fileName;
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    public static String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            L.d("file", "paramString---->null");
            return str;
        }
        L.d("file", "paramString:" + paramString);
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            L.d("file", "i <= -1");
            return str;
        }


        str = paramString.substring(i + 1);
        L.d("file", "paramString.substring(i + 1)------>" + str);
        return str;
    }

    /**
     * 向指定文件写内容  (追加形式写文件)
     *
     * @param path          文件目录(如：fy.com.base)
     * @param inputfileName 文件名（如：log.txt）
     * @param content       准备写入的内容
     */
    public static void fileToInputContent(String path, String inputfileName, String content) {
        StringBuffer sb = new StringBuffer();
        sb.append("\n").append(content).append("\n");

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File dir = new File(getPath(path, 0));
            if (!dir.exists()) {
                dir.mkdir();
            }

            try {
                // 文件目录 + 文件名 String
                String fileName = dir.toString() + File.separator + inputfileName;
                // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
                FileWriter writer = new FileWriter(fileName, true);
                writer.write(sb.toString());
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }






    /**
     * 获取SD卡的剩余容量 单位byte
     *
     * @return
     */
    public static long getSDCardAllSize() {
        if (isSDCardEnable()) {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = stat.getAvailableBlocks();
            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public static long getFreeBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }
}
