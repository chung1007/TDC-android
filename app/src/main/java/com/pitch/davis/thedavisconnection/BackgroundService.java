package com.pitch.davis.thedavisconnection;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by sam on 6/19/17.
 */
public class BackgroundService extends Service {
    private static IBinder mBinder;
    private static boolean mAllowRebind;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startFirebaseEventListener();
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }
    @Override
    public void onRebind(Intent intent) {}
    @Override
    public void onDestroy() {stopSelf();}


    public void startFirebaseEventListener(){
        Constants.ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("Firebase","something was added!");
                final String timeStamp = dataSnapshot.getKey();
                File myFile = new File(Constants.posts.getAbsolutePath() + "/" + timeStamp);
                if (!myFile.exists()) {
                    Constants.ref.child(timeStamp).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.e("data", dataSnapshot.toString());
                            try {
                                JSONObject postData = new JSONObject();
                                postData.put("timeStamp", timeStamp);
                                for (DataSnapshot data2 : dataSnapshot.getChildren()) {
                                    postData.put(data2.getKey(), data2.getValue().toString());
                                }
                                writeToSDcardFile(timeStamp, postData);
                                makeNotification();
                            } catch (JSONException JE) {
                                Log.e("JSON", "EXCEPTION");
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void writeToSDcardFile(String fileName, JSONObject Object) {
        try {
            Constants.posts.mkdir();
            PrintWriter file = new PrintWriter(new FileOutputStream(new File(Constants.posts, (fileName))));
            file.println(Object);
            file.close();
        }catch (IOException IOE){
            Log.e("IO", "EXCEPTION");
        }
    }

    private void makeNotification(){
        NotificationManager mNotificationManager =
                (NotificationManager)this. getSystemService(Context.NOTIFICATION_SERVICE);
        int notifyID = 1;
        android.support.v4.app.NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("TITLE")
                .setContentText("MESSAGE")
                .setSmallIcon(R.drawable.eye5);
        mNotificationManager.notify(
                notifyID,
                mNotifyBuilder.build());
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(400);

    }

}
