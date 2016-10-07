package cn.christian.server.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.util.Log;

import cn.christian.server.ADSocketService;
import cn.christian.server.MainActivity;
import cn.christian.server.utils.SQLiteHelper;

/**
 * Created by Administrator on 2016/9/10.
 */
public class RailWayApp extends Application {

    private static SQLiteHelper sqlite;

    @Override
    public void onCreate() {
        super.onCreate();
        sqlite = new SQLiteHelper(this, "records");


        String SerialNumber = android.os.Build.SERIAL;
        if (!"cadc4ac6".equals(SerialNumber)) {
            System.exit(0);
        }

        SQLiteDatabase sd = sqlite.getWritableDatabase();
        sd.close();

//        Intent intent = new Intent(this, ADSocketService.class);
//        startService(intent);

//        sqlite.dropTable("record");
//        sqlite.deleteAllRecord();
    }

    public static SQLiteHelper getSqlite() {

        return sqlite;
    }

}
