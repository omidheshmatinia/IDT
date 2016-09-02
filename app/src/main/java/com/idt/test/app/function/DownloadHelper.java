package com.idt.test.app.function;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.net.URLDecoder;

/**
 * Created by Omid Heshmatinia on 9/1/2016.
 */
public class DownloadHelper {

    Context mContext;
    DownloadManager downloadManager;
    String DOWNLOAD_CACH_LOCATION="";
    long DOWNLOAD_ID=0;
    downloadListener listener;

    public DownloadHelper(Context mContext) {
        this.mContext = mContext;
        DOWNLOAD_CACH_LOCATION=getFullDirectoryPath();
        File mfile=new File(DOWNLOAD_CACH_LOCATION);
        if(!mfile.exists())
            mfile.mkdirs();
    }

    public String getFullDirectoryPath(){
        return mContext.getExternalFilesDir(null).getAbsolutePath()+ getFolder();
    }

    private String getFolder(){
       return "/MyArt/";
    }

    /**
     * check if a file is downloaded before or not
     *
     * @param fileUrl
     * @return
     */
    public Boolean isDownloaded(String fileUrl){
        File mFile = new File(getDownloadedPath(fileUrl));
        if(!mFile.exists())
            return false;
        else {
            if(mFile.canRead())
                return true;
            else {
                mFile.delete();
                return false;
            }
        }
    }

    public String getDownloadedPath(String url){
        return DOWNLOAD_CACH_LOCATION+getFilename(url);
    }

    /**
     * get last part of url as the file name
     * @param fileUrl
     * @return
     */
    private String getFilename(String fileUrl){
        try {
            String[] files = fileUrl.split("/");
            return URLDecoder.decode(files[files.length - 1], "UTF-8");
        } catch (Exception e){
            return "";
        }
    }

    public void downloadFile(String fileUrl){
        try {
            if(!fileUrl.contains("http"))
                fileUrl="http://"+fileUrl;
            downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request rq = new DownloadManager.Request(Uri.parse(fileUrl));
            rq.setDestinationInExternalFilesDir(mContext, getFolder(), getFilename(fileUrl));
            rq.setTitle("Downloading");
            rq.setDescription("Please Wait ...");
            mContext.registerReceiver(br, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            DOWNLOAD_ID = downloadManager.enqueue(rq);
        } catch (IllegalArgumentException e){
            if(listener!=null)
                listener.errorInDownload("Url is Wrong");
        }
    }

    BroadcastReceiver br=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()==DownloadManager.ACTION_DOWNLOAD_COMPLETE){
                mContext.unregisterReceiver(this);
                long dlId=intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
//                if(dlId==DOWNLOAD_ID) {
                    Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(dlId));
                    if(cursor.moveToFirst()){
                        int status=cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        if(status==DownloadManager.STATUS_FAILED){
                            if(listener!=null)
                                listener.errorInDownload("Download Failed");
                        }
                        else if(status==DownloadManager.STATUS_SUCCESSFUL){
                            if(listener!=null)
                                listener.complete();
                        } else {
                            String fileAddress=cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                            File corruptedFile=new File(fileAddress);
                            corruptedFile.delete();
                            if(listener!=null)
                                listener.errorInDownload("File is corrupted");
                        }
                    }
//                }
            }
        }
    };

    public void setListener(downloadListener listener) {
        this.listener = listener;
    }

    public interface downloadListener{
        void complete();
        void errorInDownload(String txt);
    }
}
