package com.pitch.davis.thedavisconnection;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sam on 6/15/17.
 */

public class Homepage extends AppCompatActivity {

    public static Boolean halt = true;
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
        setSearchBarListener();
    }

    public void immediateUpdate(){
        PostListAdapter adapter = new PostListAdapter(getApplicationContext(), Utils.getFiles(), Homepage.this);
        postList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void postClicked(View view){
        Intent postPage = new Intent(this, PostPage.class);
        startActivity(postPage);
    }

    public void updatePostList(){
        if(!halt) {
            Timer timer = new Timer();
            timer.schedule(new updateList(), 0, 30000);
        }
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
            Log.e("Permission", "Permission is granted");
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

    public void setSearchBarListener() {
        final EditText searchBar = (EditText)findViewById(R.id.searchBar);
        setSearchBarClickListener(searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (searchBar.getText().toString().equals("")) {
                    searchBar.setCursorVisible(false);
                    halt = false;
                    immediateUpdate();
                } else {
                    searchBar.setCursorVisible(true);
                    String s  = searchBar.getText().toString();
                    halt = true;
                    postList.setAdapter(null);
                    PostListAdapter adapter = new PostListAdapter(getApplicationContext(), Utils.getSortedFiles(s), Homepage.this);
                    postList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    public class updateList extends TimerTask {

        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int index = postList.getFirstVisiblePosition();
                    View v = postList.getChildAt(0);
                    int top = (v == null) ? 0 : v.getTop();
                    PostListAdapter adapter = new PostListAdapter(getApplicationContext(), Utils.getFiles(), Homepage.this);
                    postList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    postList.setSelectionFromTop(index, top);

                }
            });

        }

    }
    public void setSearchBarClickListener(final EditText searchBar){
        searchBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                searchBar.setCursorVisible(true);
                return false;
            }
        });
    }


}
