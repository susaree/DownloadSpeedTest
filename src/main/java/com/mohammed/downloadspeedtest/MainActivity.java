package com.mohammed.downloadspeedtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    String url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4";
    private
    static int position = 0;
    static int lastPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        final DecimalFormat dec = new DecimalFormat("#.##");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                new Thread(new Runnable() {

                    RotateAnimation rotate;
                    ImageView speedoStick = findViewById(R.id.speedostick);
                    TextView downloadTextView = (TextView) findViewById(R.id.downloadTextView);



                    @Override
                    public void run() {


                        final DownloadClass downloadClass = new DownloadClass(MainActivity.this, url);
                        Boolean downloadTestStarted = false;
                        Boolean downloadTestFinished = false;

                        while(true){
                            if (!downloadTestStarted) {
                                downloadClass.start();
                                downloadTestStarted = true;
                            }

                            if (downloadTestFinished) {
                                //Failure
                                if (downloadClass.getFinalDownloadRate() == 0) {
                                    System.out.println("Download error...");
                                } else {
                                    //Success
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            downloadTextView.setText(dec.format(downloadClass.getFinalDownloadRate()) + " Mbps");
                                        }
                                    });
                                }
                            } else {
                                //Calc position
                                final double downloadRate = downloadClass.getInstantDownloadRate();
                                position = getPositionByRate(downloadRate);

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        rotate = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                        rotate.setInterpolator(new LinearInterpolator());
                                        rotate.setDuration(100);
                                        speedoStick.startAnimation(rotate);
                                        downloadTextView.setText(dec.format(downloadClass.getInstantDownloadRate()) + " Mbps");


                                    }

                                });
                                lastPosition = position;


                            }
                            if (downloadClass.isFinished()) {
                                downloadTestFinished = true;
                            }

                            if (downloadTestStarted && !downloadTestFinished) {
                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException e) {
                                }
                            } else {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                }
                            }
                        }


                    }

                }).start();






            }

        });


    }

    public int getPositionByRate(double rate) {
        if (rate <= 1) {
            return (int) 30;

        } else if (rate <= 10) {
            return (int)  30;

        } else if (rate <= 30) {
            return (int)90;

        } else if (rate <= 50) {
            return (int)  150;

        } else if (rate <= 100) {
            return (int) 180;

        } else if (rate >= 100) {
            return (int) 200;
        }
        return 0;
    }

}

