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
        private List<MessageItemEntity> list;
    }

    @Data
    public static class CommunityEntity implements Serializable {
        private String total; //总记录数
        private List<MessageItemEntity> list;
    }

    @Data
    public static class MessageItemEntity implements Serializable {
        private String id; //社区ID
        private String name; //社区名字
        private List<PostInfoEntity> post_info;
    }

    @Data
    public static class PostInfoEntity implements Serializable {
        private String id; //post id
        private String title; //post标题
        private String type; //类型: 1-普通post，2-广告
        private String create_time; //创建时间
    }

}
