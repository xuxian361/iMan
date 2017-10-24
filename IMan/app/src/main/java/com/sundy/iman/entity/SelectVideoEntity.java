package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by sundy on 17/10/24.
 */
@Data
public class SelectVideoEntity implements Serializable {

    private String localPath; //本地图片存放路径
    private String path; //要上传的服务器图片路径
    private String localVideoPath; //本地视频路径

}

