package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.ViewHolder;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class UserProfileActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMG = 1;
    private static final int PIC_CROP = 2;
    private static final int dialogView = 3;
    private static final String TAG = "Something";
    TextView mLogout, mUserName, mUserEmail;
    ImageView mUserProfileImg;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private static Uri imageUri = null;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        uid = FirebaseAuth.getInstance().getUid();

        mLogout = findViewById(R.id.logout);
        mUserName = findViewById(R.id.userName);
        mUserEmail = findViewById(R.id.userEmail);
        mUserProfileImg = findViewById(R.id.userProfileImg);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });

        mUserProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDialogView();
                getImageFromAlbum();
            }
        });
        setdata();

    }


    private void setdata() {
        DatabaseReference setdata = FirebaseDatabase.getInstance().getReference().child(getString(R.string.users)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        setdata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUserEmail.setText(snapshot.child(getString(R.string.email)).getValue().toString().trim());
                mUserName.setText(snapshot.child(getString(R.string.name)).getValue().toString().trim());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getImageFromAlbum(){
        try{
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType(getString(R.string.image));
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
        }catch(Exception exp){
            Log.i(getString(R.string.Error),exp.toString());
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if(reqCode == RESULT_LOAD_IMG){
                try {
                    imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    performCrop(imageUri);
                    mUserProfileImg.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), ""+R.string.sww, Toast.LENGTH_LONG).show();
                }

            }

            if(reqCode == PIC_CROP){
                if (data != null) {
                    // get the returned data
                    Bundle extras = data.getExtras();
                    // get the cropped bitmap
                    Bitmap selectedBitmap = extras.getParcelable(getString(R.string.data));
                    mUserProfileImg.setImageBitmap(selectedBitmap);
                    uploadImage();
                }
            }
        }
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent(getString(R.string.cac));
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, getString(R.string.image));
            // set crop properties here
            cropIntent.putExtra(getString(R.string.crop), true);
            // indicate aspect of desired crop
            cropIntent.putExtra(getString(R.string.aspectX), 1);
            cropIntent.putExtra(getString(R.string.aspectY), 1);
            // indicate output X and Y
            cropIntent.putExtra(getString(R.string.outputX), 128);
            cropIntent.putExtra(getString(R.string.outputY), 128);
            // retrieve data on return
            cropIntent.putExtra(getString(R.string.returndata), true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = getString(R.string.Whoops);
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void uploadImage()
    {
        if (imageUri != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.Uploading));
            progressDialog.show();

            // Defining the child of storageReference
            final DatabaseReference userStoryDB = FirebaseDatabase.getInstance().getReference().child(getString(R.string.users)).child(uid);
            final String Key = userStoryDB.push().getKey();
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(getString(R.string.captures)).child(Key);

            // adding listeners on upload
            // or failure of image
            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            userStoryDB.child(getString(R.string.profileImageUrl)).setValue(downloadUrl.toString());
                        }
                    });
                    // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), ""+R.string.imageUploaded, Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), ""+getString(R.string.failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(getString(R.string.uploaded) + (int)progress + getString(R.string.sign));
                                }
                            });
        }
    }

    private void removeProfilePictureFroFirebase(){
        final DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child(getString(R.string.users)).child(FirebaseAuth.getInstance().getUid());
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String url = snapshot.child(getString(R.string.profileImageUrl)).getValue().toString();

                final StorageReference photoRef = FirebaseStorage.getInstance().getReference().child(url);
                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        Log.d(TAG, getString(R.string.onSuccess));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        Log.d(TAG, getString(R.string.onFailure));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Map userInfo = new HashMap<>();
        userInfo.put(getString(R.string.profileImageUrl),"default");
        userDB.updateChildren(userInfo);
    }

    private void LogOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(),SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return;
    }

    public void closeProfile(View view) {
        finish();
        return;
    }

    public void myFriendListActivity(View view) {

    }

    public void addFriendActivity(View view) {
        Intent intent = new Intent(getApplicationContext(),FindUsersActivity.class);
        startActivity(intent);
    }

    public void AddMyNewStory(View view) {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

}