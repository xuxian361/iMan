package com.sundy.iman.net;

import com.sundy.iman.config.Apis;
import com.sundy.iman.entity.AppVersionEntity;
import com.sundy.iman.entity.UploadResEntity;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by sundy on 17/9/14.
 */

public interface ApiService {

    /**
     * 获取App 版本
     *
     * @return
     */
    @GET(Apis.URL_GET_APP_VERSION)
    Call<AppVersionEntity> getAppVersion();

    /**
     * 图片上传
     *
     * @param file
     * @return
     */
    @Multipart
    @POST(Apis.URL_UPLOAD_IMAGE)
    Call<UploadResEntity> uploadFile(@Part MultipartBody.Part file);

}
