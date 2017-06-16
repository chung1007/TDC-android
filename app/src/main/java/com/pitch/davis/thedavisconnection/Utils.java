package com.pitch.davis.thedavisconnection;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

/**
 * Created by sam on 6/15/17.
 */
public class Utils {

    public static void makeToast(Context context, String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
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
                                    }catch (JSONException JE){
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
                    public void onCancelled(DatabaseError databaseError) {}
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

    public static void imageUpload(ImageView imageView, String Name, String timestamp){
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = Constants.storageRef.child(Name).child(timestamp).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }
    public static List<String> getFiles(){
        File [] files = Constants.posts.listFiles();
        List<String> fileNames = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            fileNames.add(files[i].getName());
        }
        Collections.reverse(fileNames);
        return fileNames;
    }
    public static String readFile(String name) {
        BufferedReader file;
        try {
            file = new BufferedReader(new InputStreamReader(new FileInputStream(
                    new File(name))));
        } catch (IOException ioe) {
            Log.e("File Error", "Failed To Open File");
            return null;
        }
        String dataOfFile = "";
        String buf;
        try {
            while ((buf = file.readLine()) != null) {
                dataOfFile = dataOfFile.concat(buf + "\n");
            }
        } catch (IOException ioe) {
            Log.e("File Error", "Failed To Read From File");
            return null;
        }
        return dataOfFile;
    }
    public static Object[] reverse(Object[] arr) {
        List<Object> list = Arrays.asList(arr);
        Collections.reverse(list);
        return list.toArray();
    }
}
