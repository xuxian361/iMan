package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sundy.iman.R;
import com.sundy.iman.entity.CreateAdvertisingEntity;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/10/5.
 */

public class CreateAdvertisingActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_create_ad);
    }

    //创建广告
    private void createAd() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("title", ""); //post标题
        param.put("detail", ""); //post详情
        param.put("tags", ""); //标签
        param.put("location", "");
        param.put("latitude", "");
        param.put("longitude", "");
        param.put("communitys", ""); //社区ID:多个社区ID以“,”作为分隔符
        param.put("aging", ""); //时效
        param.put("attachment", ""); //附件 json格式数据:att_type为附件类型，1-图片，2-视频 url：附件存放路径
        Call<CreateAdvertisingEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .createAdvertising(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<CreateAdvertisingEntity>() {
            @Override
            public void onSuccess(Call<CreateAdvertisingEntity> call, Response<CreateAdvertisingEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<CreateAdvertisingEntity> call, Throwable t) {

            }
        });
    }

}
