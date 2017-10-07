package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by sundy on 17/10/2.
 */

@Data
public class QiNiuTokenItemEntity implements Serializable {

    private String upload_url; //上传链接
    private String url; //文件地址
    private String path; //存放路径
    private String token; //token
    private String key; //key

}
