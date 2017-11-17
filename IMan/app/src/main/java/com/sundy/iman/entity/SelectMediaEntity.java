package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by sundy on 17/10/24.
 */
@Data
public class SelectMediaEntity implements Serializable {

    private String localImagePath; //本地图片存放路径
    private String localVideoPath; //本地视频路径
    private String path; //要上传的服务器媒体资源路径

    private String url; //上传后保存的文件路径（完整的七牛文件路径）

}

