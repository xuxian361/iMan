package com.sundy.iman.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by sundy on 17/11/3.
 */
@Entity
public class ImUserInfo {

    @Id
    private Long id;

    @NotNull
    @Unique
    private String easemob_account;
    private String userId;
    private String profile_image;
    private String username;
    private String gender;
    @Generated(hash = 1573850151)
    public ImUserInfo(Long id, @NotNull String easemob_account, String userId,
            String profile_image, String username, String gender) {
        this.id = id;
        this.easemob_account = easemob_account;
        this.userId = userId;
        this.profile_image = profile_image;
        this.username = username;
        this.gender = gender;
    }
    @Generated(hash = 108929661)
    public ImUserInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEasemob_account() {
        return this.easemob_account;
    }
    public void setEasemob_account(String easemob_account) {
        this.easemob_account = easemob_account;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getProfile_image() {
        return this.profile_image;
    }
    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getGender() {
        return this.gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

}
