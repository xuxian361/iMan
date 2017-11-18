package com.sundy.iman.net;

import com.orhanobut.logger.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sundy on 17/9/20.
 */

public abstract class RetrofitCallback<T> implements Callback<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {

        if (response.isSuccessful()) {
            Logger.w(call.request().url().toString());
//            Logger.w(response.body().toString());
            onSuccess(call, response);
            onAfter();
        } else {
//            Logger.w(response.message().toString());
            onFailure(call, new Throwable(response.message()));
            onAfter();
        }
    }

    public abstract void onSuccess(Call<T> call, Response<T> response);

    public abstract void onAfter();

    public void onLoading(long total, long progress) {

    }
}
