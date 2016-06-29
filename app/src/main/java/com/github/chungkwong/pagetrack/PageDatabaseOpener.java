package com.github.chungkwong.pagetrack;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteProgram;

/**
 * Created by kwong on 16-6-27.
 */
public class PageDatabaseOpener extends SQLiteOpenHelper{
    public static final int VERSION=2;
    public static final String TABLE_NAME="pages";
    private static final String DB_NAME="com.github.chungkwong.pagetrack";
    public static final String ID="ID";
    public static final String LAST_MODIFIED="LAST_MODIFIED";
    public static final String URL="URL";
    public PageDatabaseOpener(Context context){
        super(context,DB_NAME,null,VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+"("+
                ID+" integer primary key autoincrement not null,"+
                URL+" text not null,"+
                LAST_MODIFIED+" long not null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion!=newVersion){
            db.execSQL("DROP TABLE "+TABLE_NAME+";");
            onCreate(db);
        }
    }
}
