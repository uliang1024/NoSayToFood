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
    public static final String COL_8 = "TS";

    public FastRecord(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,UID TEXT,KG FLOAT,HEIGHT FLOAT,WAISTLINE FLOAT,BODYFAT FLOAT,DATE TEXT,TS INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String uid, String startdate,String enddate,Long timeStamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,uid);
        contentValues.put(COL_3,startdate);
        contentValues.put(COL_4,enddate);
        contentValues.put(COL_8,timeStamp);
        Log.e("body data inserted :","ID:"+uid+"startDate:"+startdate+"endDate:"+enddate+"ts:"+timeStamp);

        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" order by TS asc ",null);
        return res;
    }


    public Integer deleteData (String uid) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "Uid = ?",new String[] {uid});
    }
}