package com.pitch.davis.thedavisconnection;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sam on 6/16/17.
 */
public class PostListAdapter extends BaseAdapter{

    Context context;
    List <String> fileNames;
    public PostListAdapter(Context context, List<String> files) {
        super();
        this.context = context;
        this.fileNames = files;

    }
    @Override
    public int getCount(){
        return fileNames.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return getItem(arg0);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomPostBox view;
        try {
            JSONObject JObject = new JSONObject(Utils.readFile(Constants.posts.getAbsolutePath() + "/" + fileNames.get(position)));
            ArrayList<Object> postData = new ArrayList<>(Arrays.asList(JObject.get("timeStamp"), JObject.get("Location")
            , JObject.get("Name"), JObject.get("Message"), JObject.get("Contact")));
            view = new CustomPostBox(context, postData);
            convertView = view;
        }catch (JSONException JE){
            Log.e("JSON EXCEPTION", "while converting file to JSON");
        }

        return convertView;
    }

}
