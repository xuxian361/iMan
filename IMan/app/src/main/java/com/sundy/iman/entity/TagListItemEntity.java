package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * 标签列表Item 实体
 * Created by sundy on 17/9/26.
 */

@Data
public class TagListItemEntity implements Serializable {

    private String id;
    private String title;

}
