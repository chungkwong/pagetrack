package com.github.chungkwong.pagetrack;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.UiThread;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;

/**
 * Created by kwong on 16-6-27.
 */
public class Page extends TextView{
    private final URL url;
    private long lastModified;
    public Page(URL url, Context context) {
        this(url,0,context);
    }
    public Page(URL url,long lastModified, Context context) {
        super(context);
        this.url = url;
        this.lastModified=lastModified;
        updateText();
    }
    public long getLatestLastModifed()throws IOException{
        return url.openConnection().getLastModified();
    }
    public boolean updateLastModified(PageList activity){
        try {
            long newLastModified = getLatestLastModifed();
            if(newLastModified>lastModified){
                lastModified=newLastModified;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setBackgroundColor(Color.YELLOW);
                        updateText();
                    }
                });
                return true;
            }else{
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setBackgroundColor(Color.GREEN);
                    }
                });
                return false;
            }
        } catch (IOException e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setBackgroundColor(Color.RED);
                }
            });
            return false;
        }
    }
    private void updateText(){
        StringBuilder buf=new StringBuilder();
        buf.append(url.toExternalForm()).append('\n');
        buf.append(getContext().getString(R.string.last_modified)).append(':');
        if(lastModified!=0)
            buf.append(DateFormat.getDateTimeInstance().format(new Date(lastModified)));
        else
            buf.append(getContext().getString(R.string.unknown));
        setText(buf.toString());
    }
    public long getLastModified() {
        return lastModified;
    }
    public URL getURL() {
        return url;
    }
    @Override
    public String toString() {
        return super.toString();
    }
}
