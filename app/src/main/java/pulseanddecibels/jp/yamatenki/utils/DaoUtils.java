package pulseanddecibels.jp.yamatenki.utils;

import android.content.Context;

import pulseanddecibels.jp.yamatenki.database.Database;
import pulseanddecibels.jp.yamatenki.database.dao.Status;
import pulseanddecibels.jp.yamatenki.database.dao.StatusDao;

/**
 * Created by Diarmaid Lindsay on 2015/11/09.
 * Copyright Pulse and Decibels 2015
 */
public class DaoUtils {
    public static Status getCurrentMountainStatusForMountainId(Context context, long mountainId) {
        StatusDao statusDao = Database.getInstance(context).getStatusDao();
        return statusDao.queryBuilder().where(StatusDao.Properties.MountainId.eq(mountainId)).unique();
    }
}
