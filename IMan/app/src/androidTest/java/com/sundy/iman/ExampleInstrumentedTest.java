package com.sundy.iman;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.sundy.iman.entity.CollectAdvertisingEntity;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.sundy.iman", appContext.getPackageName());
    }



    //领取广告奖励
    private void collectAdvertising() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("post_id", ""); //post id
        param.put("creator_id", ""); //post的作者ID
        param.put("community_id", ""); //社区ID
        param.put("income", ""); //奖励金额
        Call<CollectAdvertisingEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .collectAdvertising(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<CollectAdvertisingEntity>() {
            @Override
            public void onSuccess(Call<CollectAdvertisingEntity> call, Response<CollectAdvertisingEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<CollectAdvertisingEntity> call, Throwable t) {

            }
        });
    }




}
