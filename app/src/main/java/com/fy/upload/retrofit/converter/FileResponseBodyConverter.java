package com.fy.upload.retrofit.converter;

import android.annotation.SuppressLint;

import com.fy.upload.retrofit.LoadOnSubscribe;
import com.fy.upload.utils.ConfigUtils;
import com.fy.upload.utils.FileUtils;
import com.fy.upload.utils.L;
import com.fy.upload.utils.SpfAgent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.ResponseBody;

/**
 * describe: 文件下载
 * Created by fangs on 2019/8/28 22:03.
 */
public class FileResponseBodyConverter {

    /**
     * 根据ResponseBody 写文件
     * @param responseBody
     * @param url
     * @param filePath   文件保存路径
     * @return
     */
    public static File saveFile(LoadOnSubscribe loadOnSubscribe, final ResponseBody responseBody, String url, final String filePath) {
        final File tempFile = FileUtils.createTempFile(url, filePath);

        File file = null;
        try {
            file = writeFileToDisk(loadOnSubscribe, responseBody, tempFile.getAbsolutePath());

            int FileDownStatus = SpfAgent.init("").getInt(file.getName() + ConfigUtils.FileDownStatus);
            if (FileDownStatus == 4) {
                boolean renameSuccess = FileUtils.reNameFile(url, tempFile.getPath());
                return FileUtils.getFile(url, filePath);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    /**
     * 单线程 断点下载
     * @param loadOnSubscribe
     * @param responseBody
     * @param filePath
     * @return
     * @throws IOException
     */
    @SuppressLint("DefaultLocale")
    public static File writeFileToDisk(LoadOnSubscribe loadOnSubscribe, ResponseBody responseBody, String filePath) throws IOException {
        long totalByte = responseBody.contentLength();

        L.e("fy_file_FileDownInterceptor", "文件下载 写数据" + "---" + Thread.currentThread().getName());

        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        } else {
            if (null != loadOnSubscribe){
                loadOnSubscribe.setmSumLength(file.length() + totalByte);
                loadOnSubscribe.onRead(file.length());
            }
        }


        SpfAgent.init("").saveInt(file.getName() + ConfigUtils.FileDownStatus, 1).commit(false);//正在下载

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
        long tempFileLen = file.length();
        randomAccessFile.seek(tempFileLen);

        byte[] buffer = new byte[1024 * 4];
        InputStream is = responseBody.byteStream();

        long downloadByte = 0;
        while (true) {
            int len = is.read(buffer);
            if (len == -1) {//下载完成
                if (null != loadOnSubscribe) loadOnSubscribe.clean();

                SpfAgent.init("").saveInt(file.getName() + ConfigUtils.FileDownStatus, 4).commit(false);//下载完成
                break;
            }

            int FileDownStatus = SpfAgent.init("").getInt(file.getName() + ConfigUtils.FileDownStatus);
            if (FileDownStatus == 2 || FileDownStatus == 3) break;//暂停或者取消 停止下载

            randomAccessFile.write(buffer, 0, len);
            downloadByte += len;

            if (null != loadOnSubscribe) loadOnSubscribe.onRead(len);
        }

        is.close();
        randomAccessFile.close();

        return file;
    }

}
