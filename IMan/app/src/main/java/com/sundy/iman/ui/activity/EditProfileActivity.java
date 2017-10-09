package com.sundy.iman.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import com.sundy.iman.entity.QiNiuTokenListEntity;
import com.sundy.iman.entity.SaveMemberEntity;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
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
    ImageView ivHeader;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.ll_location)
    LinearLayout llLocation;
    @BindView(R.id.et_about)
    EditText etAbout;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.rel_gender)
    RelativeLayout relGender;

    private SelectPhotoPopup selectPhotoPopup;
    private Uri imageUri; //拍照后保存的图片uri
    private SelectGenderPopup selectGenderPopup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_edit_profile);
        ButterKnife.bind(this);

        initTitle();
        init();
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.edit));
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
        selectPhotoPopup = new SelectPhotoPopup(this);
        selectGenderPopup = new SelectGenderPopup(this);
        selectGenderPopup.setGenderSelected(1);
    }

    @PermissionYes(REQUEST_CODE_PERMISSION_LOCATION)
    private void getPermissionLocationYes(@NonNull List<String> grantedPermissions) {
        Logger.e("位置权限申请成功!");

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

    @OnClick({R.id.iv_header, R.id.ll_location, R.id.btn_save, R.id.rel_gender})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_header:
                headerClick();
                break;
            case R.id.rel_gender:
                genderClick();
                break;
            case R.id.ll_location:
                locationClick();
                break;
            case R.id.btn_save:
                saveMemberInfo();
                break;
        }
    }

    //点击性别
    private void genderClick() {
        selectGenderPopup.setOnGenderClickListener(new SelectGenderPopup.OnGenderClickListener() {
            @Override
            public void onGenderClick(int gender) {
                Logger.i("--->gender = " + gender);
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
                                    Logger.i("----->压缩成功 :" + file.getPath());

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
                .withMaxResultSize(600, 600)
                .start(this);
    }

    //获取七牛上传token
    private void getQiNiuToken() {
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ext", "jpg");
        jsonObject.addProperty("width", "200");
        jsonObject.addProperty("height", "200");
        jsonArray.add(jsonObject);
        Map<String, String> param = new HashMap<>();
        param.put("category", "1"); //类型:1-用户头像，2-社区，3-post
        param.put("upload_params", jsonArray.toString()); //上传参数:json格式的数据:ext-上传文件的格式，width-上传文件的宽度，height-上传文件的高度
        Call<QiNiuTokenListEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getQiNiuToken(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<QiNiuTokenListEntity>() {
            @Override
            public void onSuccess(Call<QiNiuTokenListEntity> call, Response<QiNiuTokenListEntity> response) {

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
                } else {
                    Logger.i("--->Upload Fail");
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
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("username", "");
        param.put("profile_image", "");
        param.put("gender", ""); //性别:1-男，2-女
        param.put("location", "");
        param.put("latitude", "");
        param.put("longitude", "");
        param.put("introduction", "");
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
