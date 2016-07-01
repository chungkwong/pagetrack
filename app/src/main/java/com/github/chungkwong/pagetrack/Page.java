package com.github.chungkwong.pagetrack;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.UiThread;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;

/**
 * Created by kwong on 16-6-27.
 */
public class Page extends TextView{
    private final URL url;
    private String MD5;
    private long lastModified,oldLastModified;
    private static final String ZEROS="00000000000000000000000000000000";
    public Page(URL url, Context context) {
        this(url,ZEROS,0,0,context);
    }
    public Page(URL url,String MD5,long lastModified,long oldLastModified, Context context) {
        super(context);
        this.url=url;
        this.MD5=MD5;
        this.lastModified=lastModified;
        this.oldLastModified=oldLastModified;
        setTextColor(Color.BLUE);
        updateText();
    }
    private String updateHash() throws IOException, NoSuchAlgorithmException {
        InputStream in=url.openStream();
        MessageDigest digester = MessageDigest.getInstance("MD5");
        byte[] bytes = new byte[8192];
        int byteCount;
        while ((byteCount = in.read(bytes)) > 0) {
            digester.update(bytes, 0, byteCount);
        }
        in.close();
        StringBuilder buf=new StringBuilder();
        for(byte b:digester.digest())
            buf.append(Integer.toHexString(b));
        return buf.toString();
    }
    public boolean updateLastModified(PageList activity){
        try {
            String newMD5=updateHash();
            if(!newMD5.equals(MD5)){
                MD5=newMD5;
                oldLastModified=lastModified;
                lastModified=System.currentTimeMillis();
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
        } catch (NoSuchAlgorithmException e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setBackgroundColor(Color.BLACK);
                }
            });
            return false;
        }
    }
    private void updateText(){
        StringBuilder buf=new StringBuilder();
        buf.append(url.toExternalForm()).append('\n');
        if(lastModified!=0) {
            if(oldLastModified!=0){
                buf.append(getContext().getString(R.string.after));
                buf.append(DateFormat.getDateTimeInstance().format(new Date(oldLastModified))).append('\n');
            }
            buf.append(getContext().getString(R.string.before));
            buf.append(DateFormat.getDateTimeInstance().format(new Date(lastModified)));
        }else {
            buf.append(getContext().getString(R.string.last_modified)).append(':');
            buf.append(getContext().getString(R.string.unknown));
        }
        setText(buf.toString());
    }
    public long getLastModified() {
        return lastModified;
    }
    public long getOldLastModified() {
        return oldLastModified;
    }
    public String getMD5() {
        return MD5;
    }
    public URL getURL() {
        return url;
    }
    @Override
    public String toString() {
        return super.toString();
    }
}
