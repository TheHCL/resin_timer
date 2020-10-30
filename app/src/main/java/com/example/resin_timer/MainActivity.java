package com.example.resin_timer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.content.Context;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public Button start_btn;
    public Button clear_btn;
    public EditText current;
    public EditText wanted;
    public TextView count;
    public CountDownTimer cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start_btn = (Button)findViewById(R.id.start);
        clear_btn = (Button)findViewById(R.id.clear);
        current = (EditText)findViewById(R.id.current);
        wanted = (EditText)findViewById(R.id.wanted);
        count = (TextView)findViewById(R.id.mtext);
        count.setTextSize(40);

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current.getText().toString().matches("")||wanted.getText().toString().matches("")){
                    count.setText("Value missing.");
                }
                else{
                    start_func();
                }


            }
        });

        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current.setEnabled(true);
                wanted.setEnabled(true);
                current.setText("");
                wanted.setText("");
                if (cd!=null){
                    cd.cancel();
                }

                count.setText("");


            }
        });








    }
    public void start_func(){
        current.setEnabled(false);
        wanted.setEnabled(false);
        int cur = Integer.parseInt(current.getText().toString());
        int wan = Integer.parseInt(wanted.getText().toString());

        cur =  (wan-cur)*480000;
        cd =new CountDownTimer(cur, 1000) {

            public void onTick(long millisUntilFinished) {
                millisUntilFinished = millisUntilFinished/1000;
                int hour = (int)millisUntilFinished/3600;
                int min = ((int)millisUntilFinished%3600)/60;
                int sec = ((int)millisUntilFinished%3600)%60;
                count.setText(hour+"hour"+min+"min"+sec+"sec.");
            }

            public void onFinish() {
                count.setText("Times up!");
                Notify("Resin_Timer","Times up");
                flash_times(1);
            }
        }.start();
    }

    public void Notify(String textTitle,String textContent) {
        String CHANNEL_ID ="My fucking channel";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable. clock)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "getString(R.string.channel_name)";
            String description = "getString(R.string.channel_description)";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, builder.build());
    }
    public void flashLightOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);

        } catch (CameraAccessException e) {
        }
    }

    public void flashLightOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
        }
    }
    private void flash_times(int n){
        for (int i =0;i<n;i++){
            try {
                flashLightOn();
                Thread.sleep(200);
                flashLightOff();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
