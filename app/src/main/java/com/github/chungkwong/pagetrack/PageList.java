package com.github.chungkwong.pagetrack;

import android.content.Intent;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.AsyncListUtil;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Vector;

public class PageList extends AppCompatActivity implements ListAdapter{
    private ListView pages;
    private Button URLadd,URLrefresh;
    private EditText URLin;
    private PageManager db;
    private Vector<Page> items;
    private DataSetObservable observers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_list);
        URLadd=(Button)findViewById(R.id.URLadd);
        URLrefresh=(Button)findViewById(R.id.URLrefresh);
        URLin=(EditText)findViewById(R.id.URLin);
        pages=(ListView)findViewById(R.id.pages);
        db=new PageManager(getApplicationContext());
        items=new Vector<>();
        observers=new DataSetObservable();
        initRefresh();
        initURLadd();
        initPages();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
    private void initRefresh(){
        URLrefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshPages();
            }
        });
    }
    private void initURLadd(){
        URLadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Page page=new Page(new URL(URLin.getText().toString()),getApplicationContext());
                    db.addPage(page);
                    addItem(page);
                    refreshPage(page);
                } catch (MalformedURLException e) {
                    new AlertDialog.Builder(getApplicationContext()).
                            setMessage(R.string.invalid_URL).show();
                    e.printStackTrace();
                }
            }
        });
    }
    private void initPages(){
        pages.setAdapter(this);
        for(final Page page:db.getPages()) {
            addItem(page);
        }
        refreshPages();
    }
    private void refreshPage(final Page page){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if(page.updateLastModified(PageList.this))
                    db.modifyPage(page);
            }
        });
    }
    private void refreshPages(){
        for(Page page:items) {
            refreshPage(page);
        }
    }
    public void addItem(final Page page){
        items.add(page);
        page.setClickable(true);
        page.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                db.removePage(page);
                removeItem(page);
                return true;
            }
        });
        page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(page.getURL().toExternalForm()));
                startActivity(intent);

            }
        });
        observers.notifyChanged();
        observers.notifyInvalidated();
    }
    public void removeItem(Page page){
        items.remove(page);
        observers.notifyChanged();
        observers.notifyInvalidated();
    }
    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        observers.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        observers.unregisterObserver(observer);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Page getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return items.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }
}
