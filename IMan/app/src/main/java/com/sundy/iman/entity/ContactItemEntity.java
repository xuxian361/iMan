package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by sundy on 17/11/6.
 */
@Data
public class ContactItemEntity implements Serializable {

    private String id;
    private String username;
    private String profile_image;
    private String easemob_account;

}
