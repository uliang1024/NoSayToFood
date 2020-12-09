package info.androidhive.firebaseauthapp.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//用來儲存斷食紀錄
public class FastRecord extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "mydb.db";
    public static final String TABLE_NAME = "FastRecord";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "UID";
    public static final String COL_3 = "STARTDATE";
    public static final String COL_4 = "ENDDATE";
    public static final String COL_5 = "EMOJI";
    public static final String COL_6 = "TS";

    public FastRecord(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,UID TEXT,STARTDATE LONG,ENDDATE LONG,EMOJI INTEGER,TS LONG)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String uid, Long startdate,Long enddate,int emoji,Long timeStamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,uid);
        contentValues.put(COL_3,startdate);
        contentValues.put(COL_4,enddate);
        contentValues.put(COL_5,emoji);
        contentValues.put(COL_6,timeStamp);
        Log.e("body data inserted :","ID:"+uid+"startDate:"+startdate+"endDate:"+enddate+"ts:"+timeStamp);

        long result = db.insert(TABLE_NAME,null ,contentValues);
        return result != -1;
    }
    public boolean updateFastData(Integer id,Long startdate,Long enddate,int emoji) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_3,startdate);
        contentValues.put(COL_4,enddate);
        contentValues.put(COL_5,emoji);
        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] {String.valueOf(id)});
        return true;
    }
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_NAME+" order by TS asc ",null);
    }


//    public Integer deleteData (String uid) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        return db.delete(TABLE_NAME, "Uid = ?",new String[] {uid});
//    }
}