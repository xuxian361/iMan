package com.sundy.iman.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;
import com.previewlibrary.GPreviewBuilder;
import com.previewlibrary.enitity.ThumbViewInfo;
import com.qiniu.android.common.AutoZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.utils.AsyncRun;
import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.adapter.MediaAdapter;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.CreatePostEntity;
import com.sundy.iman.entity.GetPostInfoEntity;
import com.sundy.iman.entity.LocationEntity;
import com.sundy.iman.entity.MsgEvent;
import com.sundy.iman.entity.QiNiuTokenItemEntity;
import com.sundy.iman.entity.QiNiuTokenListEntity;
import com.sundy.iman.entity.SelectMediaEntity;
import com.sundy.iman.entity.UpdatePostEntity;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.FileUtils;
import com.sundy.iman.utils.MediaFileUtils;
import com.sundy.iman.utils.NetWorkUtils;
import com.sundy.iman.utils.cache.CacheData;
import com.sundy.iman.view.TitleBarView;
import com.sundy.iman.view.dialog.CommonDialog;
import com.sundy.iman.view.popupwindow.SelectExpiryTimePopup;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import vn.tungdx.mediapicker.MediaItem;
import vn.tungdx.mediapicker.MediaOptions;
import vn.tungdx.mediapicker.activities.MediaPickerActivity;

/**
 * Created by sundy on 17/10/26.
 */

public class EditPostActivity extends BaseActivity {

    private static final int REQUEST_CODE_PERMISSION_LOCATION = 100;
    private static final int REQUEST_CODE_PERMISSION_PHOTO = 200;
    private static final int REQUEST_MEDIA = 300;
    private static final float VIDEO_MAX_SIZE = 50.0f;

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.et_subject)
    EditText etSubject;
    @BindView(R.id.et_detail)
    EditText etDetail;
    @BindView(R.id.tv_bytes)
    TextView tvBytes;
    @BindView(R.id.rv_media)
    RecyclerView rvMedia;
    @BindView(R.id.v_progress)
    NumberProgressBar vProgress;
    @BindView(R.id.et_tags)
    EditText etTags;
    @BindView(R.id.btn_add_tag)
    TextView btnAddTag;
    @BindView(R.id.fl_tags)
    TagFlowLayout flTags;
    @BindView(R.id.btn_more_tag)
    TextView btnMoreTag;
    @BindView(R.id.iv_arrow_expire_time)
    ImageView ivArrowExpireTime;
    @BindView(R.id.tv_expire_time)
    TextView tvExpireTime;
    @BindView(R.id.rel_expire_time)
    RelativeLayout relExpireTime;
    @BindView(R.id.btn_post_again)
    TextView btnPostAgain;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.btn_post_modified_again)
    TextView btnPostModifiedAgain;

    private String community_id;
    private String post_id;
    private String creator_id;

    //声明AMapLocationClient类对象
    public AMapLocationClient locationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption locationOption = null;
    private Geocoder geocoder;
    private LocationEntity locationEntity;

    private int aging = 48; //广告默认48小时消失
    private ArrayList<String> selectedTags = new ArrayList<>(); //已选择的标签列表

    private StaggeredGridLayoutManager photoLayoutManager;
    private ArrayList<SelectMediaEntity> selectMediaEntities = new ArrayList<>(); //已选择的本地图片列表
    private MediaAdapter mediaAdapter;

    private UploadManager uploadManager;
    private boolean isUploading = false; //是否正在上传

    private SelectExpiryTimePopup selectExpiryTimePopup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_edit_post);
        ButterKnife.bind(this);

        EventBus.getDefault().register(this);
        initData();
        initTitle();
        getLocationPermission();
        init();
        getPostInfo();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            post_id = bundle.getString("post_id");
            creator_id = bundle.getString("creator_id");
        }
    }

    private void initTitle() {
        titleBar.setBackMode();
        titleBar.setOnClickListener(new OnTitleBarClickListener() {
            @Override
            public void onLeftImgClick() {
                finish();
            }

            @Override
            public void onLeftTxtClick() {

            }

            @Override
            public void onRightImgClick() {

            }

            @Override
            public void onRightTxtClick() {

            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    private void init() {
        btnPostModifiedAgain.setSelected(true);
        btnPostModifiedAgain.setEnabled(true);
        btnPostAgain.setSelected(true);
        btnPostAgain.setEnabled(true);

        tvExpireTime.setText(getString(R.string.hour_48));

        Configuration config = new Configuration.Builder()
                .zone(AutoZone.autoZone)
                .build();
        uploadManager = new UploadManager(config);

        etSubject.addTextChangedListener(textWatcher);
        etDetail.addTextChangedListener(etDetailWatcher);

        geocoder = new Geocoder(this, Locale.getDefault());
        selectExpiryTimePopup = new SelectExpiryTimePopup(this);


        mediaAdapter = new MediaAdapter(this, selectMediaEntities, 1);
        photoLayoutManager = new StaggeredGridLayoutManager(5, OrientationHelper.VERTICAL);
        rvMedia.setLayoutManager(photoLayoutManager);
        mediaAdapter.setOnItemClickListener(onItemClickListener);
        rvMedia.setAdapter(mediaAdapter);
    }

    //获取Post 信息
    private void getPostInfo() {
        final boolean hasPermission = AndPermission.hasPermission(this, Permission.STORAGE);
        if (hasPermission) {
            GetPostInfoEntity.DataEntity dataEntity = CacheData.getInstance().getPostInfo(post_id, creator_id);
            if (dataEntity != null) {
                try {
                    showData(dataEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (NetWorkUtils.isNetAvailable(this)) {
            Map<String, String> param = new HashMap<>();
            param.put("mid", PaperUtils.getMId());
            param.put("session_key", PaperUtils.getSessionKey());
            param.put("post_id", post_id); //post id
            param.put("creator_id", creator_id); //post的作者ID
            showProgress();
            Call<GetPostInfoEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                    .getPostInfo(ParamHelper.formatData(param));
            call.enqueue(new RetrofitCallback<GetPostInfoEntity>() {
                @Override
                public void onSuccess(Call<GetPostInfoEntity> call, Response<GetPostInfoEntity> response) {
                    GetPostInfoEntity getPostInfoEntity = response.body();
                    if (getPostInfoEntity != null) {
                        int code = getPostInfoEntity.getCode();
                        String msg = getPostInfoEntity.getMsg();
                        if (code == Constants.CODE_SUCCESS) {
                            GetPostInfoEntity.DataEntity dataEntity = getPostInfoEntity.getData();
                            if (dataEntity != null) {
                                try {
                                    if (hasPermission) {
                                        CacheData.getInstance().savePostInfo(dataEntity, post_id, creator_id);
                                    }
                                    showData(dataEntity);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            MainApp.getInstance().showToast(msg);
                        }
                    }
                }

                @Override
                public void onAfter() {
                    hideProgress();
                }

                @Override
                public void onFailure(Call<GetPostInfoEntity> call, Throwable t) {

                }
            });
        }
    }

    //显示Post 信息
    private void showData(GetPostInfoEntity.DataEntity dataEntity) throws Exception {
        String subject = dataEntity.getTitle();
        String detail = dataEntity.getDetail();

        etSubject.setText(subject);
        etDetail.setText(detail);

        String type = dataEntity.getType(); //类型: 1-普通post，2-广告
        String status = dataEntity.getStatus(); //post状态 1-有效,2-过期, 3-取消

        if (!TextUtils.isEmpty(status)) {
            if (status.equals("1")) {
                btnPostModifiedAgain.setVisibility(View.VISIBLE);
                btnPostAgain.setVisibility(View.GONE);
            } else {
                btnPostModifiedAgain.setVisibility(View.GONE);
                btnPostAgain.setVisibility(View.VISIBLE);
            }
        }

        String effective_time = dataEntity.getEffective_time();
        String create_time = dataEntity.getCreate_time();

        //获取时间差
        long second = Long.parseLong(effective_time) - Long.parseLong(create_time);
        int hours = (int) (second / 60 / 60);
        aging = hours;
        tvExpireTime.setText(hours + " " + getString(R.string.hours));

        community_id = dataEntity.getCommunitys();

        String tags = dataEntity.getTags();
        if (!TextUtils.isEmpty(tags)) {
            String strArr[] = tags.split(",");
            if (strArr != null && strArr.length > 0) {
                selectedTags.clear();
                for (int i = 0; i < strArr.length; i++) {
                    selectedTags.add(strArr[i]);
                }
            }
            setTagsList();
        }

        List<GetPostInfoEntity.AttachmentEntity> attachment = dataEntity.getAttachment();
        if (attachment != null && attachment.size() > 0) {
            for (GetPostInfoEntity.AttachmentEntity attachmentEntity : attachment) {
                if (attachmentEntity != null) {
                    SelectMediaEntity selectMediaEntity = new SelectMediaEntity();

                    String path = attachmentEntity.getPath();
                    String att_type = attachmentEntity.getAtt_type(); //1-图片，2-视频
                    String thumbnail = attachmentEntity.getThumbnail();
                    String url = attachmentEntity.getUrl();

                    if (att_type.equals("1")) {
                        selectMediaEntity.setLocalImagePath(url);
                    } else {
                        selectMediaEntity.setLocalImagePath(thumbnail);
                        selectMediaEntity.setLocalVideoPath(url);
                    }

                    selectMediaEntity.setPath(path);

                    selectMediaEntities.add(selectMediaEntity);
                }
            }
            mediaAdapter.notifyDataSetChanged();
        }

        List<GetPostInfoEntity.CommunityEntity> community_list = dataEntity.getCommunity_list();
        if (community_list != null && community_list.size() > 0) {
            GetPostInfoEntity.CommunityEntity communityEntity = community_list.get(0);
            if (communityEntity != null) {
                titleBar.setBackMode(communityEntity.getName());
                titleBar.setVisibility(View.VISIBLE);
            }
        }

    }

    private MediaAdapter.OnItemClickListener onItemClickListener = new MediaAdapter.OnItemClickListener() {
        @Override
        public void onAddClick(MediaAdapter.PhotoViewHolder holder, int position) {
            getFilePermission();
        }

        @Override
        public void onDeleteClick(MediaAdapter.PhotoViewHolder holder, int position) {
            try {
                if (selectMediaEntities != null) {
                    selectMediaEntities.remove(position);
                    mediaAdapter.notifyDataSetChanged();
                }
                vProgress.setProgress(0);
                vProgress.setVisibility(View.INVISIBLE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onImageClick(MediaAdapter.PhotoViewHolder holder, int position) {
            Logger.e("----->position = " + position);
            if (selectMediaEntities != null) {
                SelectMediaEntity selectMediaEntity = selectMediaEntities.get(position);
                if (selectMediaEntity != null) {
                    String videoPath = selectMediaEntity.getLocalVideoPath();
                    if (!TextUtils.isEmpty(videoPath)) //视频
                    {
                        try {
                            if (videoPath.startsWith("http")) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                Uri uri = Uri.parse(videoPath);
                                intent.setDataAndType(uri, "video/*");
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                Uri uri = Uri.parse("file://" + videoPath);
                                intent.setDataAndType(uri, "video/mp4");
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        ArrayList<ThumbViewInfo> selectList = new ArrayList<>();
                        for (int i = 0; i < selectMediaEntities.size(); i++) {
                            SelectMediaEntity entity = selectMediaEntities.get(i);
                            if (entity != null) {
                                String localPath = entity.getLocalImagePath();
                                String localVideoPath = entity.getLocalVideoPath();
                                ThumbViewInfo thumbViewInfo = new ThumbViewInfo(localPath);
                                if (TextUtils.isEmpty(localVideoPath)) {
                                    selectList.add(thumbViewInfo);
                                }
                            }
                        }

                        GPreviewBuilder.from(EditPostActivity.this)
                                .setData(selectList)
                                .setCurrentIndex(position)
                                .setType(GPreviewBuilder.IndicatorType.Number)
                                .start();

                    }
                }
            }
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            canBtnClick();
        }
    };

    private TextWatcher etDetailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String content = etDetail.getText().toString().trim();
            if (content.length() > 144) {
                tvBytes.setTextColor(ContextCompat.getColor(EditPostActivity.this, R.color.main_red));
            } else {
                tvBytes.setTextColor(ContextCompat.getColor(EditPostActivity.this, R.color.txt_normal));
            }

            tvBytes.setText("(" + content.length() + "/" + 144 + ")");

            canBtnClick();
        }
    };

    //判断可否点击确认按钮
    private void canBtnClick() {
        String subject = etSubject.getText().toString().trim();
        if (TextUtils.isEmpty(subject) || isUploading) {
            btnPostAgain.setSelected(false);
            btnPostAgain.setEnabled(false);

            btnPostModifiedAgain.setSelected(false);
            btnPostModifiedAgain.setEnabled(false);
        } else {
            btnPostAgain.setSelected(true);
            btnPostAgain.setEnabled(true);

            btnPostModifiedAgain.setSelected(true);
            btnPostModifiedAgain.setEnabled(true);
        }
    }

    //获取定位全向
    private void getLocationPermission() {
        AndPermission.with(this)
                .requestCode(REQUEST_CODE_PERMISSION_LOCATION)
                .permission(Permission.LOCATION)
                .callback(this)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                        AndPermission.rationaleDialog(EditPostActivity.this, rationale).show();
                    }
                })
                .start();
    }

    //获取文件权限
    private void getFilePermission() {
        if (!NetWorkUtils.isNetAvailable(this)) {
            MainApp.getInstance().showToast(getString(R.string.network_not_available));
            return;
        }
        AndPermission.with(this)
                .requestCode(REQUEST_CODE_PERMISSION_PHOTO)
                .permission(Permission.STORAGE, Permission.CAMERA)
                .callback(this)
                .start();
    }

    //视频选择器
    private void pickPhotoAndVideo() {
        MediaOptions.Builder builder = new MediaOptions.Builder();
        MediaOptions options = builder.canSelectBothPhotoVideo()
                .canSelectMultiPhoto(false).canSelectMultiVideo(false)
                .build();
        if (options != null) {
            MediaPickerActivity.open(this, REQUEST_MEDIA, options);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA) {
            if (resultCode == RESULT_OK) {
                List<MediaItem> mMediaSelectedList = MediaPickerActivity
                        .getMediaItemSelected(data);
                if (mMediaSelectedList != null) {
                    try {
                        for (MediaItem mediaItem : mMediaSelectedList) {
                            if (mediaItem != null) {
                                String mediaPath = mediaItem.getPathOrigin(EditPostActivity.this);
                                Logger.e("------>mediaPath= " + mediaPath);
                                if (!TextUtils.isEmpty(mediaPath)) {
                                    if (MediaFileUtils.isVideoFileType(mediaPath)) { //视频
                                        File file = new File(mediaPath);
                                        if (file.exists()) {
                                            long fileSize = FileUtils.getFileSize(file); //单位：B
                                            float fileSizeMb = fileSize / 1024.0f / 1024.0f;
                                            if (fileSizeMb > VIDEO_MAX_SIZE) {
                                                MainApp.getInstance().showToast(getString(R.string.video_size_more_than_100));
                                                return;
                                            }
                                            getQiNiuToken(file);
                                        }
                                    } else { //图片
                                        String targetDir = FileUtils.getImageCache();
                                        //压缩图片
                                        Luban.with(EditPostActivity.this)
                                                .load(mediaPath)                                   // 传人要压缩的图片列表
                                                .ignoreBy(100)                                  // 忽略不压缩图片的大小
                                                .setTargetDir(targetDir)                        // 设置压缩后文件存储位置
                                                .setCompressListener(new OnCompressListener() { //设置回调
                                                    @Override
                                                    public void onStart() {
                                                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                                                        Logger.i("----->压缩开始");
                                                    }

                                                    @Override
                                                    public void onSuccess(File file) {
                                                        // TODO 压缩成功后调用，返回压缩后的图片文件
                                                        if (file != null) {
                                                            Logger.i("----->压缩成功 :" + file.getPath());
                                                            getQiNiuToken(file);
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(Throwable e) {
                                                        // TODO 当压缩过程出现问题时调用
                                                        Logger.i("----->压缩失败");
                                                    }
                                                }).launch();    //启动压缩
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Logger.e("Error to get media, NULL");
                }
            }
        }
    }

    //获取七牛上传token
    private void getQiNiuToken(final File file) {
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        String fileExtension = FileUtils.getFileExtension(file.getPath());
        if (TextUtils.isEmpty(fileExtension)) {
            fileExtension = "jpg";
        }
        jsonObject.addProperty("ext", fileExtension);
        jsonObject.addProperty("width", "1080");
        jsonObject.addProperty("height", "1920");
        jsonArray.add(jsonObject);
        final Map<String, String> param = new HashMap<>();
        param.put("category", "3"); //类型:1-用户头像，2-社区，3-post
        param.put("upload_params", jsonArray.toString()); //上传参数:json格式的数据:ext-上传文件的格式，width-上传文件的宽度，height-上传文件的高度
        Call<QiNiuTokenListEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getQiNiuToken(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<QiNiuTokenListEntity>() {
            @Override
            public void onSuccess(Call<QiNiuTokenListEntity> call, Response<QiNiuTokenListEntity> response) {
                QiNiuTokenListEntity qiNiuTokenListEntity = response.body();
                if (qiNiuTokenListEntity != null) {
                    int code = qiNiuTokenListEntity.getCode();
                    String msg = qiNiuTokenListEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        QiNiuTokenListEntity.DataEntity dataEntity = qiNiuTokenListEntity.getData();
                        if (dataEntity != null) {
                            List<QiNiuTokenItemEntity> list = dataEntity.getList();
                            if (list != null && list.size() > 0) {
                                QiNiuTokenItemEntity itemEntity = list.get(0);
                                if (itemEntity != null) {
                                    String key = itemEntity.getKey();
                                    String token = itemEntity.getToken();
                                    String header = itemEntity.getUrl();
                                    String path = itemEntity.getPath();
                                    if (file != null && !TextUtils.isEmpty(token)) {
                                        uploadMedia(file, key, token, path);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<QiNiuTokenListEntity> call, Throwable t) {

            }
        });
    }

    //上传视频和图片
    private void uploadMedia(final File file, String key, String token, final String path) {
        uploadManager.put(file, key, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                AsyncRun.runInMain(new Runnable() {
                    @Override
                    public void run() {
                        vProgress.setVisibility(View.INVISIBLE);
                        isUploading = false;
                        canBtnClick();
                    }
                });
                if (info.isOK()) {
                    Logger.e("--->Upload Success");
                    if (selectMediaEntities != null) {
                        SelectMediaEntity selectMediaEntity = new SelectMediaEntity();

                        if (MediaFileUtils.isVideoFileType(file.getPath())) //视频
                        {
                            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                            if (bitmap != null) {
                                String thumbnailPath = FileUtils.saveBitmapToSD(bitmap);
                                selectMediaEntity.setLocalImagePath(thumbnailPath);
                            }
                            selectMediaEntity.setLocalVideoPath(file.getPath());
                        } else { //图片
                            selectMediaEntity.setLocalImagePath(file.getPath());
                        }

                        selectMediaEntity.setPath(path);
                        selectMediaEntities.add(selectMediaEntity);

                        AsyncRun.runInMain(new Runnable() {
                            @Override
                            public void run() {
                                mediaAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } else {
                    Logger.e("--->Upload Fail" + info.toString());
                    //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                }
            }
        }, new UploadOptions(null, null, false, new UpProgressHandler() {
            @Override
            public void progress(String key, final double percent) {
//                Logger.e("--->percent : " + percent);
                AsyncRun.runInMain(new Runnable() {
                    @Override
                    public void run() {
                        vProgress.setVisibility(View.VISIBLE);
                        vProgress.setProgress((int) (percent * 100));
                        isUploading = true;
                        canBtnClick();
                    }
                });
            }
        }, null));
    }

    //显示选择有效期Popup
    private void selectExpireTimePopup() {
        selectExpiryTimePopup.setOnClickListener(new SelectExpiryTimePopup.OnClickListener() {
            @Override
            public void onTimeSelected(int hours, String name) {
                selectExpiryTimePopup.dismiss();
                aging = hours;
                tvExpireTime.setText(name);
            }
        });
        selectExpiryTimePopup.showAtLocation(llContent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //添加标签
    private void addTag() {
        String tag = etTags.getText().toString().trim();
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        if (tag.contains(",")) {
            MainApp.getInstance().showToast(getString(R.string.tag_cannot_contain_dot));
            return;
        }

        if (selectedTags != null) {
            if (selectedTags.contains(tag)) {
                return;
            }
            selectedTags.add(tag);
        }
        setTagsList();
        etTags.setText("");
        closeKeyboard();
    }

    //设置标签列表
    private void setTagsList() {
        final TagAdapter<String> adapter_Tag = new TagAdapter<String>(selectedTags) {
            @Override
            public View getView(FlowLayout parent, int position, String item) {
                View view = getLayoutInflater().inflate(R.layout.item_tag_can_remove,
                        null, false);
                TextView tv = (TextView) view.findViewById(R.id.tv_tag);
                if (!TextUtils.isEmpty(item)) {
                    tv.setText(item);
                }
                return view;
            }
        };
        flTags.setAdapter(adapter_Tag);
        flTags.setEnabled(true);
        flTags.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, final int position, FlowLayout parent) {
                selectedTags.remove(selectedTags.get(position));
                adapter_Tag.notifyDataChanged();
                canBtnClick();
                return false;
            }
        });
    }

    //跳转选择标签
    private void goSelectTags() {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("selectedTags", selectedTags);
        UIHelper.jump(this, SelectTagsActivity.class, bundle);
    }

    @PermissionYes(REQUEST_CODE_PERMISSION_LOCATION)
    private void getPermissionLocationYes(@NonNull List<String> grantedPermissions) {
        Logger.e("位置权限申请成功!");
        initLocation();
        startLocation();
    }

    @PermissionNo(REQUEST_CODE_PERMISSION_LOCATION)
    private void getPermissionLocationNo(@NonNull List<String> deniedPermissions) {
        Logger.e("位置权限申请失败!");
    }

    @PermissionYes(REQUEST_CODE_PERMISSION_PHOTO)
    private void getPermissionStorageYes(@NonNull List<String> grantedPermissions) {
        Logger.e("文件操作权限申请成功!");
        pickPhotoAndVideo();
    }

    @PermissionNo(REQUEST_CODE_PERMISSION_PHOTO)
    private void getPermissionStorageNo(@NonNull List<String> deniedPermissions) {
        Logger.e("文件操作权限申请失败!");
    }

    //初始化定位
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this);
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    //默认的定位参数
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    //开始定位
    private void startLocation() {
        if (locationClient != null)
            locationClient.startLocation();
    }

    //停止定位
    private void stopLocation() {
        if (locationClient != null)
            locationClient.stopLocation();
    }

    //销毁定位
    private void destroyLocation() {
        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    //定位监听
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    Logger.i("定位成功");
                    String address = location.getAddress();
                    if (!TextUtils.isEmpty(address)) {
                        Logger.i("获取定位信息成功");
                        stopLocation();
                        saveLocation(location.getLatitude(), location.getLongitude());
                    } else {
                        Logger.w("获取定位信息失败");
                    }
                } else {
                    //定位失败
                    Logger.e("定位失败");
                }
            } else {
                Logger.e("定位失败，loc is null");
            }
        }
    };

    //保存定位信息
    private void saveLocation(final double latitude, final double longitude) {
        Logger.e("---->lat = " + latitude);
        Logger.e("---->lng = " + longitude);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Address> locationList = geocoder.getFromLocation(latitude, longitude, 1);
                    Logger.e("---->size =" + locationList.size());
                    if (locationList != null && locationList.size() > 0) {
                        Address address = locationList.get(0);
                        if (address != null) {
                            String country = address.getCountryName();
                            String province = address.getAdminArea();
                            String city = address.getLocality();
                            String district = address.getSubLocality();
                            String addressStr = country + " " + province + " " + city + " " + district + " " + address.getFeatureName();
                            Logger.e("----->国家 = " + country);
                            Logger.e("----->省份 = " + province);
                            Logger.e("----->城市 = " + city);
                            Logger.e("----->区域 = " + district);
                            Logger.e("----->门牌号 = " + addressStr);

                            locationEntity = new LocationEntity();
                            locationEntity.setCountry(address.getCountryName());
                            locationEntity.setProvince(address.getAdminArea());
                            locationEntity.setCity(address.getLocality());
                            locationEntity.setDistrict(address.getSubLocality());
                            locationEntity.setAddress(addressStr);
                            locationEntity.setLat(address.getLatitude());
                            locationEntity.setLng(address.getLongitude());

                            canBtnClick();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgEvent event) {
        if (event != null) {
            String msg = event.getMsg();
            switch (msg) {
                case MsgEvent.EVENT_GET_TAGS:
                    selectedTags = (ArrayList<String>) event.getObj();
                    if (selectedTags != null && selectedTags.size() > 0) {
                        setTagsList();
                    }
                    canBtnClick();
                    break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (selectExpiryTimePopup != null) {
            selectExpiryTimePopup.dismiss();
            selectExpiryTimePopup = null;
        }
        destroyLocation();
        FileUtils.clearFileCache(FileUtils.getImageCache());
    }

    @OnClick({R.id.btn_add_tag, R.id.btn_more_tag, R.id.rel_expire_time, R.id.btn_post_modified_again, R.id.btn_post_again})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add_tag:
                addTag();
                break;
            case R.id.btn_more_tag:
                goSelectTags();
                break;
            case R.id.rel_expire_time:
                selectExpireTimePopup();
                break;
            case R.id.btn_post_modified_again:
                updatePost();
                break;
            case R.id.btn_post_again:
                createPost();
                break;
        }
    }

    //修改post
    private void updatePost() {
        String title = etSubject.getText().toString().trim();
        String detail = etDetail.getText().toString().trim();

        String country = "";
        String province = "";
        String city = "";
        String latitude = "";
        String longitude = "";
        String addressStr = "";
        String location = "";
        if (locationEntity != null) {
            country = locationEntity.getCountry();
            province = locationEntity.getProvince();
            city = locationEntity.getCity();
            latitude = locationEntity.getLat() + "";
            longitude = locationEntity.getLng() + "";
            addressStr = locationEntity.getAddress();

            if (!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city)) {
                location = province + " " + city;
            } else if (TextUtils.isEmpty(province) && TextUtils.isEmpty(city)) {
                location = country;
            } else {
                if (TextUtils.isEmpty(province)) {
                    location = city;
                } else {
                    location = province;
                }
            }
        }

        String tags = "";
        if (selectedTags != null && selectedTags.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < selectedTags.size(); i++) {
                String tag = selectedTags.get(i);
                if (!TextUtils.isEmpty(tag)) {
                    if (i == selectedTags.size() - 1) {
                        sb.append(tag);
                    } else {
                        sb.append(tag + ",");
                    }
                }
            }
            tags = sb.toString();
        }

        String attachment = "";
        JsonArray attachmentArr = new JsonArray();
        if (selectMediaEntities != null && selectMediaEntities.size() > 0) {
            for (int i = 0; i < selectMediaEntities.size(); i++) {
                SelectMediaEntity selectMediaEntity = selectMediaEntities.get(i);
                if (selectMediaEntity != null) {
                    JsonObject jsonObject = new JsonObject();

                    String path = selectMediaEntity.getPath();
                    String localVideoPath = selectMediaEntity.getLocalVideoPath();
                    if (TextUtils.isEmpty(localVideoPath)) {
                        jsonObject.addProperty("att_type", "1");
                    } else {
                        jsonObject.addProperty("att_type", "2");
                    }
                    jsonObject.addProperty("url", path);
                    attachmentArr.add(jsonObject);
                }
            }
        }
        if (attachmentArr.size() > 0) {
            attachment = attachmentArr.toString();
        }

        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("title", title); //post标题
        param.put("detail", detail); //post详情
        param.put("tags", tags); //标签
        param.put("location", location);
        param.put("latitude", latitude);
        param.put("longitude", longitude);
        param.put("community_id", community_id); //社区ID
        param.put("aging", aging + ""); //时效
        param.put("attachment", attachment); //附件 json格式数据:att_type为附件类型，1-图片，2-视频 url：附件存放路径
        //[{"att_type":"1"," url":"post/20170 9/74c06ef7f40ac a08bce37c0403 d08521.png"}]
        param.put("post_id", post_id);
        showProgress();
        Call<UpdatePostEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .updatePost(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<UpdatePostEntity>() {
            @Override
            public void onSuccess(Call<UpdatePostEntity> call, Response<UpdatePostEntity> response) {
                UpdatePostEntity updatePostEntity = response.body();
                if (updatePostEntity != null) {
                    int code = updatePostEntity.getCode();
                    String msg = updatePostEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        showUpdateAdSuccessDialog();
                    } else {
                        MainApp.getInstance().showToast(msg);
                    }
                }
            }

            @Override
            public void onAfter() {
                hideProgress();
            }

            @Override
            public void onFailure(Call<UpdatePostEntity> call, Throwable t) {

            }
        });
    }

    //显示成功修改Post弹框
    private void showUpdateAdSuccessDialog() {
        final CommonDialog dialog = new CommonDialog(this);
        dialog.getTitle().setText(getString(R.string.success));
        dialog.getContent().setText(getString(R.string.message_post_success_tips));
        dialog.getBtnCancel().setVisibility(View.GONE);
        dialog.getBtnOk().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //SUNDY - 跳转到社区消息列表

            }
        });
    }

    //创建post
    private void createPost() {
        if (!NetWorkUtils.isNetAvailable(this)) {
            MainApp.getInstance().showToast(getString(R.string.network_not_available));
            return;
        }

        String title = etSubject.getText().toString().trim();
        String detail = etDetail.getText().toString().trim();

        String country = "";
        String province = "";
        String city = "";
        String latitude = "";
        String longitude = "";
        String addressStr = "";
        String location = "";
        if (locationEntity != null) {
            country = locationEntity.getCountry();
            province = locationEntity.getProvince();
            city = locationEntity.getCity();
            latitude = locationEntity.getLat() + "";
            longitude = locationEntity.getLng() + "";
            addressStr = locationEntity.getAddress();

            if (!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city)) {
                location = province + " " + city;
            } else if (TextUtils.isEmpty(province) && TextUtils.isEmpty(city)) {
                location = country;
            } else {
                if (TextUtils.isEmpty(province)) {
                    location = city;
                } else {
                    location = province;
                }
            }
        }

        String tags = "";
        if (selectedTags != null && selectedTags.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < selectedTags.size(); i++) {
                String tag = selectedTags.get(i);
                if (!TextUtils.isEmpty(tag)) {
                    if (i == selectedTags.size() - 1) {
                        sb.append(tag);
                    } else {
                        sb.append(tag + ",");
                    }
                }
            }
            tags = sb.toString();
        }

        String attachment = "";
        JsonArray attachmentArr = new JsonArray();
        if (selectMediaEntities != null && selectMediaEntities.size() > 0) {
            for (int i = 0; i < selectMediaEntities.size(); i++) {
                SelectMediaEntity selectMediaEntity = selectMediaEntities.get(i);
                if (selectMediaEntity != null) {
                    JsonObject jsonObject = new JsonObject();

                    String path = selectMediaEntity.getPath();
                    String localVideoPath = selectMediaEntity.getLocalVideoPath();
                    if (TextUtils.isEmpty(localVideoPath)) {
                        jsonObject.addProperty("att_type", "1");
                    } else {
                        jsonObject.addProperty("att_type", "2");
                    }
                    jsonObject.addProperty("url", path);
                    attachmentArr.add(jsonObject);
                }
            }
        }
        if (attachmentArr.size() > 0) {
            attachment = attachmentArr.toString();
        }

        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("title", title); //post标题
        param.put("detail", detail); //post详情
        param.put("tags", tags); //标签
        param.put("location", location);
        param.put("latitude", latitude);
        param.put("longitude", longitude);
        param.put("community_id", community_id); //社区ID
        param.put("aging", aging + ""); //时效
        param.put("attachment", attachment); //附件 json格式数据:att_type为附件类型，1-图片，2-视频 url：附件存放路径
        //[{"att_type":"1"," url":"post/20170 9/74c06ef7f40ac a08bce37c0403 d08521.png"}]
        showProgress();
        Call<CreatePostEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .createPost(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<CreatePostEntity>() {
            @Override
            public void onSuccess(Call<CreatePostEntity> call, Response<CreatePostEntity> response) {
                CreatePostEntity createPostEntity = response.body();
                if (createPostEntity != null) {
                    int code = createPostEntity.getCode();
                    String msg = createPostEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        showCreateAdSuccessDialog();
                    } else {
                        MainApp.getInstance().showToast(msg);
                    }
                }
            }

            @Override
            public void onAfter() {
                hideProgress();
            }

            @Override
            public void onFailure(Call<CreatePostEntity> call, Throwable t) {

            }
        });
    }

    //显示成功创建Post弹框
    private void showCreateAdSuccessDialog() {
        final CommonDialog dialog = new CommonDialog(this);
        dialog.getTitle().setText(getString(R.string.success));
        dialog.getContent().setText(getString(R.string.message_post_success_tips));
        dialog.getBtnCancel().setVisibility(View.GONE);
        dialog.getBtnOk().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                titleBar.setTitleTvText(getString(R.string.active_message));
                btnPostModifiedAgain.setVisibility(View.VISIBLE);
                btnPostAgain.setVisibility(View.GONE);
            }
        });
    }


}
