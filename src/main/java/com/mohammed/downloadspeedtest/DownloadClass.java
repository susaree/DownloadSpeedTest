package com.mohammed.downloadspeedtest;


import android.content.Context;

import android.util.Log;


import java.io.BufferedInputStream;

import java.io.InputStream;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;


public class DownloadClass extends Thread {
    private static final String TAG = MainActivity.class.getSimpleName();
    private String url;

    public String fileURL = "";
    Context mContext;
    long startTime = 0;
    long endTime = 0;
    double downloadElapsedTime = 0;
    int total = 0;
    double finalDownloadRate = 0.0;
    double instantDownloadRate = 0;
    boolean finished = false;
    int timeout = 15;

    public DownloadClass(Context mContext, String fileURL) {
        this.mContext = mContext;
        this.fileURL = fileURL;
    }





       @Override
       public void run() {
           int count;
           startTime = System.currentTimeMillis();
           try {
               URL url = new URL(fileURL);
               URLConnection connection = url.openConnection();
               connection.connect();
               // getting file length
               int lengthOfFile = connection.getContentLength();

               // input stream to read file - with 8k buffer
               InputStream input = new BufferedInputStream(url.openStream(), 8192);



               byte data[] = new byte[10240];

               total = 0;
               outer:
               while ((count = input.read(data)) != -1) {
                   total += count;
                   endTime = System.currentTimeMillis();
                   downloadElapsedTime = (endTime - startTime) / 1000.0;
                   setInstantDownloadRate(total, downloadElapsedTime);
                   if (downloadElapsedTime >= timeout) {
                       break outer;
                   }
                   // publishing the progress....

                //   Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));


               }

               input.close();


           } catch (Exception e) {
               Log.e("Error: ", e.getMessage());
           }

           endTime = System.currentTimeMillis();
           downloadElapsedTime = (endTime - startTime) / 1000.0;
           finalDownloadRate = ((total * 8) / (1000 * 1000.0)) / downloadElapsedTime;
           System.out.println(finalDownloadRate);
           finished = true;


       }

       private double round(double value, int places) {
            if (places < 0) throw new IllegalArgumentException();

            BigDecimal bd;
            try {
                bd = new BigDecimal(value);
            } catch (Exception ex) {
                return 0.0;
            }
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }

        public double getInstantDownloadRate() {
            return instantDownloadRate;
        }

        public void setInstantDownloadRate(int downloadedByte, double elapsedTime) {

            if (downloadedByte >= 0) {
                instantDownloadRate = round((Double) (((downloadedByte * 8) / (1000 * 1000)) / elapsedTime), 2);
            } else {
                instantDownloadRate = 0.0;
            }
        }

    public double getFinalDownloadRate() {
        return finalDownloadRate;
    }

    public boolean isFinished() {
        return finished;
    }
}
