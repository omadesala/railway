package cn.christian.server.application;

import android.app.Application;

import cn.christian.server.utils.SQLiteHelper;

/**
 * Created by Administrator on 2016/9/10.
 */
public class RailWayApp extends Application {

    private static SQLiteHelper sqlite;

    @Override
    public void onCreate() {
        sqlite = new SQLiteHelper(this, "records");
        super.onCreate();
    }

    public static SQLiteHelper getSqlite() {

        return sqlite;
    }

}
