package com.sundy.iman.utils.cache.beans;

import com.sundy.iman.entity.PostItemEntity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Created by sundy on 17/11/16.
 */

@Data
public class MyPostListCacheBean implements Serializable{

    private List<PostItemEntity> list;
}
