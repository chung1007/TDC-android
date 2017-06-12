package com.pitch.davis.thedavisconnection;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText nameInput;
    EditText emailOrPhoneInput;
    Animation anm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (!pref.contains("Name")){
            Log.e("test", "1");
            animations();
            nameInput.setFocusableInTouchMode(true);
            emailOrPhoneInput.setFocusableInTouchMode(true);
        }
    }

    public void setUp(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        nameInput = (EditText)findViewById(R.id.nameInput);
        emailOrPhoneInput = (EditText)findViewById(R.id.phoneNumberInput);
        nameInput.setFocusable(false);
        emailOrPhoneInput.setFocusable(false);
    }
    public void animations(){
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
