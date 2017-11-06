package com.sundy.iman.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by sundy on 17/10/3.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GetHomeListEntity extends BaseResEntity {

    private DataEntity data;

    @Data
    public static class DataEntity implements Serializable {
        private NearByEntity near_by;
        private CommunityEntity community_list;
    }

    @Data
    public static class NearByEntity implements Serializable {
        private String total; //总记录数
        private List<NearByItemEntity> list;
    }

    @Data
    public static class NearByItemEntity implements Serializable {
        private String id; //post ID
        private String title; //post 标题
        private String type; //post 类型： 1 - 普通；2 - 广告
        private String create_time;
        private List<CommunityItemBean> community_list;
    }

    @Data
    public static class CommunityItemBean implements Serializable {
        private String id; //社区ID
        private String name; //社区名字
    }

    @Data
    public static class CommunityEntity implements Serializable {
        private String total; //总记录数
        private List<CommunityItem> list;
    }

    @Data
    public static class CommunityItem implements Serializable {
        private String id; //社区ID
        private String name; //社区名字
        private PostInfo post_info;

    }

    @Data
    public static class PostInfo implements Serializable {
        private String id; //post ID
        private String title; //post 标题
        private String type; //post 类型： 1 - 普通；2 - 广告
        private String create_time;
    }

}
