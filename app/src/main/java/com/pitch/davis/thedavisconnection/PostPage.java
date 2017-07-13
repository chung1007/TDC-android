package com.pitch.davis.thedavisconnection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by sam on 6/15/17.
 */

public class PostPage extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static volatile String timeStamp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postpage);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        timeStamp = Utils.getCurrentTimeStamp();
        actionBar.hide();
        setCategoryList();
        Intent intent = getIntent();
        BackgroundService.runOnBackground = false;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void camClicked(View view){
        dispatchTakePictureIntent();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView image = (ImageView)findViewById(R.id.postImage);
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, null, null, null);
            int column_index_data = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToLast();
            String imagePath = cursor.getString(column_index_data);
            String name = MainActivity.pref.getString("Name", "");
            Utils.imageUpload(imagePath, timeStamp, name);
        }
    }

    public void postClicked(View view){
        EditText locationInput = (EditText) findViewById(R.id.location);
        EditText messageInput = (EditText) findViewById(R.id.postMessage);
        EditText titleInput = (EditText)findViewById(R.id.postTitle);
        AutoCompleteTextView category = (AutoCompleteTextView)findViewById(R.id.categoryList);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginInfo", MODE_PRIVATE);
        String location = locationInput.getText().toString();
        String message = messageInput.getText().toString();
        String postTitle = titleInput.getText().toString();
        String categoryChosen = category.getText().toString();
        if(location.equals("") || message.equals("") || postTitle.equals("") || categoryChosen.equals("Options..")) {
            Utils.makeToast(this, "Incomplete!");
        }else {
            Constants.ref.child(timeStamp).child("Location").setValue(location);
            Constants.ref.child(timeStamp).child("Message").setValue(message);
            Constants.ref.child(timeStamp).child("Title").setValue(postTitle);
            Constants.ref.child(timeStamp).child("Name").setValue(pref.getString("Name", ""));
            Constants.ref.child(timeStamp).child("Contact").setValue(pref.getString("Contact", ""));
            Constants.ref.child(timeStamp).child("Category").setValue(categoryChosen);
            Utils.makeToast(this, "Posted");
            Intent goBack = new Intent(this, Homepage.class);
            startActivity(goBack);
            }
        }

   /* public void getCategory(){
        RelativeLayout RLayout = (RelativeLayout)findViewById(R.id.categoryLayout);
        LinearLayout LLayout = (LinearLayout)RLayout.findViewById(R.id.container);
        int childCount = LLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Button category = (Button) LLayout.getChildAt(i);
            if (category.getAlpha() == 1f){
                Constants.ref.child(timeStamp).child("Category").setValue(category.getText().toString());
            }
        }
    }*/

    public void setCategoryList() {
        final AutoCompleteTextView category = (AutoCompleteTextView)findViewById(R.id.categoryList);
        final String[] list= new String[]{"Sale", "Announcement", "Report", "Lost & Found", "Misc."};
        final ListPopupWindow lpw = new ListPopupWindow(this);
        lpw.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list));
        lpw.setAnchorView(category);
        lpw.setModal(true);
        lpw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String item = list[position];
                category.setText(item);
                lpw.dismiss();
            }
        });
        category.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final InputMethodManager imm = (InputMethodManager) PostPage.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                lpw.show();
                return true;
            }
        });
        category.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                final InputMethodManager imm = (InputMethodManager) PostPage.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                lpw.show();
            }
        });
    }

}


