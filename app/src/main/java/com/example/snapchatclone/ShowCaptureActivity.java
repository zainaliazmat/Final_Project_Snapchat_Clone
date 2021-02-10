package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class ShowCaptureActivity extends AppCompatActivity {

    String uid;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_capture);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();

        try {
            bitmap = BitmapFactory.decodeStream(getApplication().openFileInput(getString(R.string.imageToSend)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            finish();
            return;
        }

        ImageView mImage = findViewById(R.id.imageCapture);
        mImage.setImageBitmap(bitmap);
        uid = FirebaseAuth.getInstance().getUid();
        CardView mSend = findViewById(R.id.send);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ChooseReceiverActivity.class);
                startActivity(intent);
            }
        });
    }

    private void SaveToStories() {
        final DatabaseReference userStoryDB = FirebaseDatabase.getInstance().getReference().child(getString(R.string.users)).child(uid).child(getString(R.string.story));
        final String Key = userStoryDB.push().getKey();
        StorageReference filepath = FirebaseStorage.getInstance().getReference().child(getString(R.string.captures)).child(Key);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);
        byte[] dataToUpload = baos.toByteArray();

        UploadTask uploadTask = filepath.putBytes(dataToUpload);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        Long CurrentTimeStamp = System.currentTimeMillis();
                        Long EndTimeStamp = CurrentTimeStamp + (24*60*60*1000);

                        Map<String, Object> mapToUpload = new HashMap<>();
                        mapToUpload.put(getString(R.string.imageURL), downloadUrl.toString());
                        mapToUpload.put(getString(R.string.timeStampBeg), CurrentTimeStamp);
                        mapToUpload.put(getString(R.string.timeStampEnd), EndTimeStamp);
                        userStoryDB.child(Key).setValue(mapToUpload);
                    }
                });

                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                finish();
            }
        });

    }


}