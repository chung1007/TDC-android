package com.pitch.davis.thedavisconnection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sam on 6/15/17.
 */

public class Homepage extends AppCompatActivity {

    ListView postList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        postList = (ListView)findViewById(R.id.postList);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        Intent intent = getIntent();
        isStoragePermissionGranted();
        immediateUpdate();
        updatePostList();
    }

    //TODO clickable toast for next posts and go to the top.

    public void immediateUpdate(){
        PostListAdapter adapter = new PostListAdapter(getApplicationContext(), Utils.getFiles());
        postList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void postClicked(View view){
        Intent postPage = new Intent(this, PostPage.class);
        startActivity(postPage);
    }

    public void updatePostList(){
        Timer timer = new Timer();
        timer.schedule(new updateList(), 0, 30000);
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission", "Permission is granted");
                Utils.startFirebaseListener();
                return true;
            } else {
                Log.e("Permission","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            Log.e("Permission","Permission is granted");
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.e("Permission","Permission: "+permissions[0]+ "was "+grantResults[0]);
            Utils.startFirebaseListener();
        }
    }
    public class updateList extends TimerTask {

        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int index = postList.getFirstVisiblePosition();
                    View v = postList.getChildAt(0);
                    int top = (v == null) ? 0 : v.getTop();
                    PostListAdapter adapter = new PostListAdapter(getApplicationContext(), Utils.getFiles());
                    postList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    postList.setSelectionFromTop(index, top);

                }
            });

        }

    }


}
