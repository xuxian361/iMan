package com.sundy.iman.net;

import com.sundy.iman.config.Apis;
import com.sundy.iman.entity.AppVersionEntity;
import com.sundy.iman.entity.ChangeLanguageEntity;
import com.sundy.iman.entity.LoginEntity;
import com.sundy.iman.entity.LogoutEntity;
import com.sundy.iman.entity.MemberInfoEntity;
import com.sundy.iman.entity.SaveMemberEntity;
import com.sundy.iman.entity.TagListEntity;
import com.sundy.iman.entity.UpdateTransferPwdEntity;
import com.sundy.iman.entity.UploadResEntity;
import com.sundy.iman.entity.VerificationCodeEntity;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;

/**
 * Created by sundy on 17/9/14.
 */

public interface ApiService {

    /**
     * 发送验证码
     *
     * @return
     */
    @GET(Apis.URL_SEND_VERIFICATION_CODE)
    Call<VerificationCodeEntity> sendVerificationCode(@QueryMap Map<String, String> map);

    /**
     * 发送验证码
     *
     * @return
     */
    @GET(Apis.URL_LOGIN)
    Call<LoginEntity> login(@QueryMap Map<String, String> map);

    /**
     * 获取个人用户信息
     *
     * @return
     */
    @GET(Apis.URL_GET_MEMBER_INFO)
    Call<MemberInfoEntity> getMemberInfo(@QueryMap Map<String, String> map);

    /**
     * 登出
     *
     * @return
     */
    @GET(Apis.URL_LOGOUT)
    Call<LogoutEntity> logout(@QueryMap Map<String, String> map);

    /**
     * 更新支付密码接口
     *
     * @return
     */
    @GET(Apis.URL_UPDATE_TRANSFER_PASSWORD)
    Call<UpdateTransferPwdEntity> updateTransferPwd(@QueryMap Map<String, String> map);

    /**
     * 修改语言接口
     *
     * @return
     */
    @GET(Apis.URL_CHANGE_LANGUAGE)
    Call<ChangeLanguageEntity> changeLanguage(@QueryMap Map<String, String> map);

    /**
     * 保存个人信息接口
     *
     * @return
     */
    @POST(Apis.URL_SAVE_MEMBER_INFO)
    Call<SaveMemberEntity> saveMemberInfo(@QueryMap Map<String, String> map);


    /**
     * 获取App 版本
     *
     * @return
     */
    @GET(Apis.URL_GET_APP_VERSION)
    Call<AppVersionEntity> getAppVersion(@QueryMap Map<String, String> map);

    /**
     * 图片上传
     *
     * @param file
     * @return
     */
    @Multipart
    @POST(Apis.URL_UPLOAD_IMAGE)
    Call<UploadResEntity> uploadFile(@Part MultipartBody.Part file);

    /**
     * 获取标签列表
     *
     * @return
     */
    @GET(Apis.URL_TAG_GET_LIST)
    Call<TagListEntity> getTagList(@QueryMap Map<String, String> map);
}
