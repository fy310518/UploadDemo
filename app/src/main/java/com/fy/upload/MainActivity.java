package com.fy.upload;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.widget.TextView;

import com.fy.upload.retrofit.LoadOnSubscribe;
import com.fy.upload.retrofit.down.DownLoadListener;
import com.fy.upload.retrofit.up.LoadCallBack;
import com.fy.upload.retrofit.LoadService;
import com.fy.upload.retrofit.RequestUtils;
import com.fy.upload.utils.ConfigUtils;
import com.fy.upload.utils.L;
import com.fy.upload.utils.TransfmtUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * 主界面
 * Created by fangs on 2018/11/12.
 */
public class MainActivity extends AppCompatActivity {

    Unbinder unbinder;
    @BindView(R.id.tvKing)
    TextView tvKing;
    @BindView(R.id.tvKing2)
    TextView tvKing2;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        ConfigUtils.ctx = getApplication();

//        List<String> files = new ArrayList<>();
//        files.add(TransfmtUtils.getSDCardPath() + "DCIM/Camera/679f6337gy1fr69ynfq3nj20hs0qodh0.jpg");
//        files.add(TransfmtUtils.getSDCardPath() + "DCIM/Camera/IMG_20181108_144507.jpg");
//        files.add(TransfmtUtils.getSDCardPath() + "DCIM/Camera/RED,胡歌 - 逍遥叹（Cover 胡歌）.mp3");
//        files.add(TransfmtUtils.getSDCardPath() + "DCIM/Camera/马郁 - 下辈子如果我还记得你.mp3");
//
//        uploadFiles(files, tvKing);
//
//        List<String> files1 = new ArrayList<>();
//        files1.add(TransfmtUtils.getSDCardPath() + "DCIM/Camera/IMG_20181108_143502.jpg");
//        files1.add(TransfmtUtils.getSDCardPath() + "DCIM/Camera/序人Hm - 再见（cover：张震岳）.mp3");
//        uploadFiles(files1, tvKing2);

        downLoadFile();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void uploadFiles(List<String> files, TextView textView) {
        LoadOnSubscribe loadOnSubscribe = new LoadOnSubscribe();
        ArrayMap<String, Object> params = new ArrayMap<>();
        params.put("filePathList", files);
        params.put("LoadOnSubscribe", loadOnSubscribe);

        Observable.merge(Observable.create(loadOnSubscribe), RequestUtils.create(LoadService.class).uploadFile(params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LoadCallBack<Object>() {
                    @Override
                    protected void onProgress(String percent) {
                        textView.setText(percent + "%");
                    }

                    @Override
                    protected void onSuccess(Object t) {

                    }
                });
    }

    /**
     * 下载文件
     */
    public void downLoadFile() {
        String url = "http://acj3.pc6.com/pc6_soure/2018-11/com.tencent.mobileqqi_6600.apk";
        RequestUtils.downLoadFile(url, new DownLoadListener<File>(){
            @Override
            public void onSuccess(File file) {
                L.e("apk 进度", "onSuccess--" + Thread.currentThread().getName());
            }

            @Override
            public void onFail() {
                L.e("apk 进度", "onFail" + Thread.currentThread().getName());
            }

            @Override
            public void onProgress(String percent) {
                tvKing2.setText(percent + "%");
            }
        });
    }
}
