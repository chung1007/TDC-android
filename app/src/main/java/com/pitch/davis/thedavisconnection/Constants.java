package com.pitch.davis.thedavisconnection;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * Created by sam on 6/15/17.
 */
public class Constants {

    public static FirebaseDatabase dataBase = FirebaseDatabase.getInstance();;
    public static DatabaseReference ref = dataBase.getReference();
    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static StorageReference storageRef = storage.getReferenceFromUrl("gs://thedavisconnection-92e9e.appspot.com");
    public static File posts = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/TDC/");

}
