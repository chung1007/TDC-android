package com.pitch.davis.thedavisconnection;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
        String currDate = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date());
        return currDate;
    }

    public static void imageUpload(String imagePath, String timeStamp, String name){
        try {
            InputStream stream = new FileInputStream(new File(imagePath));
            UploadTask uploadTask = Constants.storageRef.child(name + "_" + timeStamp).putStream(stream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("Pic Upload", "FAILED");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e("Pic Upload", "SUCCESS!");
                }
            });
        }catch (FileNotFoundException FILE){
            Log.e("File", "NOT FOUND");
        }
    }

    public static void checkPostExistance(){
        File [] files = Constants.posts.listFiles();
        for (File file : files) {
            if(!file.getName().equals("")) {
                final String key = file.getName();
                Constants.ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            Log.e("snapshot key", dataSnapshot.getValue().toString());
                        }catch (NullPointerException NPE){
                            File myFile = new File(Constants.posts.getAbsolutePath() + "/" + key);
                            myFile.delete();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        }

    }

    public static List<String> getFiles(){
        File [] files = Constants.posts.listFiles();
        List<String> fileNames = new ArrayList<>();
        try {
            for (int i = 0; i < files.length; i++) {
                if(!files[i].getName().equals("")) {
                    fileNames.add(files[i].getName());
                }
            }
        }catch (NullPointerException npe){
            Log.e("NO", "FILES");
        }
        Collections.reverse(fileNames);
        return fileNames;
    }

    public static List<String> getSortedFiles(String type, String search){
        List<String> files = Utils.getFiles();
        List<String> sortedFiles = new ArrayList<>();
        for (int i = 0; i < files.size(); i++){
            try {
                if (type.equals("SEARCH")){
                    JSONObject JObject = new JSONObject(Utils.readFile(Constants.posts.getAbsolutePath() + "/" + files.get(i)));
                if (JObject.getString("Message").toLowerCase().contains(search.toLowerCase()) || JObject.getString("Name").toLowerCase().contains(search.toLowerCase())) {
                    sortedFiles.add(files.get(i));
                    }
                }else if(type.equals("FILTER")){
                    JSONObject JObject = new JSONObject(Utils.readFile(Constants.posts.getAbsolutePath() + "/" + files.get(i)));
                    if (JObject.getString("Category").equals(search)) {
                        sortedFiles.add(files.get(i));
                    }
                }else if(type.equals("ARCHIVE")) {
                    JSONObject JObject = new JSONObject(Utils.readFile(Constants.posts.getAbsolutePath() + "/" + files.get(i)));
                    if (JObject.getString("Name").equals(MainActivity.pref.getString("Name", ""))) {
                        sortedFiles.add(files.get(i));
                    }
                }
            }catch (JSONException JE){
                Log.e("JSON", "EXCEPTION");
            }

        }
        return sortedFiles;
    }

    public static String getSpecificFile(String message, String Name) {
        List<String> files = Utils.getFiles();
        String fileName = "";
        for (int i = 0; i < files.size(); i++) {
            try {
                JSONObject JObject = new JSONObject(Utils.readFile(Constants.posts.getAbsolutePath() + "/" + files.get(i)));
                if (JObject.getString("Message").equals(message)) {
                    fileName = files.get(i);
                }

            } catch (JSONException JE) {
                Log.e("JSON", "EXCEPTION");
            }

        }
        return fileName;
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

    public static String getCurrentActivity(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // get the info from the currently running task
        List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = taskInfo.get(0).topActivity;
        return taskInfo.get(0).topActivity.getClassName();
    }


}
