package cn.edu.buaa.a103.reminders;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by zhengxiaohu on 2017/2/25.
 */

public class RemindersDbAdapter {
    //表的列名称
    public static final String COL_ID="_id";
    public static final String COL_CONTENT="content";
    public static final String COL_IMPORTANT="important";

    //对应的列索引
    public static final int INDEX_ID=0;
    public static final int INDEX_CONTENT=INDEX_ID+1;
    public static final int INDEX_IMPORTANT=INDEX_ID+2;

    //used for logging
    public static final String TAG="RemindersDbAdapter";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    public static final String DATABASE_NAME="dba_remdrs";
    public static final String TABLE_NAME="tbl_remdrs";
    public static final int DATABASE_VERSION=1;

    private final Context mContext;

    //SQL statement used to create the database
    public static final String DATABASE_CREATE=
            "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ( "+
                    COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    COL_CONTENT+" TEXT, "+
                    COL_IMPORTANT+" INTEGER );";

    //定义一个用于打开和关闭数据库的SQLite API类DatabaseHelper。它使用context，这是一个Android抽象类，用于提供对Android操作系统
    //的访问。将DatabaseHelper实现为一个内部类
    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        //创建数据库
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG,DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG,"Upgrading database form version "+oldVersion+" to "+newVersion+", which will " +
                    "destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            onCreate(db);
        }
    }

    public RemindersDbAdapter(Context context){
        this.mContext=context;
    }

    //数据库的open方法
    public void open() throws SQLException{
        mDbHelper=new DatabaseHelper(mContext);
        mDb=mDbHelper.getWritableDatabase();
    }

    //数据库的close方法
    public void close(){
        if (mDbHelper!=null){
            mDbHelper.close();
        }
    }

    /**
     * 数据库的CRUD操作；CRUD表示创建，读取，更新，删除
     */

    //CREATE
    public void createReminder(String name,boolean important){
        ContentValues values=new ContentValues();
        values.put(COL_CONTENT,name);
        values.put(COL_IMPORTANT,important ?1:0);
        mDb.insert(TABLE_NAME,null,values);
    }

    //overload to take a reminder
    public long createReminder(Reminder reminder){
        ContentValues values=new ContentValues();
        values.put(COL_CONTENT,reminder.getContent());
        values.put(COL_IMPORTANT,reminder.getImportant());

        //Inserting Row
        return mDb.insert(TABLE_NAME,null,values);
    }

    //READ
    public Reminder fecthReminderById(int id){
        Cursor cursor=mDb.query(TABLE_NAME,new String[]{COL_ID,COL_CONTENT,COL_IMPORTANT},COL_ID+"=?",
                new String[]{String.valueOf(id)},null,null,null,null);
        if (cursor!=null)
            cursor.moveToFirst();

        return new Reminder(cursor.getInt(INDEX_ID),
                cursor.getString(INDEX_CONTENT),
                cursor.getInt(INDEX_IMPORTANT));
    }

    public Cursor fetchAllReminders(){
        Cursor mCursor=mDb.query(TABLE_NAME,new String[]{COL_ID,COL_CONTENT,COL_IMPORTANT},null,null,
                null,null,null);

        if (mCursor!=null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //UPDATE
    public void updateReminder(Reminder reminder){
        ContentValues values=new ContentValues();
        values.put(COL_CONTENT,reminder.getContent());
        values.put(COL_IMPORTANT,reminder.getImportant());
        mDb.update(TABLE_NAME,values,COL_ID+"=?",new String[]{String.valueOf(reminder.getId())});
    }

    //DELETE
    public void deleteRemindersById(int nId){
        mDb.delete(TABLE_NAME,COL_ID+"=?",new String[]{String.valueOf(nId)});
    }

    public void deleteAllReminders(){
        mDb.delete(TABLE_NAME,null,null);
    }
}
