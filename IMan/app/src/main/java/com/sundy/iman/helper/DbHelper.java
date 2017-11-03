package com.sundy.iman.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sundy.iman.MainApp;
import com.sundy.iman.greendao.DaoMaster;
import com.sundy.iman.greendao.DaoSession;
import com.sundy.iman.greendao.ImUserInfo;
import com.sundy.iman.greendao.ImUserInfoDao;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sundy on 17/11/3.
 */

public class DbHelper {

    private static final DbHelper ourInstance = new DbHelper();
    public static boolean ENCRYPTED = true;

    public static DbHelper getInstance() {
        return ourInstance;
    }

    private DaoSession daoSession;
    private ImUserInfoDao userInfoDao;

    private DbHelper() {
        DbOpenHelper helper = new DbOpenHelper(MainApp.getInstance(), ENCRYPTED ? "user-db-encrypted" : "user-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("cncbk-user-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        userInfoDao = daoSession.getImUserInfoDao();
    }


    /**********************************************************************************************************/


    /**
     * 添加用户信息
     *
     * @param entity
     */
    public void addUserInfoEntity(ImUserInfo entity) {
        if (entity != null) {
            entity.setEasemob_account(entity.getEasemob_account());
            if (entity.getId() == null) {
                userInfoDao.save(entity);
            } else {
                userInfoDao.update(entity);
            }
        }
    }

    /**
     * 添加用户列表
     *
     * @param entities
     */
    public void addUserInfoEntityList(List<ImUserInfo> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        List<ImUserInfo> updateList = new ArrayList<>();
        List<ImUserInfo> addList = new ArrayList<>();
        for (ImUserInfo entity : entities) {
            if (entity != null) {
                if (entity.getId() == null) {
                    addList.add(entity);
                } else {
                    updateList.add(entity);
                }
            }
        }
        userInfoDao.saveInTx(addList);
        userInfoDao.updateInTx(updateList);

    }

    /**
     * 通过环信ID 查找用户
     *
     * @param hxId
     * @return
     */
    public ImUserInfo getUserInfoByHxId(String hxId) {
        try {
            Query<ImUserInfo> userInfoQuery = userInfoDao.queryBuilder()
                    .where(ImUserInfoDao.Properties.Easemob_account.eq(hxId)).orderDesc(ImUserInfoDao.Properties.Id).build();
            List<ImUserInfo> result = userInfoQuery.list();
            if (result != null && !result.isEmpty()) {
                return result.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**********************************************************************************************************/

    public static class DbOpenHelper extends DaoMaster.OpenHelper {

        public DbOpenHelper(Context context, String name) {
            super(context, name);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            super.onUpgrade(db, oldVersion, newVersion);
            //在这里更新数据库
        }
    }

    //Getter and Setter
    public static DbHelper getOurInstance() {
        return ourInstance;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
    }

    public ImUserInfoDao getUserInfoDao() {
        return userInfoDao;
    }

    public void setUserInfoDao(ImUserInfoDao userInfoDao) {
        this.userInfoDao = userInfoDao;
    }
}
