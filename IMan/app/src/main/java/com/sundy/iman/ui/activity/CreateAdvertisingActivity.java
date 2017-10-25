package com.sundy.iman.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
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
import com.sundy.iman.entity.CommunityItemEntity;
import com.sundy.iman.entity.CreateAdvertisingEntity;
import com.sundy.iman.entity.LocationEntity;
import com.sundy.iman.entity.MsgEvent;
import com.sundy.iman.entity.QiNiuTokenItemEntity;
import com.sundy.iman.entity.QiNiuTokenListEntity;
import com.sundy.iman.entity.SelectMediaEntity;
import com.sundy.iman.entity.StaticContentEntity;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.FileUtils;
import com.sundy.iman.utils.MediaFileUtils;
import com.sundy.iman.view.TitleBarView;
import com.sundy.iman.view.dialog.CommonDialog;
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
 * Created by sundy on 17/10/5.
 */

public class CreateAdvertisingActivity extends BaseActivity {

    private static final int REQUEST_CODE_PERMISSION_LOCATION = 100;
    private static final int REQUEST_CODE_PERMISSION_PHOTO = 200;
    private static final int REQUEST_MEDIA = 300;
    private static final float VIDEO_MAX_SIZE = 100.0f;

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.et_subject)
    EditText etSubject;
    @BindView(R.id.et_detail)
    EditText etDetail;
    @BindView(R.id.tv_bytes)
    TextView tvBytes;
    @BindView(R.id.tv_select_community)
    TextView tvSelectCommunity;
    @BindView(R.id.rel_select_community)
    RelativeLayout relSelectCommunity;
    @BindView(R.id.rv_community)
    RecyclerView rvCommunity;
    @BindView(R.id.tv_total_user_title)
    TextView tvTotalUserTitle;
    @BindView(R.id.tv_total_user_value)
    TextView tvTotalUserValue;
    @BindView(R.id.tv_total_cost_title)
    TextView tvTotalCostTitle;
    @BindView(R.id.tv_total_cost_value)
    TextView tvTotalCostValue;
    @BindView(R.id.ll_state)
    LinearLayout llState;
    @BindView(R.id.et_tags)
    EditText etTags;
    @BindView(R.id.btn_add_tag)
    TextView btnAddTag;
    @BindView(R.id.fl_tags)
    TagFlowLayout flTags;
    @BindView(R.id.btn_more_tag)
    TextView btnMoreTag;
    @BindView(R.id.btn_confirm)
    TextView btnConfirm;
    @BindView(R.id.ll_how_get_imcoin)
    LinearLayout llHowGetImcoin;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.v_progress)
    NumberProgressBar vProgress;
    @BindView(R.id.rv_media)
    RecyclerView rvMedia;

    //声明AMapLocationClient类对象
    public AMapLocationClient locationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption locationOption = null;
    private Geocoder geocoder;
    private LocationEntity locationEntity;

    private final static int AGING = 48; //广告默认48小时消失
    private ArrayList<CommunityItemEntity> selectedCommunities = new ArrayList<>(); //选择的社区
    private CommunityAdapter communityAdapter;
    private ArrayList<String> selectedTags = new ArrayList<>(); //已选择的标签列表

    private StaggeredGridLayoutManager photoLayoutManager;
    private ArrayList<SelectMediaEntity> selectMediaEntities = new ArrayList<>(); //已选择的本地图片列表
    private MediaAdapter mediaAdapter;

    private UploadManager uploadManager;
    private boolean isUploading = false; //是否正在上传

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_create_ad);
        ButterKnife.bind(this);

        EventBus.getDefault().register(this);
        initTitle();
        getLocationPermission();
        init();
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.advertise));
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
        btnConfirm.setSelected(false);
        btnConfirm.setEnabled(false);

        Configuration config = new Configuration.Builder()
                .zone(AutoZone.autoZone)
                .build();
        uploadManager = new UploadManager(config);

        etSubject.addTextChangedListener(textWatcher);
        etDetail.addTextChangedListener(etDetailWatcher);

        geocoder = new Geocoder(this, Locale.getDefault());

        communityAdapter = new CommunityAdapter(R.layout.item_selected_community, selectedCommunities);
        rvCommunity.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvCommunity.setAdapter(communityAdapter);

        llState.removeAllViews();
        String[] stateArr = getResources().getStringArray(R.array.ad_charge_state);
        for (String state : stateArr) {
            TextView tvState = new TextView(this);
            tvState.setText(state);
            tvState.setTextSize(10);
            tvState.setTextColor(ContextCompat.getColor(this, R.color.txt_gray));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 5, 0, 5);
            tvState.setLayoutParams(params);
            llState.addView(tvState);
        }

        mediaAdapter = new MediaAdapter(this, selectMediaEntities, 10);
        photoLayoutManager = new StaggeredGridLayoutManager(5, OrientationHelper.VERTICAL);
        rvMedia.setLayoutManager(photoLayoutManager);
        mediaAdapter.setOnItemClickListener(onItemClickListener);
        rvMedia.setAdapter(mediaAdapter);
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
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri uri = Uri.parse("file://" + videoPath);
                            intent.setDataAndType(uri, "video/mp4");
                            startActivity(intent);
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

                        GPreviewBuilder.from(CreateAdvertisingActivity.this)
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
                tvBytes.setTextColor(ContextCompat.getColor(CreateAdvertisingActivity.this, R.color.main_red));
            } else {
                tvBytes.setTextColor(ContextCompat.getColor(CreateAdvertisingActivity.this, R.color.txt_normal));
            }

            tvBytes.setText("(" + content.length() + "/" + 144 + ")");

            canBtnClick();
        }
    };

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
                        AndPermission.rationaleDialog(CreateAdvertisingActivity.this, rationale).show();
                    }
                })
                .start();
    }

    //获取文件权限
    private void getFilePermission() {
        AndPermission.with(this)
                .requestCode(REQUEST_CODE_PERMISSION_PHOTO)
                .permission(Permission.STORAGE, Permission.CAMERA)
                .callback(this)
                .start();
    }

    //视频选择器
    private void pickPhotoAndVideo() {
        if (isVideoUploaded()) {
            MediaOptions.Builder builder = new MediaOptions.Builder();
            MediaOptions options = builder.canSelectMultiPhoto(true)
                    .build();
            if (options != null) {
                MediaPickerActivity.open(this, REQUEST_MEDIA, options);
            }
        } else {
            MediaOptions.Builder builder = new MediaOptions.Builder();
            MediaOptions options = builder.canSelectBothPhotoVideo()
                    .canSelectMultiPhoto(true).canSelectMultiVideo(false)
                    .build();
            if (options != null) {
                MediaPickerActivity.open(this, REQUEST_MEDIA, options);
            }
        }
    }

    //是否已经上传了视频（视频最多只有一个）
    private boolean isVideoUploaded() {
        boolean isVideoUploaded = false;
        if (selectMediaEntities != null && selectMediaEntities.size() > 0) {
            for (SelectMediaEntity selectMediaEntity : selectMediaEntities) {
                if (selectMediaEntity != null) {
                    String videoPath = selectMediaEntity.getLocalVideoPath();
                    if (!TextUtils.isEmpty(videoPath)) {
                        isVideoUploaded = true;
                    }
                }
            }
        }
        return isVideoUploaded;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA) {
            if (resultCode == RESULT_OK) {
                List<MediaItem> mMediaSelectedList = MediaPickerActivity.getMediaItemSelected(data);
                if (mMediaSelectedList != null) {
                    try {
                        for (MediaItem mediaItem : mMediaSelectedList) {
                            if (mediaItem != null) {
                                String mediaPath = mediaItem.getPathOrigin(CreateAdvertisingActivity.this);
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
                                        Luban.with(CreateAdvertisingActivity.this)
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

    //判断可否点击确认按钮
    private void canBtnClick() {
        String subject = etSubject.getText().toString().trim();
        if (TextUtils.isEmpty(subject) || selectedCommunities == null
                || selectedCommunities.size() == 0 || isUploading) {
            btnConfirm.setSelected(false);
            btnConfirm.setEnabled(false);
        } else {
            btnConfirm.setSelected(true);
            btnConfirm.setEnabled(true);
        }
    }

    @OnClick({R.id.rel_select_community, R.id.btn_add_tag, R.id.btn_more_tag, R.id.btn_confirm, R.id.ll_how_get_imcoin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rel_select_community:
                goSelectCommunity();
                break;
            case R.id.btn_add_tag:
                addTag();
                break;
            case R.id.btn_more_tag:
                goSelectTags();
                break;
            case R.id.ll_how_get_imcoin:
                getStaticContent(Constants.TYPE_HOW_GET_IMCOIN);
                break;
            case R.id.btn_confirm:
                createAd();
                break;
        }
    }

    //跳转选择社区
    private void goSelectCommunity() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedCommunity", selectedCommunities);
        UIHelper.jump(this, SelectCommunityActivity.class, bundle);
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

    //获取静态内容
    private void getStaticContent(int type) { //类型:1-使用条款，2-隐私条例，3-联系我们, 4-How to get imcoin?
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("type", type + "");
        Call<StaticContentEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getStaticContent(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<StaticContentEntity>() {
            @Override
            public void onSuccess(Call<StaticContentEntity> call, Response<StaticContentEntity> response) {
                StaticContentEntity staticContentEntity = response.body();
                if (staticContentEntity != null) {
                    int code = staticContentEntity.getCode();
                    if (code == Constants.CODE_SUCCESS) {
                        StaticContentEntity.DataEntity dataEntity = staticContentEntity.getData();
                        if (dataEntity != null) {
                            goWebView(dataEntity);
                        }
                    }
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<StaticContentEntity> call, Throwable t) {

            }
        });
    }

    //跳转Web View显示H5
    private void goWebView(StaticContentEntity.DataEntity dataEntity) {
        String url = dataEntity.getUrl();
        String title = getString(R.string.how_get_imcoin);
        if (TextUtils.isEmpty(url))
            return;
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("title", title);
        UIHelper.jump(this, WebActivity.class, bundle);
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
    private void saveLocation(double latitude, double longitude) {
        Logger.e("---->lat = " + latitude);
        Logger.e("---->lng = " + longitude);
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

    //设置显示选择的社区列表
    private void setCommunityList() {
        try {
            communityAdapter.setNewData(selectedCommunities);
            communityAdapter.notifyDataSetChanged();

            setCommunityValue();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设置所选社区的计数
    private void setCommunityValue() {
        int totalUser = 0;
        for (int i = 0; i < selectedCommunities.size(); i++) {
            CommunityItemEntity communityItemEntity = selectedCommunities.get(i);
            if (communityItemEntity != null) {
                String members = communityItemEntity.getMembers();
                if (!TextUtils.isEmpty(members)) {
                    int num = Integer.parseInt(members);
                    totalUser = totalUser + num;
                }
            }
        }

        tvTotalUserValue.setText(String.valueOf(totalUser));
        tvTotalCostValue.setText(String.valueOf(totalUser));
    }

    //创建广告
    private void createAd() {
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

        String communitys = "";
        if (selectedCommunities != null && selectedCommunities.size() > 0) {
            for (int i = 0; i < selectedCommunities.size(); i++) {
                CommunityItemEntity communityItemEntity = selectedCommunities.get(i);
                if (communityItemEntity != null) {
                    String communityId = communityItemEntity.getId();
                    if (i == selectedCommunities.size() - 1) {
                        communitys = communitys + communityId;
                    } else {
                        communitys = communitys + communityId + ",";
                    }
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
        param.put("communitys", communitys); //社区ID:多个社区ID以“,”作为分隔符
        param.put("aging", AGING + ""); //时效
        param.put("attachment", attachment); //附件 json格式数据:att_type为附件类型，1-图片，2-视频 url：附件存放路径
        //[{"att_type":"1"," url":"post/20170 9/74c06ef7f40ac a08bce37c0403 d08521.png"}]
        showProgress();
        Call<CreateAdvertisingEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .createAdvertising(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<CreateAdvertisingEntity>() {
            @Override
            public void onSuccess(Call<CreateAdvertisingEntity> call, Response<CreateAdvertisingEntity> response) {
                CreateAdvertisingEntity createAdvertisingEntity = response.body();
                if (createAdvertisingEntity != null) {
                    int code = createAdvertisingEntity.getCode();
                    String msg = createAdvertisingEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        sendUpdateSuccessEvent();
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
            public void onFailure(Call<CreateAdvertisingEntity> call, Throwable t) {

            }
        });
    }

    //显示成功创建广告弹框
    private void showCreateAdSuccessDialog() {
        final CommonDialog dialog = new CommonDialog(this);
        dialog.getTitle().setText(getString(R.string.success));
        dialog.getContent().setText(getString(R.string.ad_post_success_tips));
        dialog.getBtnCancel().setVisibility(View.GONE);
        dialog.getBtnOk().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgEvent event) {
        if (event != null) {
            String msg = event.getMsg();
            switch (msg) {
                case MsgEvent.EVENT_GET_COMMUNITIES:
                    selectedCommunities = (ArrayList<CommunityItemEntity>) event.getObj();
                    if (selectedCommunities != null && selectedCommunities.size() > 0) {
                        setCommunityList();
                    }
                    canBtnClick();
                    break;
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

    private class CommunityAdapter extends BaseQuickAdapter<CommunityItemEntity, BaseViewHolder> {

        public CommunityAdapter(@LayoutRes int layoutResId, @Nullable List<CommunityItemEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final CommunityItemEntity item) {
            TextView tv_community_name = helper.getView(R.id.tv_community_name);
            tv_community_name.setText(item.getName() + " (" + item.getMembers() + ")");

            ImageView iv_delete = helper.getView(R.id.iv_delete);
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedCommunities.remove(item);
                    notifyItemRemoved(helper.getPosition());

                    setCommunityValue();
                }
            });
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
        destroyLocation();
        FileUtils.clearFileCache(FileUtils.getImageCache());
    }

    //发送更新用户信息成功Event
    private void sendUpdateSuccessEvent() {
        MsgEvent msgEvent = new MsgEvent();
        msgEvent.setMsg(MsgEvent.EVENT_UPDATE_USER_INFO);
        EventBus.getDefault().post(msgEvent);
    }

}
