package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by sundy on 17/10/19.
 */

@Data
public class SelectedMediaEntity implements Serializable {

    private String type; //1：图片；2：视频
    private String localPath; //本地图片存放路径
    private String path; //要上传的服务器图片路径
    private String localVideoPath; //本地视频资源存放路径

    private double percent; //上传进度

}
