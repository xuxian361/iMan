package com.sundy.iman.net;

import com.orhanobut.logger.Logger;
import com.sundy.iman.config.Apis;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sundy on 17/9/20.
 */

public class RetrofitHelper {

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

    public ApiService getRetrofitServer() {
        return getRetrofit().create(ApiService.class);
    }

    private OkHttpClient getClient() {
        if (okHttpClient == null) {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Logger.w(message);
                }
            });
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);

            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                    .build();
        }
        return okHttpClient;
    }

}
