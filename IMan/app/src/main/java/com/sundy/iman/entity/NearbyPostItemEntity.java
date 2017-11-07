package com.sundy.iman.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Created by sundy on 17/11/6.
 */

@Data
public class NearbyPostItemEntity implements Serializable {

    private String id;
    private String title; //Post 标题
    private String detail; //Post 详情
    private String type; //类型: 1-普通post，2-广告
    private String effective_time; //有效时间
    private String att_num; //附件数
    private String total_amount; //总金额
    private String balance; //剩余金额
    private String par_amount; //每人可得金额
    private String location; //位置
    private String latitude; //纬度
    private String longitude; //经度
    private String tags; //标签
    private String communitys;
    private String creator_id; //创建者ID
    private String is_creator; //是否是作者: 1-是，0-否
    private String create_time; //创建时间
    private String is_collect; //是否领取过奖励: 1-是，0-否
    private String status; //post状态: 1-有效，2-过期，3-取消

    private List<AttachmentEntity> attachment; //附件列表
    private MemberEntity members; //成员列表
    private List<CommunityEntity> community_list;


    @Data
    public static class AttachmentEntity implements Serializable {
        private String id;
        private String att_type; //附件类型: 1-图片，2-视频
        private String url; //附件链接
        private String path; //附件存放路径
        private String thumbnail; //缩略图

    }

    @Data
    public static class MemberEntity implements Serializable {
        private String id;
        private String username; //用户名
        private String profile_image; //头像
        private String easemob_account; //环信账号

    }

    @Data
    public static class CommunityEntity implements Serializable {
        private String id;
        private String name; //社区名字

    }
}
