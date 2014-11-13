package com.aumum.app.mobile.core.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.aumum.app.mobile.core.dao.gen.DaoMaster;
import com.aumum.app.mobile.core.dao.gen.MessageVMDao;
import com.aumum.app.mobile.core.dao.gen.UserVMDao;

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

    public MessageVMDao getMessageVMDao() {
        return daoMaster.newSession().getMessageVMDao();
    }

    public UserVMDao getUserVMDao() {
        return daoMaster.newSession().getUserVMDao();
    }
}
