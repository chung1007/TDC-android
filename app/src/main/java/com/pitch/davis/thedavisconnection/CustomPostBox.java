package com.pitch.davis.thedavisconnection;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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
    private TextView readMore;
    private ImageView eye;
    private TextView contact;
    private Activity activity;
    private ArrayList<Object> postInfo;
    Context context;


    public CustomPostBox(Context context, ArrayList<Object> postInfo, Activity acitivity) {
        super(context);
        this.context = context;
        this.activity = acitivity;
        this.postInfo = postInfo;
        init();
        setInfo();

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
        this.readMore = (TextView)findViewById(R.id.readMore);
    }
    private void setInfo() {
        this.location.setText(postInfo.get(1).toString());
        this.Name.setText(postInfo.get(2).toString());
        this.postMessage.setText(postInfo.get(3).toString());
        this.contact.setText(postInfo.get(4).toString());

        checkImageExistance(postInfo.get(2).toString(), postInfo.get(0).toString(), eye);
        checkMessageLength();
        setEyeClickListener(postInfo.get(2).toString() + "_" + postInfo.get(0).toString());
    }
    private void checkImageExistance(String Name, String timeStamp, final ImageView eye){
        StorageReference storageRef = Constants.storage.getReference();
        storageRef.child(Name + "_" + timeStamp).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
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
    private void checkMessageLength(){
        postMessage.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if (postMessage.getLineCount() == 4) {
                    readMore.setAlpha(1f);
                    readMore.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Dialog dialog = new Dialog(activity, R.style.Full);
                            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.post_view_layout);

                            TextView viewName = (TextView) dialog.findViewById(R.id.Name);
                            TextView viewLocation = (TextView) dialog.findViewById(R.id.location);
                            TextView viewMessage = (TextView) dialog.findViewById(R.id.postMessage);

                            viewName.setText(postInfo.get(2).toString());
                            viewLocation.setText(postInfo.get(1).toString());
                            viewMessage.setText(postInfo.get(3).toString());

                            Window window = dialog.getWindow();
                            window.setGravity(Gravity.CENTER);
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.show();
                        }
                    });
                }
            }
        });
    }

    private void setEyeClickListener(final String fileName){
        eye.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eye.getAlpha() == 1f){
                    StorageReference ref = Constants.storageRef.child(fileName);
                    final Dialog dialog = new Dialog(activity, R.style.FullHeightDialog);
                    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.image_layout);
                    ImageView image = (ImageView) dialog.findViewById(R.id.postImageDisplay);

                    Glide.with(context)
                            .using(new FirebaseImageLoader())
                            .load(ref)
                            .into(image);
                    Window window = dialog.getWindow();
                    window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    window.setGravity(Gravity.CENTER);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                }
            }
        });
    }
}