package cn.christian.server.utils;

/**
 * Created by Administrator on 2016/9/10.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.common.collect.Lists;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import cn.christian.server.dao.Record;

public class SQLiteHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "records";
    private static final String TABLE_NAME = "record";
    private static final String KEY_ID = "id";
    private static final String KEY_CODE = "code";
    private static final String KEY_DATA = "data";
    private static final String KEY_DATE = "createdate";


    private static final int VERSION = 1;
    private static final String SWORD = "SWORD";

    private Context context;
    //建表语句
    private static final String CREATE_TABLE = "create table" + TABLE_NAME + "(" + KEY_ID
            + " integer primary key autoincrement, " + KEY_CODE + " text not null,  " + KEY_DATA + " text not null," + KEY_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";


    //三个不同参数的构造函数
    //带全部参数的构造函数，此构造函数必不可少
    public SQLiteHelper(Context context, String name, CursorFactory factory,
                        int version) {
        super(context, name, factory, version);

    }

    //带两个参数的构造函数，调用的其实是带三个参数的构造函数
    public SQLiteHelper(Context context, String name) {
        this(context, name, VERSION);
    }

    //带三个参数的构造函数，调用的是带所有参数的构造函数
    public SQLiteHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    //创建数据库
    public void onCreate(SQLiteDatabase db) {
        Log.i(SWORD, "create a table");
        //创建数据库sql语句
        String sql = "CREATE TABLE IF NOT EXISTS record(id integer primary key autoincrement,code varchar(20),data varchar(2048),createdate datetime DEFAULT CURRENT_TIMESTAMP)";
        //执行创建数据库操作
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //创建成功，日志输出提示
        Log.i(SWORD, "update a Database");
    }

    public void addRecord(Record item) {

        SQLiteDatabase db = this.getWritableDatabase();

        //使用ContentValues添加数据
        ContentValues values = new ContentValues();
        values.put(KEY_CODE, item.getCode());
        values.put(KEY_DATA, item.getData());
        values.put(KEY_DATE, item.getCreatedate());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Record> getRecordByDate(Date date) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from record where createdate =? ", new String[]{String.valueOf(date.getTime())});
        List<Record> rets = Lists.newArrayList();
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            return rets;
        }

        do {
            Record item = new Record();
            item.setId(cursor.getInt(0));
            item.setCode(cursor.getString(1));
            item.setData(cursor.getString(2));
            item.setCreatedate(cursor.getInt(3));
            rets.add(item);
        } while (cursor.moveToNext());
        return rets;
    }

    public List<Record> getRecordByInterval(Date begin, Date end) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from record where createdate >? and createdate < ? ", new String[]{String.valueOf(begin.getTime()), String.valueOf(end.getTime())});
        List<Record> rets = Lists.newArrayList();
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            return rets;
        }
        do {
            Record item = new Record();
            item.setId(cursor.getInt(0));
            item.setCode(cursor.getString(1));
            item.setData(cursor.getString(2));
            item.setCreatedate(cursor.getInt(3));
            rets.add(item);
        } while (cursor.moveToNext());
        return rets;
    }

    public Record getRecordById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from record where id = ?", new String[]{String.valueOf(id)});
        Record item = new Record();
        if (cursor.moveToFirst()) {
            item.setId(cursor.getInt(0));
            item.setCode(cursor.getString(1));
            item.setData(cursor.getString(2));
            item.setCreatedate(cursor.getInt(3));
        }
        return item;
    }

    public void deleteAllRecord() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from record");

    }


    public List<Record> getAllRecord() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from record ", null);
        List<Record> rets = Lists.newArrayList();

        if (cursor.getCount() == 0) {
            return rets;
        }
        cursor.moveToFirst();
        do {
            Record item = new Record();
            item.setId(cursor.getInt(0));
            item.setCode(cursor.getString(1));
            item.setData(cursor.getString(2));
            item.setCreatedate(cursor.getInt(3));
            rets.add(item);
        } while (cursor.moveToNext());
        return rets;
    }


}

