package com.pitch.davis.thedavisconnection;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sam on 6/16/17.
 */
public class CustomPostBox extends RelativeLayout {
    private TextView location;
    private TextView Name;
    private TextView postMessage;
    private ImageView eye;
    private TextView contact;
    Context context;


    public CustomPostBox(Context context, ArrayList<Object> postInfo) {
        super(context);
        this.context = context;
        init();
        setInfo(postInfo);

    }
    //String timestamp, String location, String Name, String message, String contact

    public CustomPostBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomPostBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.post_box, this);
        this.location = (TextView)findViewById(R.id.location);
        this.Name = (TextView)findViewById(R.id.Name);
        this.postMessage = (TextView)findViewById(R.id.postMessage);
        this.contact = (TextView)findViewById(R.id.contact);
        this.eye = (ImageView)findViewById(R.id.eyeIcon);
    }
    private void setInfo(ArrayList<Object> postInfo) {
        this.location.setText(postInfo.get(1).toString());
        this.Name.setText(postInfo.get(2).toString());
        this.postMessage.setText(postInfo.get(3).toString());
        this.contact.setText(postInfo.get(4).toString());
        Log.e("timestamp", postInfo.get(0).toString());
        checkImageExistance(postInfo.get(2).toString(), postInfo.get(0).toString(), eye );
    }
    private void checkImageExistance(String Name, String timeStamp, final ImageView eye){
        StorageReference storageRef = Constants.storage.getReference();
        storageRef.child(Name + "_" + timeStamp).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Log.e("IMAGE", "SUCCESS");
                eye.setAlpha(1f);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("FAILED", "TO GET IMAGE");
            }
        });
    }
}