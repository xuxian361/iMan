package com.sundy.iman.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;
import com.qiniu.android.common.AutoZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.MemberInfoEntity;
import com.sundy.iman.entity.QiNiuTokenItemEntity;
import com.sundy.iman.entity.QiNiuTokenListEntity;
import com.sundy.iman.entity.SaveMemberEntity;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.DateUtils;
import com.sundy.iman.utils.DeviceUtils;
import com.sundy.iman.utils.FileUtils;
import com.sundy.iman.view.TitleBarView;
import com.sundy.iman.view.popupwindow.SelectGenderPopup;
import com.sundy.iman.view.popupwindow.SelectPhotoPopup;
import com.yalantis.ucrop.UCrop;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import me.iwf.photopicker.PhotoPicker;
import retrofit2.Call;
import retrofit2.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 编辑用户信息
 * Created by sundy on 17/9/22.
 */

public class EditProfileActivity extends BaseActivity {

    private static final int REQUEST_CODE_PERMISSION_LOCATION = 100;
    private static final int REQUEST_CODE_PERMISSION_PHOTO = 200;
    private static final int REQUEST_CODE_CAMERA = 300;

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.iv_header)
    CircleImageView ivHeader;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_about)
    EditText etAbout;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.rel_gender)
    RelativeLayout relGender;
    @BindView(R.id.rel_header)
    RelativeLayout relHeader;
    @BindView(R.id.iv_location)
    ImageView ivLocation;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.line_location)
    View lineLocation;
    @BindView(R.id.rel_location)
    RelativeLayout relLocation;
    @BindView(R.id.iv_arrow_gender)
    ImageView ivArrowGender;
    @BindView(R.id.tv_bytes)
    TextView tvBytes;

    private SelectPhotoPopup selectPhotoPopup;
    private Uri imageUri; //拍照后保存的图片uri
    private SelectGenderPopup selectGenderPopup;
    private int curGender = 1; //1:male;2:female
    private File curHeaderFile;
    private static final int headerSize = 600;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_edit_profile);
        ButterKnife.bind(this);

        initTitle();
        init();
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.edit_profile));
        titleBar.setRightTvText(getString(R.string.save));
        titleBar.setRightTvVisibility(View.VISIBLE);
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
                saveMemberInfo();
            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    private void init() {
        selectPhotoPopup = new SelectPhotoPopup(this);
        selectGenderPopup = new SelectGenderPopup(this);
        selectGenderPopup.setGenderSelected(1);

        etAbout.addTextChangedListener(etAboutWatcher);
        MemberInfoEntity memberInfoEntity = PaperUtils.getUserInfo();
        if (memberInfoEntity != null) {
            MemberInfoEntity.DataEntity dataEntity = memberInfoEntity.getData();
            if (dataEntity != null) {
                String username = dataEntity.getUsername();
                String aboutMe = dataEntity.getIntroduction();
                String gender = dataEntity.getGender(); //性别1-男，2-女
                String country = dataEntity.getCountry();
                String province = dataEntity.getProvince();
                String city = dataEntity.getCity();

                if (TextUtils.isEmpty(username)) {
                    etUsername.setText(getString(R.string.iman));
                } else {
                    etUsername.setText(username);
                }
                if (TextUtils.isEmpty(aboutMe)) {
                    etAbout.setText(getString(R.string.introduction_default));
                } else {
                    etAbout.setText(aboutMe);
                }
                if (!TextUtils.isEmpty(gender)) {
                    if (gender.equals("1")) {
                        curGender = 1;
                        tvGender.setText(getString(R.string.male));
                    } else {
                        curGender = 2;
                        tvGender.setText(getString(R.string.female));
                    }
                } else {
                    curGender = 1;
                    tvGender.setText(getString(R.string.male));
                }

                if (!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city)) {
                    tvLocation.setText(province + "  " + city);
                } else if (TextUtils.isEmpty(province) && TextUtils.isEmpty(city)) {
                    tvLocation.setText(country);
                } else {
                    if (TextUtils.isEmpty(city)) {
                        tvLocation.setText(country + " " + province);
                    } else {
                        tvLocation.setText(country + " " + city);
                    }
                }
            }
        }
    }

    private TextWatcher etAboutWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String content = etAbout.getText().toString().trim();
            if (content.length() > 144) {
                tvBytes.setTextColor(ContextCompat.getColor(EditProfileActivity.this, R.color.main_red));
            } else {
                tvBytes.setTextColor(ContextCompat.getColor(EditProfileActivity.this, R.color.txt_normal));
            }

            tvBytes.setText("(" + content.length() + "/" + 144 + ")");
        }
    };

    @PermissionYes(REQUEST_CODE_PERMISSION_LOCATION)
    private void getPermissionLocationYes(@NonNull List<String> grantedPermissions) {
        Logger.e("位置权限申请成功!");
        goSelectLocationByMap();
    }

    @PermissionNo(REQUEST_CODE_PERMISSION_LOCATION)
    private void getPermissionLocationNo(@NonNull List<String> deniedPermissions) {
        Logger.e("位置权限申请失败!");
    }

    @PermissionYes(REQUEST_CODE_PERMISSION_PHOTO)
    private void getPermissionStorageYes(@NonNull List<String> grantedPermissions) {
        Logger.e("文件操作权限申请成功!");
        showSelectPhoto();
    }

    @PermissionNo(REQUEST_CODE_PERMISSION_PHOTO)
    private void getPermissionStorageNo(@NonNull List<String> deniedPermissions) {
        Logger.e("文件操作权限申请失败!");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (selectPhotoPopup != null) {
            selectPhotoPopup.dismiss();
            selectPhotoPopup = null;
        }
        if (selectGenderPopup != null) {
            selectGenderPopup.dismiss();
            selectGenderPopup = null;
        }
    }

    @OnClick({R.id.rel_location, R.id.rel_gender, R.id.rel_header})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rel_header:
                headerClick();
                break;
            case R.id.rel_gender:
                genderClick();
                break;
            case R.id.rel_location:
                locationClick();
                break;
        }
    }

    //点击性别
    private void genderClick() {
        selectGenderPopup.setOnGenderClickListener(new SelectGenderPopup.OnGenderClickListener() {
            @Override
            public void onGenderClick(int gender) {
                //1: male; 2:female
                Logger.i("--->gender = " + gender);
                curGender = gender;
                if (gender == 1) {
                    tvGender.setText(getString(R.string.male));
                } else {
                    tvGender.setText(getString(R.string.female));
                }
            }
        });
        selectGenderPopup.showAtLocation(llContent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //点击定位
    private void locationClick() {
        AndPermission.with(this)
                .requestCode(REQUEST_CODE_PERMISSION_LOCATION)
                .permission(Permission.LOCATION)
                .callback(this)
                .start();
    }

    //点击头像
    private void headerClick() {
        AndPermission.with(this)
                .requestCode(REQUEST_CODE_PERMISSION_PHOTO)
                .permission(Permission.STORAGE, Permission.CAMERA)
                .callback(this)
                .start();
    }

    //显示选择图片弹框
    private void showSelectPhoto() {
        selectPhotoPopup.setOnClickListener(new SelectPhotoPopup.OnClickListener() {
            @Override
            public void clickCamera() {
                selectPhotoPopup.dismiss();
                if (FileUtils.existSD()) {
                    File fileUri = new File(FileUtils.getImageCache() + "/" + DateUtils.getTimeStamp() + ".jpg");
                    imageUri = Uri.fromFile(fileUri);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        imageUri = FileProvider.getUriForFile(EditProfileActivity.this,
                                DeviceUtils.getAppPackageName(EditProfileActivity.this), fileUri);//通过FileProvider创建一个content类型的Uri

                    //调用系统相机
                    Intent intentCamera = new Intent();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                    }
                    intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    //将拍照结果保存至photo_file的Uri中，不保留在相册中
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intentCamera, REQUEST_CODE_CAMERA);
                }
            }

            @Override
            public void clickAlbum() {
                selectPhotoPopup.dismiss();
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(false)
                        .setShowGif(false)
                        .setPreviewEnabled(true)
                        .start(EditProfileActivity.this, PhotoPicker.REQUEST_CODE);
            }
        });
        selectPhotoPopup.showAtLocation(llContent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //跳转地图获取位置信息
    private void goSelectLocationByMap() {
        UIHelper.jump(this, SelectLocationByMapActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PhotoPicker.REQUEST_CODE: //相册获取图片
                    if (data != null) {
                        ArrayList<String> photos =
                                data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        if (photos != null && photos.size() > 0) {
                            String photoPath = photos.get(0);
                            Logger.i("---->photoPath = " + photoPath);
                            if (!TextUtils.isEmpty(photoPath))
                                cropPhoto(photoPath);
                        }
                    }
                    break;
                case REQUEST_CODE_CAMERA: //拍照获取图片
                    if (imageUri != null) {
                        String photoPath = imageUri.getPath();
                        Logger.i("---->photoPath = " + photoPath);
                        if (!TextUtils.isEmpty(photoPath))
                            cropPhoto(photoPath);
                    }
                    break;
                case UCrop.REQUEST_CROP:
                    final Uri resultUri = UCrop.getOutput(data);
                    Logger.i("---->resultUri =" + resultUri.getPath());
                    String targetDir = FileUtils.getImageCache();
                    //压缩图片
                    Luban.with(this)
                            .load(resultUri.getPath())                                   // 传人要压缩的图片列表
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
                                        curHeaderFile = file;
                                        Logger.i("----->压缩成功 :" + file.getPath());
                                        getQiNiuToken();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    // TODO 当压缩过程出现问题时调用
                                    Logger.i("----->压缩失败");
                                }
                            }).launch();    //启动压缩
                    break;
            }
        }
    }

    //裁剪图片
    private void cropPhoto(String source) {
        Uri sourceUri = Uri.fromFile(new File(source));
        String saveDir = FileUtils.getPortraitCache();
        Uri destinationUri = Uri.fromFile(new File(saveDir, "header.jpg"));
        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(headerSize, headerSize)
                .start(this);
    }

    //获取七牛上传token
    private void getQiNiuToken() {
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ext", "jpg");
        jsonObject.addProperty("width", headerSize + "");
        jsonObject.addProperty("height", headerSize + "");
        jsonArray.add(jsonObject);
        Map<String, String> param = new HashMap<>();
        param.put("category", "1"); //类型:1-用户头像，2-社区，3-post
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
                                    if (curHeaderFile != null && !TextUtils.isEmpty(token)) {
                                        uploadImg(curHeaderFile, key, token);
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

    //上传图片
    private void uploadImg(File data, String key, String token) {
        Configuration config = new Configuration.Builder()
                .zone(AutoZone.autoZone)
                .build();
        UploadManager uploadManager = new UploadManager(config);
        uploadManager.put(data, key, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {
                    Logger.i("--->Upload Success");


                    //                                        Glide.with(EditProfileActivity.this)
//                                                .load(file)
//                                                .dontAnimate()
//                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                                .placeholder(R.mipmap.icon_default_portrait)
//                                                .transform(new GlideCircleTransform(EditProfileActivity.this))
//                                                .centerCrop()
//                                                .into(ivHeader);
                } else {
                    Logger.i("--->Upload Fail" + info.toString());
                    //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                }
                Logger.i("--->" + key + ",\r\n " + info + ",\r\n " + response);
            }
        }, new UploadOptions(null, null, false, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                Logger.i("--->percent : " + percent);
            }
        }, null));
    }

    //保存个人信息
    private void saveMemberInfo() {
        String username = etUsername.getText().toString().trim();
        String introduction = etAbout.getText().toString().trim();

        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("username", username);
        param.put("profile_image", "");
        param.put("gender", curGender + ""); //性别:1-男，2-女
        param.put("location", "");
        param.put("country", "");
        param.put("province", "");
        param.put("city", "");
        param.put("latitude", "");
        param.put("longitude", "");
        param.put("introduction", introduction);
        Call<SaveMemberEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .saveMemberInfo(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<SaveMemberEntity>() {
            @Override
            public void onSuccess(Call<SaveMemberEntity> call, Response<SaveMemberEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<SaveMemberEntity> call, Throwable t) {

            }
        });
    }

}
