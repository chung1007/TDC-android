package com.pitch.davis.thedavisconnection;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sam on 6/15/17.
 */

public class Homepage extends AppCompatActivity {

    ListView postList;
    boolean isFiltered = false;
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
        setFilterListener();
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
        Timer timer = new Timer();
        timer.schedule(new updateList(), 0, 30000);

    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission", "Permission is granted");
                startService();
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
            Log.e("Permission", "Permission: " + permissions[0] + "was " + grantResults[0]);
            startService();
        }
    }
    public void setSearchBarListener() {
        final EditText searchBar = (EditText)findViewById(R.id.searchBar);
        setSearchBarClickListener(searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (searchBar.getText().toString().equals("")) {
                    searchBar.setCursorVisible(false);
                    immediateUpdate();
                } else {
                    searchBar.setCursorVisible(true);
                    String s = searchBar.getText().toString();
                    postList.setAdapter(null);
                    PostListAdapter adapter = new PostListAdapter(getApplicationContext(), Utils.getSortedFiles("SEARCH", s), Homepage.this);
                    postList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    public void setFilterListener(){
        LinearLayout LLayout = (LinearLayout)findViewById(R.id.filterLayout);
        int childCount = LLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final TextView filter = (TextView) LLayout.getChildAt(i);
            filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postList.setAdapter(null);
                    PostListAdapter adapter = new PostListAdapter(getApplicationContext(), Utils.getSortedFiles("FILTER", filter.getText().toString()), Homepage.this);
                    postList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    if(((TextView) v).getCurrentTextColor() == Color.BLACK){
                        ((TextView) v).setTextColor(Color.parseColor("#808080"));
                        immediateUpdate();
                        isFiltered = false;

                    }else {
                        for (int i = 0; i < ((LinearLayout) v.getParent()).getChildCount(); i++) {
                            ((TextView) ((LinearLayout) v.getParent()).getChildAt(i)).setTextColor(Color.parseColor("#808080"));
                        }
                        ((TextView) v).setTextColor(Color.parseColor("#000000"));
                        isFiltered = true;
                    }
                }
            });
        }
    }

    public class updateList extends TimerTask {

        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EditText searchBar = (EditText) findViewById(R.id.searchBar);
                    if (searchBar.getText().toString().equals("") && !isFiltered) {
                        int index = postList.getFirstVisiblePosition();
                        View v = postList.getChildAt(0);
                        int top = (v == null) ? 0 : v.getTop();
                        PostListAdapter adapter = new PostListAdapter(getApplicationContext(), Utils.getFiles(), Homepage.this);
                        postList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        postList.setSelectionFromTop(index, top);
                    }
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

    public void startService(){
        Intent i = new Intent(this, BackgroundService.class);
        BackgroundService.runOnBackground = false;
        startService(i);
    }

    @Override
    public void onPause(){
        BackgroundService.runOnBackground = true;
        super.onPause();
    }
    @Override
    public void onStop(){
        BackgroundService.runOnBackground = true;
        super.onStop();
    }

    @Override
    public void onResume(){
        BackgroundService.runOnBackground = false;
        immediateUpdate();
        updatePostList();
        super.onResume();
    }
    @Override
    public void onBackPressed(){
        finishAffinity();
    }
}
