package com.fy.upload;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.widget.TextView;

import com.fy.upload.retrofit.up.LoadCallBack;
import com.fy.upload.retrofit.LoadService;
import com.fy.upload.retrofit.RequestUtils;
import com.fy.upload.retrofit.up.UploadOnSubscribe;
import com.fy.upload.utils.TransfmtUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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

        List<String> files = new ArrayList<>();
        files.add(TransfmtUtils.getSDCardPath() + "DCIM/Camera/679f6337gy1fr69ynfq3nj20hs0qodh0.jpg");
        files.add(TransfmtUtils.getSDCardPath() + "DCIM/Camera/IMG_20181108_144507.jpg");
        files.add(TransfmtUtils.getSDCardPath() + "DCIM/Camera/RED,胡歌 - 逍遥叹（Cover 胡歌）.mp3");
        files.add(TransfmtUtils.getSDCardPath() + "DCIM/Camera/马郁 - 下辈子如果我还记得你.mp3");

        uploadFiles(files, tvKing);

        List<String> files1 = new ArrayList<>();
        files1.add(TransfmtUtils.getSDCardPath() + "DCIM/Camera/IMG_20181108_143502.jpg");
        files1.add(TransfmtUtils.getSDCardPath() + "DCIM/Camera/序人Hm - 再见（cover：张震岳）.mp3");
        uploadFiles(files1, tvKing2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void uploadFiles(List<String> files, TextView textView) {
        UploadOnSubscribe uploadOnSubscribe = new UploadOnSubscribe();
        ArrayMap<String, Object> params = new ArrayMap<>();
        params.put("filePathList", files);
        params.put("UploadOnSubscribe", uploadOnSubscribe);

        Observable.merge(Observable.create(uploadOnSubscribe), RequestUtils.create(LoadService.class).uploadFile(params))
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
}
