# UploadDemo
Retrofit2 文件上传及其进度显示
只涉及 Retrofit + RxJava 上传文件，像权限管理没有处理，毕竟只是演示多文件上传，显示进度

如果：只想测试一下 只需要把项目根目录 “上传文件” 目录下面的所有文件，复制到手机SD卡 “DCIM/Camera/”目录下面，即可测试文件上传

如果：不想复制文件 可以把 MainActivity 里面添加的文件改成手机里面存在的文件，不然会报 文件找不到异常


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
    
    
