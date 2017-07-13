package com.pitch.davis.thedavisconnection;


import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.ThemedSpinnerAdapter;
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
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by sam on 6/19/17.
 */
public class BackgroundService extends Service {
    public static volatile boolean runOnBackground = true;
    int badgeCount = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startFirebaseEventListener();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {return null;}
    @Override
    public boolean onUnbind(Intent intent) {return false;}
    @Override
    public void onRebind(Intent intent) {}

    public void startFirebaseEventListener(){
        Constants.ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String timeStamp = dataSnapshot.getKey();
                File myFile = new File(Constants.posts.getAbsolutePath() + "/" + timeStamp);
                if (!myFile.exists()) {
                    Constants.ref.child(timeStamp).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                JSONObject postData = new JSONObject();
                                postData.put("timeStamp", timeStamp);
                                for (DataSnapshot data2 : dataSnapshot.getChildren()) {
                                    postData.put(data2.getKey(), data2.getValue().toString());
                                }
                                writeToSDcardFile(timeStamp, postData);
                                if(runOnBackground) {
                                    Log.e("App", " is not opened!");
                                    Log.e("postData", postData.toString());
                                    makeNotification(postData.getString("Message"), postData.getString("Title"));
                                }
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
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.e("Post", "REMOVED!");
                final String timeStamp = dataSnapshot.getKey();
                File myFile = new File(Constants.posts.getAbsolutePath() + "/" + timeStamp);
                myFile.delete();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

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

    private void makeNotification(String message, String Title){
        Log.e("Notification", "made");
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        int notifyID = 1;
        android.support.v4.app.NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(Title)
                .setContentText(message)
                .setSmallIcon(R.drawable.eye5);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        mNotifyBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(
                notifyID,
                mNotifyBuilder.build());
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        ShortcutBadger.applyCount(getApplicationContext(), ++badgeCount);

    }

}
