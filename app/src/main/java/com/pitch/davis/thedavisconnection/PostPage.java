package com.pitch.davis.thedavisconnection;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.Window;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by sam on 6/15/17.
 */

public class PostPage extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static volatile String timeStamp;
    EditText locationInput;
    EditText messageInput;
    EditText titleInput;
    TextView deletePost;
    AutoCompleteTextView category;
    ImageView imagePreview;
    Boolean editing = false;
    String editingFileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postpage);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        locationInput = (EditText) findViewById(R.id.location);
        messageInput = (EditText) findViewById(R.id.postMessage);
        titleInput = (EditText)findViewById(R.id.postTitle);
        category = (AutoCompleteTextView)findViewById(R.id.categoryList);
        imagePreview = (ImageView)findViewById(R.id.postImage);
        deletePost = (TextView)findViewById(R.id.deleteButton);
        ActionBar actionBar = getSupportActionBar();
        timeStamp = Utils.getCurrentTimeStamp();
        actionBar.hide();
        setCategoryList();
        Intent intent = getIntent();
        editingFileName = intent.getStringExtra("file");
        try {
            if (!editingFileName.equals("")) {
                deletePost.setAlpha(1f);
                editing = true;
                setArchiceInfo(editingFileName);
            }
        }catch (NullPointerException npe){
                editing = false;
        }
    }

    private void dispatchTakePictureIntent() {
        /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }*/
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, REQUEST_IMAGE_CAPTURE);
    }

    public void camClicked(View view){
        dispatchTakePictureIntent();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Uri pickedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            ImageView image = (ImageView)findViewById(R.id.postImage);
            image.setImageBitmap(bitmap);
            String name = MainActivity.pref.getString("Name", "");
            Utils.imageUpload(imagePath, timeStamp, name);
            cursor.close();
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
        if(editing){
            Constants.ref.child(editingFileName).setValue(null);
            Utils.deleteFirebaseImage(pref.getString("Name", "")+"_"+timeStamp);
        }
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
    public void deleteClicked(View view){
        if(editing){
            SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginInfo", MODE_PRIVATE);
            Constants.ref.child(editingFileName).setValue(null);
            Utils.deleteFirebaseImage(pref.getString("Name", "") + "_" + timeStamp);
            Intent goBack = new Intent(this, ArchivePage.class);
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

    public void setArchiceInfo(String fileName){
        try{
            JSONObject JObject = new JSONObject(Utils.readFile(Constants.posts.getAbsolutePath() + "/" + fileName));
            titleInput.setText(JObject.getString("Title"));
            category.setText(JObject.getString("Category"));
            locationInput.setText(JObject.getString("Location"));
            messageInput.setText(JObject.getString("Message"));
            StorageReference ref = Constants.storageRef.child(MainActivity.pref.getString("Name", "") + "_" + fileName);
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(ref)
                    .into(imagePreview);
        }catch (JSONException JE){
            Log.e("JSON ERROR ", "SOMETHING WENT WRONG");
        }
    }

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


