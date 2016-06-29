package com.github.chungkwong.pagetrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwong on 16-6-27.
 */
public class PageManager {
    private final SQLiteDatabase db;
    private final Context context;
    public PageManager(Context context){
        db=new PageDatabaseOpener(context).getWritableDatabase();
        this.context=context;
    }
    public void addPage(Page page){
        ContentValues entry=new ContentValues();
        entry.put(PageDatabaseOpener.URL,page.getURL().toExternalForm());
        entry.put(PageDatabaseOpener.LAST_MODIFIED,page.getLastModified());
        db.insert(PageDatabaseOpener.TABLE_NAME,null,entry);
    }
    public void modifyPage(Page page){
        ContentValues entry=new ContentValues();
        entry.put(PageDatabaseOpener.LAST_MODIFIED,page.getLastModified());
        db.update(PageDatabaseOpener.TABLE_NAME,entry,"URL=?",new String[]{page.getURL().toExternalForm()});
    }
    public void removePage(Page page){
        db.delete(PageDatabaseOpener.TABLE_NAME,"URL=?",new String[]{page.getURL().toExternalForm()});
    }
    public List<Page> getPages(){
        List<Page> pages=new ArrayList<>();
        Cursor found = db.query(PageDatabaseOpener.TABLE_NAME,
                new String[]{PageDatabaseOpener.ID, PageDatabaseOpener.URL, PageDatabaseOpener.LAST_MODIFIED},
                null, null, null, null, PageDatabaseOpener.LAST_MODIFIED);
        found.moveToFirst();
        while(!found.isAfterLast()){
            try {
                pages.add(new Page(new URL(found.getString(1)),found.getLong(2),context));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            found.moveToNext();
        }
        found.close();
        return pages;
    }
    public void close(){
        db.close();
    }

}
