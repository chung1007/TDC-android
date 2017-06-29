package com.pitch.davis.thedavisconnection;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by sam on 6/29/17.
 */
public class CategoryList extends RelativeLayout {
    Button sale;
    Button announcement;
    Button report;
    Button lostAndFound;
    Button misc;

    public CategoryList(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.category_scrollview, this, true);
        sale = (Button)findViewById(R.id.Sale);
        announcement = (Button)findViewById(R.id.Announcement);
        report = (Button)findViewById(R.id.Report);
        lostAndFound = (Button)findViewById(R.id.lostAndFound);
        misc = (Button)findViewById(R.id.Misc);
        setOnClickListener();

    }

    public void setOnClickListener(){
        ArrayList<Button> buttons = new ArrayList<>(Arrays.asList(sale, announcement, report, lostAndFound, misc));
        for(Button b : buttons){
            b.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < ((LinearLayout) v.getParent()).getChildCount(); i++) {
                        ((Button) ((LinearLayout) v.getParent()).getChildAt(i)).setAlpha(0.3f);
                    }
                    ((Button) v).setAlpha(1f);
                }
            });
        }
    }
}
