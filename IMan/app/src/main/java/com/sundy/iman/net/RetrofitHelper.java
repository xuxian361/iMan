package com.sundy.iman.net;

import com.sundy.iman.config.Apis;
import com.sundy.iman.entity.UploadResEntity;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sundy on 17/9/20.
 */

public class RetrofitHelper {

    public static final long UPLOAD_OUT_TIME = 30000;
    private Retrofit retrofit;
    private OkHttpClient okHttpClient;
    private static RetrofitHelper ourInstance = new RetrofitHelper();

    public static RetrofitHelper getInstance() {
        return ourInstance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    private RetrofitHelper() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Apis.URL_BASE)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Retrofit getRetrofit(long connectOutTime, long readOutTime, long writeOutTime) {
        return new Retrofit.Builder()
                .baseUrl(Apis.URL_BASE)
                .client(getClient(connectOutTime, readOutTime, writeOutTime))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Retrofit getOtherRetrofit(String baseUrl, long connectOutTime, long readOutTime, long writeOutTime) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOtherClient(connectOutTime, readOutTime, writeOutTime))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public ApiService getRetrofitServer() {
        return getRetrofit().create(ApiService.class);
    }

    private OkHttpClient getOtherClient(long connectOutTime, long readOutTime, long writeOutTime) {
        long conn = connectOutTime <= 0 ? 10000 : connectOutTime;
        long read = readOutTime <= 0 ? 15000 : readOutTime;
        long write = writeOutTime <= 0 ? 15000 : writeOutTime;
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .connectTimeout(conn, TimeUnit.SECONDS)
                .writeTimeout(read, TimeUnit.SECONDS)
                .readTimeout(write, TimeUnit.SECONDS)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();
        return okHttpClient;
    }

    private OkHttpClient getClient(long connectOutTime, long readOutTime, long writeOutTime) {
        long conn = connectOutTime <= 0 ? 10000 : connectOutTime;
        long read = readOutTime <= 0 ? 15000 : readOutTime;
        long write = writeOutTime <= 0 ? 15000 : writeOutTime;
        if (connectOutTime <= 0 && readOutTime <= 0 && writeOutTime <= 0) {
            if (okHttpClient == null) {
                okHttpClient = new OkHttpClient.Builder()
                        .addInterceptor(new LoggingInterceptor())
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
            }
            return okHttpClient;
        } else {
            return new OkHttpClient.Builder()
                    .addInterceptor(new LoggingInterceptor())
                    .connectTimeout(conn, TimeUnit.MILLISECONDS)
                    .readTimeout(read, TimeUnit.MILLISECONDS)
                    .writeTimeout(write, TimeUnit.MILLISECONDS)
                    .build();
        }
    }

    public OkHttpClient getClient() {
        return getClient(-1, -1, -1);
    }

    /**
     * 对图片上传的处理
     *
     * @param file 需要上传的文件
     * @return call
     */
    public Call<UploadResEntity> uploadPicture(File file) {
        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/png"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file1", file.getName(), photoRequestBody);
        Call<UploadResEntity> uploadCall = getRetrofit().create(ApiService.class)
                .uploadFile(part);
        return uploadCall;
    }

    /**
     * 带上传进度的图片上传
     *
     * @param file     需要上传的图片
     * @param callback 回调
     */
    public void uploadPictureProgress(File file, RetrofitCallback<UploadResEntity> callback) {
        RequestBody originalBody = RequestBody.create(MediaType.parse("image/png"), file);
        //通过该行代码将RequestBody转换成特定的FileRequestBody
        FileRequestBody body = new FileRequestBody(originalBody, callback);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file1", file.getName(), body);
        Call<UploadResEntity> call = getRetrofit(UPLOAD_OUT_TIME, UPLOAD_OUT_TIME, UPLOAD_OUT_TIME).create(ApiService.class).uploadFile(part);
        call.enqueue(callback);
    }

}
