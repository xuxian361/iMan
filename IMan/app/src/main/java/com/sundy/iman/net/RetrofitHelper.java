package com.sundy.iman.net;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.sundy.iman.config.Apis;
import com.sundy.iman.entity.BaseResEntity;
import com.sundy.iman.entity.UploadResEntity;
import com.sundy.iman.utils.EncryptorUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sundy on 17/9/20.
 */

public class RetrofitHelper {

    public static final long UPLOAD_OUT_TIME = 30000;
    public static final String MJ_SECRET_KEY = "681B9DD3CD857B5A0BF4CBDCCA17D802";
    public static final String MJ_VERSION = "1";
    public static final String MJ_NONESTR = "1SDFA";
    private Retrofit retrofit;
    private OkHttpClient okHttpClient;
    private long outDateTime;
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
                .addInterceptor(new BaseInterceptor(true))
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
                        .addInterceptor(new BaseInterceptor())
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
            }
            return okHttpClient;
        } else {
            return new OkHttpClient.Builder()
                    .addInterceptor(new BaseInterceptor())
                    .connectTimeout(conn, TimeUnit.MILLISECONDS)
                    .readTimeout(read, TimeUnit.MILLISECONDS)
                    .writeTimeout(write, TimeUnit.MILLISECONDS)
                    .build();
        }
    }

    public OkHttpClient getClient() {
        return getClient(-1, -1, -1);
    }

    public String getSign(long timestamp) {
        return getSign(timestamp, MJ_NONESTR, MJ_SECRET_KEY, MJ_VERSION);
    }

    public String getSign(long timestamp, String noneStr, String secretKey, String version) {
        String joinSignStr = timestamp + "=" + noneStr + "=" + secretKey + "=" + version;
        return EncryptorUtils.encryptByMD532(EncryptorUtils.encryptByMD532(joinSignStr));
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

    public class BaseInterceptor implements Interceptor {
        private boolean isOther;

        public BaseInterceptor() {
        }

        public BaseInterceptor(boolean isOther) {
            this.isOther = isOther;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            long timeStamp = System.currentTimeMillis();
            Request request = chain.request();
            HttpUrl originalHttpUrl = request.url();
            if (!isOther) {
                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("req_from", "mj-app")
                        .build();
                Request.Builder requestBuilder = request.newBuilder()
                        .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .addHeader("mj-timestamp", String.valueOf(timeStamp))
                        .addHeader("mj-v", MJ_VERSION)
                        .addHeader("mj-nonestr", MJ_NONESTR)
                        .addHeader("mj-sign", getSign(timeStamp))
                        .url(url);
                Request newRequest = requestBuilder.build();
                Logger.d(newRequest.url());
                Response response = chain.proceed(newRequest);
                try {
                    if (response.code() == 200) {
                        MediaType contentType = response.body().contentType();
                        String bodyString = response.body().string();
                        ResponseBody newBody = ResponseBody.create(contentType, bodyString);
                        response = response.newBuilder().body(newBody).build();
                        Logger.json(bodyString);
                        BaseResEntity resEntity = new Gson().fromJson(bodyString, BaseResEntity.class);
//                        if (resEntity.getStatus() == Constants.CONS_RESPONSE_STATUS_INFO_INVALID) {
//                            //如果是登录过期的话，在这里统一处理
//                            if (System.currentTimeMillis() - outDateTime >= 1000) {
//                                outDateTime = System.currentTimeMillis();
//                                Intent intent = new Intent();
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                Context context = MainApp.getInstance().getApplicationContext();
//                                intent.setClass(context, LoginActivity.class);
//                                context.startActivity(intent);
//                                EventBus.getDefault().post(new LogoutEvent());
//                            }
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            } else {
                Logger.d(request.url());
                return chain.proceed(request);
            }
        }
    }
}
