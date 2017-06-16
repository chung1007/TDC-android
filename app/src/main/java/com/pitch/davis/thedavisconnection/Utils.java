package com.pitch.davis.thedavisconnection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sam on 6/15/17.
 */
public class Utils {

    public static void makeToast(Context context, String message){
        Toast toast = Toast.makeText(context,message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    public static String getCurrentTimeStamp(){
        String currDate = new SimpleDateFormat("MM-dd-yyyy-HH:mm:ss").format(new Date());
        return currDate;
    }
    public static void startFirebaseListener(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Constants.ref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String timeStamp = dataSnapshot.getKey();
                        File myFile = new File(Constants.posts.getAbsolutePath() + "/" + timeStamp);
                        if (!myFile.exists()) {
                            String values = dataSnapshot.getValue().toString();
                            try {
                                JSONObject postData = new JSONObject();
                                postData.put(timeStamp, values);
                                writeToSDcardFile(timeStamp, postData);
                            }catch (JSONException JE){
                                Log.e("JSON", "ERROR");
                            }
                        }
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
    }
    private static void writeToSDcardFile(String fileName, JSONObject Object) {
        try {
            Constants.posts.mkdir();
            PrintWriter file = new PrintWriter(new FileOutputStream(new File(Constants.posts, (fileName))));
            file.println(Object);
            file.close();
        }catch (IOException IOE){
            Log.e("IO", "EXCEPTION");
        }
    }
}
