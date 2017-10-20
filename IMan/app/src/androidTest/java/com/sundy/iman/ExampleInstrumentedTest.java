package com.sundy.iman;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.sundy.iman.entity.CancelPostEntity;
import com.sundy.iman.entity.CollectAdvertisingEntity;
import com.sundy.iman.entity.DeletePostEntity;
import com.sundy.iman.entity.GetPostInfoEntity;
import com.sundy.iman.entity.UpdatePostEntity;
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

    //删除Post
    private void deletePost() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("post_id", "");
        Call<DeletePostEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .deletePost(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<DeletePostEntity>() {
            @Override
            public void onSuccess(Call<DeletePostEntity> call, Response<DeletePostEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<DeletePostEntity> call, Throwable t) {

            }
        });
    }

    //取消Post
    private void cancelPost() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("post_id", "");
        Call<CancelPostEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .cancelPost(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<CancelPostEntity>() {
            @Override
            public void onSuccess(Call<CancelPostEntity> call, Response<CancelPostEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<CancelPostEntity> call, Throwable t) {

            }
        });
    }

    //更新Post
    private void updatePost() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("title", ""); //post标题
        param.put("detail", ""); //post详情
        param.put("tags", ""); //标签
        param.put("location", "");
        param.put("latitude", "");
        param.put("longitude", "");
        param.put("aging", ""); //时效
        param.put("attachment", ""); //附件 json格式数据:att_type为附件类型，1-图片，2-视频 url：附件存放路径
        param.put("post_id", "");
        Call<UpdatePostEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .updatePost(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<UpdatePostEntity>() {
            @Override
            public void onSuccess(Call<UpdatePostEntity> call, Response<UpdatePostEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<UpdatePostEntity> call, Throwable t) {

            }
        });
    }

    //获取Post 信息
    private void getPostInfo() {
        Map<String, String> param = new HashMap<>();
        param.put("mid", "");
        param.put("session_key", "");
        param.put("post_id", ""); //post id
        param.put("creator_id", ""); //post的作者ID
        Call<GetPostInfoEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getPostInfo(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<GetPostInfoEntity>() {
            @Override
            public void onSuccess(Call<GetPostInfoEntity> call, Response<GetPostInfoEntity> response) {

            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<GetPostInfoEntity> call, Throwable t) {

            }
        });
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
