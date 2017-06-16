package com.pitch.davis.thedavisconnection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    EditText nameInput;
    EditText emailOrPhoneInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        pref = getApplicationContext().getSharedPreferences("LoginInfo", MODE_PRIVATE);
        editor = pref.edit();
        setUp();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLogin();
            }
        }, 3000);
    }

    private void checkLogin(){
        if (!pref.contains("Name")){
            Log.e("test", "1");
            animations();
            nameInput.setFocusableInTouchMode(true);
            emailOrPhoneInput.setFocusableInTouchMode(true);
        }else{
            Intent homepage = new Intent(this, Homepage.class);
            startActivity(homepage);
            Utils.makeToast(this, "Welcome, " + pref.getString("Name", ""));
        }
    }

    public void nextClicked(View view){
        if (nameInput.getText().toString().equals("") || emailOrPhoneInput.getText().toString().equals("")){
            Utils.makeToast(this, "Invalid Input");
        }else{
            editor.putString("Name",nameInput.getText().toString());
            editor.putString("Contact", emailOrPhoneInput.getText().toString());
            editor.commit();
            Intent homepage = new Intent(this, Homepage.class);
            startActivity(homepage);
            Utils.makeToast(this, "Welcome, " + pref.getString("Name", ""));
        }
    }

    private void setUp(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        nameInput = (EditText)findViewById(R.id.nameInput);
        emailOrPhoneInput = (EditText)findViewById(R.id.phoneNumberInput);
        nameInput.setFocusable(false);
        emailOrPhoneInput.setFocusable(false);
        nameInput.setHintTextColor(Color.WHITE);
        emailOrPhoneInput.setHintTextColor(Color.WHITE);
    }
    private void animations(){
        TextView title = (TextView)findViewById(R.id.Title);
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.loginLayout);
        AlphaAnimation animation1 = new AlphaAnimation(0f, 1.0f);
        animation1.setDuration(500);
        layout.setAlpha(1f);
        layout.startAnimation(animation1);
        Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.text_anim);
        myAnim.setFillAfter(true);
        title.startAnimation(myAnim);
    }
}
