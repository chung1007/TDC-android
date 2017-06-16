package com.pitch.davis.thedavisconnection;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * Created by sam on 6/15/17.
 */

public class PostPage extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postpage);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        image = (ImageView)findViewById(R.id.postImage);
        actionBar.hide();
        Intent intent = getIntent();
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);
        }
    }
    public void postClicked(View view){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginInfo", MODE_PRIVATE);
        EditText locationInput = (EditText)findViewById(R.id.location);
        EditText messageInput = (EditText)findViewById(R.id.postMessage);
        String location = locationInput.getText().toString();
        String message = messageInput.getText().toString();
        String currentTime = Utils.getCurrentTimeStamp();
        Constants.ref.child(currentTime).child("Location").setValue(location);
        Constants.ref.child(currentTime).child("Message").setValue(message);
        Constants.ref.child(currentTime).child("Name").setValue(pref.getString("Name", ""));
        Constants.ref.child(currentTime).child("Contact").setValue(pref.getString("Contact", ""));
        Utils.makeToast(this, "Posted");
        finish();

    }

}
