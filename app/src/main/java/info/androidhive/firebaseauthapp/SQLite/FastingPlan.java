package info.androidhive.firebaseauthapp.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FastingPlan extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "mydb.db";
        public static final String TABLE_NAME = "FastingPlan";
        public static final String COL_1 = "ID";
        public static final String COL_2 = "Start_time";
        public static final String COL_3 = "End_time";
        public static final String COL_4 = "Off_day";
        public static final String COL_5 = "Uid";
        public static final String COL_6 = "Now_time";
        public static final String COL_7 = "Day";

        public FastingPlan(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }



    @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,START_TIME LONG,END_TIME LONG,OFF_DAY INTEGER,UID TEXT,NOW_TIME LONG,DAY INTEGER)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            onCreate(db);
        }

        public boolean insertData(long start_time, long end_time, Integer off_day,String uid,long now_time,Integer day) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_2,start_time);
            contentValues.put(COL_3,end_time);
            contentValues.put(COL_4,off_day);
            contentValues.put(COL_5,uid);
            contentValues.put(COL_6,now_time);
            contentValues.put(COL_7,day);
            long result = db.insert(TABLE_NAME,null ,contentValues);
            if(result == -1)
                return false;
            else
                return true;
        }

        public Cursor getAllData() {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
            return res;
        }

        public boolean updateData(Integer id, long start_time, long end_time, Integer off_day,String uid,long now_time,Integer day) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_1,id);
            contentValues.put(COL_2,start_time);
            contentValues.put(COL_3,end_time);
            contentValues.put(COL_4,off_day);
            contentValues.put(COL_5,uid);
            contentValues.put(COL_6,now_time);
            contentValues.put(COL_7,day);
            db.update(TABLE_NAME, contentValues, "ID = ?",new String[] {String.valueOf(id)});
            return true;
        }

        public Integer deleteData (String uid) {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(TABLE_NAME, "Uid = ?",new String[] {uid});
        }
}