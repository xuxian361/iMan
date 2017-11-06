package com.sundy.iman.net;

import com.sundy.iman.config.Apis;
import com.sundy.iman.entity.AddContactEntity;
import com.sundy.iman.entity.AppVersionEntity;
import com.sundy.iman.entity.CancelPostEntity;
import com.sundy.iman.entity.ChangeLanguageEntity;
import com.sundy.iman.entity.CollectAdvertisingEntity;
import com.sundy.iman.entity.CommunityInfoEntity;
import com.sundy.iman.entity.CommunityListEntity;
import com.sundy.iman.entity.ContactListEntity;
import com.sundy.iman.entity.CreateAdvertisingEntity;
import com.sundy.iman.entity.CreateCommunityEntity;
import com.sundy.iman.entity.CreatePostEntity;
import com.sundy.iman.entity.DeleteContactEntity;
import com.sundy.iman.entity.DeletePostEntity;
import com.sundy.iman.entity.GetHomeListEntity;
import com.sundy.iman.entity.GetPostInfoEntity;
import com.sundy.iman.entity.JoinCommunityEntity;
import com.sundy.iman.entity.JoinPromoteCommunityEntity;
import com.sundy.iman.entity.LastPostListEntity;
import com.sundy.iman.entity.LoginEntity;
import com.sundy.iman.entity.LogoutEntity;
import com.sundy.iman.entity.MemberInfoEntity;
import com.sundy.iman.entity.MyPromoteCommunityListEntity;
import com.sundy.iman.entity.ParseUrlEntity;
import com.sundy.iman.entity.PostListEntity;
import com.sundy.iman.entity.QiNiuTokenListEntity;
import com.sundy.iman.entity.QuitPromoteCommunityEntity;
import com.sundy.iman.entity.ReportPostEntity;
import com.sundy.iman.entity.SaveMemberEntity;
import com.sundy.iman.entity.ShareInfoEntity;
import com.sundy.iman.entity.StaticContentEntity;
import com.sundy.iman.entity.TagListEntity;
import com.sundy.iman.entity.UpdatePostEntity;
import com.sundy.iman.entity.UpdateTransferPwdEntity;
import com.sundy.iman.entity.UploadResEntity;
import com.sundy.iman.entity.VerificationCodeEntity;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
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
     * 登录
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
    @FormUrlEncoded
    @POST(Apis.URL_UPDATE_TRANSFER_PASSWORD)
    Call<UpdateTransferPwdEntity> updateTransferPwd(@FieldMap Map<String, String> map);

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
    @FormUrlEncoded
    @POST(Apis.URL_SAVE_MEMBER_INFO)
    Call<SaveMemberEntity> saveMemberInfo(@FieldMap Map<String, String> map);

    /**
     * 获取七牛上传token接口
     *
     * @return
     */
    @FormUrlEncoded
    @POST(Apis.URL_GET_QINIU_TOKEN)
    Call<QiNiuTokenListEntity> getQiNiuToken(@FieldMap Map<String, String> map);

    /**
     * 创建社区接口
     *
     * @return
     */
    @FormUrlEncoded
    @POST(Apis.URL_CREATE_COMMUNITY)
    Call<CreateCommunityEntity> createCommunity(@FieldMap Map<String, String> map);

    /**
     * 社区列表接口
     *
     * @return
     */
    @GET(Apis.URL_COMMUNITY_LIST)
    Call<CommunityListEntity> getCommunityList(@QueryMap Map<String, String> map);

    /**
     * 加入或退出社区接口
     *
     * @return
     */
    @GET(Apis.URL_JOIN_COMMUNITY)
    Call<JoinCommunityEntity> joinCommunity(@QueryMap Map<String, String> map);

    /**
     * 获取标签列表
     *
     * @return
     */
    @GET(Apis.URL_TAG_GET_LIST)
    Call<TagListEntity> getTagList(@QueryMap Map<String, String> map);

    /**
     * 创建post接口
     *
     * @return
     */
    @FormUrlEncoded
    @POST(Apis.URL_CREATE_POST)
    Call<CreatePostEntity> createPost(@FieldMap Map<String, String> map);

    /**
     * post列表接口
     *
     * @return
     */
    @GET(Apis.URL_POST_LIST)
    Call<PostListEntity> getPostList(@QueryMap Map<String, String> map);

    /**
     * 创建广告接口
     *
     * @return
     */
    @FormUrlEncoded
    @POST(Apis.URL_CREATE_ADVERTISING)
    Call<CreateAdvertisingEntity> createAdvertising(@FieldMap Map<String, String> map);

    /**
     * 删除post接口
     *
     * @return
     */
    @GET(Apis.URL_DELETE_POST)
    Call<DeletePostEntity> deletePost(@QueryMap Map<String, String> map);

    /**
     * 取消post接口
     *
     * @return
     */
    @GET(Apis.URL_CANCEL_POST)
    Call<CancelPostEntity> cancelPost(@QueryMap Map<String, String> map);

    /**
     * 更新post接口
     *
     * @return
     */
    @FormUrlEncoded
    @POST(Apis.URL_UPDATE_POST)
    Call<UpdatePostEntity> updatePost(@FieldMap Map<String, String> map);

    /**
     * 获取post信息接口
     *
     * @return
     */
    @GET(Apis.URL_GET_POST_INFO)
    Call<GetPostInfoEntity> getPostInfo(@QueryMap Map<String, String> map);

    /**
     * 领取广告奖励接口
     *
     * @return
     */
    @GET(Apis.URL_COLLECT_ADVERTISING)
    Call<CollectAdvertisingEntity> collectAdvertising(@QueryMap Map<String, String> map);

    /**
     * 加入推广社区接口
     *
     * @return
     */
    @GET(Apis.URL_JOIN_PROMOTE_COMMUNITY)
    Call<JoinPromoteCommunityEntity> joinPromoteCommunity(@QueryMap Map<String, String> map);

    /**
     * 退出推广社区接口
     *
     * @return
     */
    @GET(Apis.URL_QUIT_PROMOTE_COMMUNITY)
    Call<QuitPromoteCommunityEntity> quitPromoteCommunity(@QueryMap Map<String, String> map);

    /**
     * 举报post接口
     *
     * @return
     */
    @FormUrlEncoded
    @POST(Apis.URL_REPORT_POST)
    Call<ReportPostEntity> reportPost(@FieldMap Map<String, String> map);

    /**
     * 我的推广社区列表接口
     *
     * @return
     */
    @GET(Apis.URL_GET_MY_PROMOTE_COMMUNITY_LIST)
    Call<MyPromoteCommunityListEntity> getMyPromoteCommunityList(@QueryMap Map<String, String> map);

    /**
     * 社区详情接口
     *
     * @return
     */
    @GET(Apis.URL_GET_COMMUNITY_INFO)
    Call<CommunityInfoEntity> getCommunityInfo(@QueryMap Map<String, String> map);

    /**
     * 首页列表接口
     *
     * @return
     */
    @GET(Apis.URL_HOME_GET_LIST)
    Call<GetHomeListEntity> getHomeList(@QueryMap Map<String, String> map);

    /**
     * 获取App 版本 更新
     *
     * @return
     */
    @GET(Apis.URL_UPDATE_VERSION)
    Call<AppVersionEntity> getAppVersion(@QueryMap Map<String, String> map);

    /**
     * 获取静态内容接口
     *
     * @return
     */
    @GET(Apis.URL_GET_STATIC_CONTENT)
    Call<StaticContentEntity> getStaticContent(@QueryMap Map<String, String> map);

    /**
     * 解析url
     *
     * @return
     */
    @FormUrlEncoded
    @POST(Apis.URL_PARSE_URL)
    Call<ParseUrlEntity> parseUrl(@FieldMap Map<String, String> map);

    /**
     * 获取分享内容
     *
     * @return
     */
    @GET(Apis.URL_GET_SHARE_INFO)
    Call<ShareInfoEntity> getShareInfo(@QueryMap Map<String, String> map);

    /**
     * 添加联系人
     *
     * @return
     */
    @GET(Apis.URL_ADD_CONTACT)
    Call<AddContactEntity> addContact(@QueryMap Map<String, String> map);

    /**
     * 联系人列表
     *
     * @return
     */
    @GET(Apis.URL_GET_CONTACT_LIST)
    Call<ContactListEntity> getContactList(@QueryMap Map<String, String> map);

    /**
     * 删除联系人
     *
     * @return
     */
    @GET(Apis.URL_DELETE_CONTACT)
    Call<DeleteContactEntity> deleteContact(@QueryMap Map<String, String> map);

    /**
     * 获取最新消息列表
     *
     * @return
     */
    @GET(Apis.URL_GET_LAST_POST)
    Call<LastPostListEntity> getLastPostList(@QueryMap Map<String, String> map);



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
