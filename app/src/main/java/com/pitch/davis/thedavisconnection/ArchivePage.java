package com.pitch.davis.thedavisconnection;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

/**
 * Created by sam on 8/12/17.
 */
public class ArchivePage extends AppCompatActivity {

    ListView archiveList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.archive);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent intent = getIntent();
        archiveList = (ListView)findViewById(R.id.archiveList);
        immediateUpdate();
    }

    public void immediateUpdate(){
        PostListAdapter adapter = new PostListAdapter(getApplicationContext(), Utils.getSortedFiles("ARCHIVE", ""), ArchivePage.this);
        archiveList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        Intent goBack = new Intent(this, Homepage.class);
        startActivity(goBack);
    }
}
