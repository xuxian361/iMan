package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by sundy on 17/10/19.
 */

@Data
public class SelectImageEntity implements Serializable {

    private String localPath; //本地图片存放路径
    private String path; //要上传的服务器图片路径

}
