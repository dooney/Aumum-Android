package com.aumum.app.mobile.core.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.aumum.app.mobile.core.dao.gen.AskingEntityDao;
import com.aumum.app.mobile.core.dao.gen.ContactRequestEntityDao;
import com.aumum.app.mobile.core.dao.gen.DaoMaster;
import com.aumum.app.mobile.core.dao.gen.PartyEntityDao;
import com.aumum.app.mobile.core.dao.gen.UserEntityDao;

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
        getPartyEntityDao().deleteAll();
        getContactRequestEntityDao().deleteAll();
        getAskingEntityDao().deleteAll();
    }

    public UserEntityDao getUserEntityDao() {
        return daoMaster.newSession().getUserEntityDao();
    }

    public PartyEntityDao getPartyEntityDao() {
        return daoMaster.newSession().getPartyEntityDao();
    }

    public ContactRequestEntityDao getContactRequestEntityDao() {
        return daoMaster.newSession().getContactRequestEntityDao();
    }

    public AskingEntityDao getAskingEntityDao() {
        return daoMaster.newSession().getAskingEntityDao();
    }
}
