package com.aumum.app.mobile.core.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.aumum.app.mobile.core.dao.gen.ContactRequestEntityDao;
import com.aumum.app.mobile.core.dao.gen.DaoMaster;
import com.aumum.app.mobile.core.dao.gen.GroupRequestEntityDao;
import com.aumum.app.mobile.core.dao.gen.MomentCommentEntityDao;
import com.aumum.app.mobile.core.dao.gen.MomentEntityDao;
import com.aumum.app.mobile.core.dao.gen.MomentLikeEntityDao;
import com.aumum.app.mobile.core.dao.gen.UserEntityDao;
import com.aumum.app.mobile.core.dao.gen.UserInfoEntityDao;

/**
 * Created by Administrator on 12/11/2014.
 */
public class Repository {

    private DaoMaster daoMaster;

    private static final String DB_NAME = "aumum-db";

    public Repository(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
    }

    public void reset() {
        getUserEntityDao().deleteAll();
        getUserInfoEntityDao().deleteAll();
        getContactRequestEntityDao().deleteAll();
        getGroupRequestEntityDao().deleteAll();
        getMomentEntityDao().deleteAll();
        getMomentLikeEntityDao().deleteAll();
        getMomentCommentEntityDao().deleteAll();
    }

    public UserEntityDao getUserEntityDao() {
        return daoMaster.newSession().getUserEntityDao();
    }

    public UserInfoEntityDao getUserInfoEntityDao() {
        return daoMaster.newSession().getUserInfoEntityDao();
    }

    public ContactRequestEntityDao getContactRequestEntityDao() {
        return daoMaster.newSession().getContactRequestEntityDao();
    }

    public GroupRequestEntityDao getGroupRequestEntityDao() {
        return daoMaster.newSession().getGroupRequestEntityDao();
    }

    public MomentEntityDao getMomentEntityDao() {
        return daoMaster.newSession().getMomentEntityDao();
    }

    public MomentLikeEntityDao getMomentLikeEntityDao() {
        return daoMaster.newSession().getMomentLikeEntityDao();
    }

    public MomentCommentEntityDao getMomentCommentEntityDao() {
        return daoMaster.newSession().getMomentCommentEntityDao();
    }
}
