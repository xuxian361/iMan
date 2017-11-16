package com.sundy.iman.utils.cache;

import android.util.Log;

import com.sundy.iman.config.Apis;
import com.sundy.iman.entity.BillRecordListEntity;
import com.sundy.iman.entity.CommunityInfoEntity;
import com.sundy.iman.entity.CommunityListEntity;
import com.sundy.iman.entity.ContactListEntity;
import com.sundy.iman.entity.GetHomeListEntity;
import com.sundy.iman.entity.GetPostInfoEntity;
import com.sundy.iman.entity.LastPostListEntity;
import com.sundy.iman.entity.MemberInfoEntity;
import com.sundy.iman.entity.MyPromoteCommunityListEntity;
import com.sundy.iman.entity.NearbyPostListEntity;
import com.sundy.iman.entity.TagListEntity;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.FileUtils;
import com.sundy.iman.utils.cache.beans.CommunityPostListCacheBean;
import com.sundy.iman.utils.cache.beans.MyPostListCacheBean;

import java.io.File;

/**
 * 保存缓存 读取缓存
 * Created by sundy on 17/11/13.
 */

public class CacheData {
    private final static String TAG = "CacheData";

    private static final CacheData ourInstance = new CacheData();

    private CacheUtils mCacheUtils;
    private final String cachePath = FileUtils.getHttpCache();
    private final File cacheFile = new File(cachePath);
    private String userId = "";

    private CacheData() {
        if (mCacheUtils == null) {
            Log.e("TAG", "---->缓存文件路径: " + cacheFile.getPath());
            userId = PaperUtils.getMId();
            mCacheUtils = CacheUtils.getInstance(cacheFile);
        }
    }

    public static CacheData getInstance() {
        return ourInstance;
    }

    /**
     * 保存首页列表数据
     *
     * @param getHomeListEntity
     */
    public void saveHomeList(GetHomeListEntity getHomeListEntity) {
        mCacheUtils.put(userId + "_" + Apis.URL_HOME_GET_LIST, getHomeListEntity);
    }

    /**
     * 获取首页列表数据
     */
    public GetHomeListEntity getHomeList() {
        return (GetHomeListEntity) mCacheUtils.getSerializable(userId + "_" + Apis.URL_HOME_GET_LIST);
    }

    /**
     * 保存附近消息列表
     *
     * @param dataEntity 每一页数据
     * @param curPage    当前页
     */
    public void saveNearbyPostList(NearbyPostListEntity.DataEntity dataEntity, int curPage) {
        mCacheUtils.put(userId + "_" + Apis.URL_GET_NEARBY_POST + "_" + curPage, dataEntity);
    }

    /**
     * 获取附近消息列表
     *
     * @param curPage 当前页
     * @return
     */
    public NearbyPostListEntity.DataEntity getNearbyPostList(int curPage) {
        return (NearbyPostListEntity.DataEntity) mCacheUtils.getSerializable(userId + "_" +
                Apis.URL_GET_NEARBY_POST + "_" + curPage);
    }

    /**
     * 保存社区详情
     *
     * @param dataEntity
     */
    public void saveCommunityInfo(String community_id, CommunityInfoEntity.DataEntity dataEntity) {
        mCacheUtils.put(userId + "_" + Apis.URL_GET_COMMUNITY_INFO + "_" + community_id, dataEntity);
    }

    /**
     * 获取社区详情
     *
     * @return
     */
    public CommunityInfoEntity.DataEntity getCommunityInfo(String community_id) {
        return (CommunityInfoEntity.DataEntity) mCacheUtils.getSerializable(userId + "_" +
                Apis.URL_GET_COMMUNITY_INFO + "_" + community_id);
    }

    /**
     * 保存我的社区列表
     *
     * @param dataEntity 每一页数据
     * @param curPage    当前页
     */
    public void saveMyCommunityList(CommunityListEntity.DataEntity dataEntity, int curPage) {
        mCacheUtils.put(userId + "_" + Apis.URL_COMMUNITY_LIST + "_" + curPage, dataEntity);
    }

    /**
     * 获取我的社区列表
     *
     * @param curPage 当前页
     * @return
     */
    public CommunityListEntity.DataEntity getMyCommunityList(int curPage) {
        return (CommunityListEntity.DataEntity) mCacheUtils.getSerializable(userId + "_" +
                Apis.URL_COMMUNITY_LIST + "_" + curPage);
    }

    /**
     * 保存社区消息列表
     *
     * @param community_id 社区ID
     */
    public void saveCommunityPostList(String community_id, CommunityPostListCacheBean cacheBean) {
        mCacheUtils.put(userId + "_" + Apis.URL_POST_LIST + "_" + community_id, cacheBean);
    }

    /**
     * 获取社区消息列表
     *
     * @param community_id
     * @return
     */
    public CommunityPostListCacheBean getCommunityPostList(String community_id) {
        return (CommunityPostListCacheBean) mCacheUtils.getSerializable(userId + "_" + Apis.URL_POST_LIST + "_" + community_id);
    }

    /**
     * 保存最新消息列表
     *
     * @param dataEntity 每一页的数据
     * @param curPage    当前页
     */
    public void saveLatestPostList(LastPostListEntity.DataEntity dataEntity, int curPage) {
        mCacheUtils.put(userId + "_" + Apis.URL_GET_LAST_POST + "_" + curPage, dataEntity);
    }

    /**
     * 获取最新消息列表
     *
     * @param curPage 当前页
     * @return
     */
    public LastPostListEntity.DataEntity getLatestPostList(int curPage) {
        return (LastPostListEntity.DataEntity) mCacheUtils.getSerializable(userId + "_" +
                Apis.URL_GET_LAST_POST + "_" + curPage);
    }

    /**
     * 保存我的联系人列表
     *
     * @param dataEntity 每一页数据
     * @param curPage    当前页
     */
    public void saveMyContactsList(ContactListEntity.DataEntity dataEntity, int curPage) {
        mCacheUtils.put(userId + "_" + Apis.URL_GET_CONTACT_LIST + "_" + curPage, dataEntity);
    }

    /**
     * 获取我的联系人列表
     *
     * @param curPage 当前页
     * @return
     */
    public ContactListEntity.DataEntity getMyContactList(int curPage) {
        return (ContactListEntity.DataEntity) mCacheUtils.getSerializable(userId + "_" +
                Apis.URL_GET_CONTACT_LIST + "_" + curPage);
    }

    /**
     * 保存交易记录
     *
     * @param dataEntity
     * @param yearMonth
     * @param curPage
     */
    public void saveBillRecordList(BillRecordListEntity.DataEntity dataEntity, String yearMonth, int curPage) {
        mCacheUtils.put(userId + "_" + Apis.URL_GET_BILL_RECORD + "_" + yearMonth + "_" + curPage, dataEntity);
    }

    /**
     * 获取交易记录
     *
     * @param yearMonth
     * @param curPage
     * @return
     */
    public BillRecordListEntity.DataEntity getBillRecordList(String yearMonth, int curPage) {
        return (BillRecordListEntity.DataEntity) mCacheUtils.getSerializable(userId + "_" +
                Apis.URL_GET_BILL_RECORD + "_" + yearMonth + "_" + curPage);
    }

    /**
     * 保存标签列表
     *
     * @param dataEntity
     */
    public void saveTagsList(TagListEntity.DataEntity dataEntity) {
        mCacheUtils.put(userId + "_" + Apis.URL_TAG_GET_LIST, dataEntity);
    }

    /**
     * 获取标签列表
     *
     * @return
     */
    public TagListEntity.DataEntity getTagsList() {
        return (TagListEntity.DataEntity) mCacheUtils.getSerializable(userId + "_" +
                Apis.URL_TAG_GET_LIST);
    }

    /**
     * 保存推广社区列表
     *
     * @param dataEntity 每一页数据
     * @param curPage    当前页
     */
    public void savePromoteCommunityList(MyPromoteCommunityListEntity.DataEntity dataEntity, int curPage) {
        mCacheUtils.put(userId + "_" + Apis.URL_GET_MY_PROMOTE_COMMUNITY_LIST + "_" + curPage, dataEntity);
    }

    /**
     * 获取推广社区列表
     *
     * @param curPage 当前页
     * @return
     */
    public MyPromoteCommunityListEntity.DataEntity getPromoteCommunityList(int curPage) {
        return (MyPromoteCommunityListEntity.DataEntity) mCacheUtils.getSerializable(userId + "_" +
                Apis.URL_GET_MY_PROMOTE_COMMUNITY_LIST + "_" + curPage);
    }

    /**
     * 保存联系人信息
     *
     * @param profile_id 联系人ID
     * @param dataEntity 联系人信息
     */
    public void saveContactInfo(String profile_id, MemberInfoEntity.DataEntity dataEntity) {
        mCacheUtils.put(userId + "_" + Apis.URL_GET_MEMBER_INFO + "_" + profile_id, dataEntity);
    }

    /**
     * 获取联系人信息
     *
     * @param profile_id 联系人ID
     * @return
     */
    public MemberInfoEntity.DataEntity getContactInfo(String profile_id) {
        return (MemberInfoEntity.DataEntity) mCacheUtils.getSerializable(userId + "_" +
                Apis.URL_GET_MEMBER_INFO + "_" + profile_id);
    }

    /**
     * 保存我的Post 列表
     */
    public void saveMyPostList(MyPostListCacheBean bean) {
        mCacheUtils.put(userId + "_" + Apis.URL_POST_LIST, bean);
    }

    /**
     * 清除我的Post 列表
     */
    public boolean removeMyPostList() {
        return mCacheUtils.remove(userId + "_" + Apis.URL_POST_LIST);
    }

    /**
     * 获取我的Post 列表
     *
     * @return
     */
    public MyPostListCacheBean getMyPostList() {
        return (MyPostListCacheBean) mCacheUtils.getSerializable(userId + "_" + Apis.URL_POST_LIST);
    }

    /**
     * 保存Post 详情
     *
     * @param dataEntity
     * @param post_id    postID
     * @param creator_id 创建者ID
     */
    public void savePostInfo(GetPostInfoEntity.DataEntity dataEntity, String post_id, String creator_id) {
        mCacheUtils.put(userId + "_" + Apis.URL_GET_POST_INFO + "_" + post_id + "_" + creator_id, dataEntity);
    }

    /**
     * 获取Post 详情
     *
     * @param post_id    postID
     * @param creator_id 创建者ID
     * @return
     */
    public GetPostInfoEntity.DataEntity getPostInfo(String post_id, String creator_id) {
        return (GetPostInfoEntity.DataEntity) mCacheUtils.getSerializable(userId + "_" +
                Apis.URL_GET_POST_INFO + "_" + post_id + "_" + creator_id);
    }


}
