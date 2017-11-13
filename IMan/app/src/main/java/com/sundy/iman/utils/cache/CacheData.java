package com.sundy.iman.utils.cache;

import android.util.Log;

import com.sundy.iman.config.Apis;
import com.sundy.iman.entity.CommunityInfoEntity;
import com.sundy.iman.entity.CommunityListEntity;
import com.sundy.iman.entity.GetHomeListEntity;
import com.sundy.iman.entity.PostListEntity;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.FileUtils;

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
     * @param dataEntity   每一页数据
     * @param curPage      当前页
     */
    public void saveCommunityPostList(String community_id, PostListEntity.DataEntity dataEntity, int curPage) {
        mCacheUtils.put(userId + "_" + Apis.URL_POST_LIST + "_" + community_id + "_" + curPage, dataEntity);
    }

    /**
     * 获取社区消息列表
     *
     * @param community_id
     * @param curPage
     * @return
     */
    public PostListEntity.DataEntity getCommunityPostList(String community_id, int curPage) {
        return (PostListEntity.DataEntity) mCacheUtils.getSerializable(userId + "_" +
                Apis.URL_POST_LIST + "_" + community_id + "_" + curPage);
    }


}
