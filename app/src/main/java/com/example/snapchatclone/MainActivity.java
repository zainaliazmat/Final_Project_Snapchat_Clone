package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.security.AccessController;

public class MainActivity extends AppCompatActivity {

    CardView mUserProfile, mFindUsers, mCameraTools;
    ImageView mCameraFragmentIcon, mChatFragmentIcon, mStoryFragmentIcon;
    ViewPager viewPager;

    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mFindUsers =findViewById(R.id.findusers);
        mUserProfile =findViewById(R.id.userProfile);
        mCameraTools =findViewById(R.id.cameraTools);
        mCameraFragmentIcon =findViewById(R.id.cameraFragmentIcon);
        mChatFragmentIcon =findViewById(R.id.chatFragmentIcon);
        mStoryFragmentIcon =findViewById(R.id.storyFragmentIcon);

        UserInformation userInformationListener = new UserInformation();
        userInformationListener.startFetching();

        viewPager = findViewById(R.id.viewpager);

        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        viewPager.setCurrentItem(1);
          cameraFragmentView();
        clickListener();
        showFragmentView();
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter{
        public MyPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:{
                    return chat_fragment.newInstance();
                }
                case 1:{
                    return CameraFragment.newInstance();
                }
                case 2:{
                    return StoryFragment.newInstance();
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

    }

    private void showFragmentView() {
        if(viewPager.getCurrentItem() == 0){
            chatFragmentView();
        }else if(viewPager.getCurrentItem() == 2){
            storyFragmentView();
        }else{
            cameraFragmentView();
        }
    }

    private void clickListener() {
        mUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),UserProfileActivity.class);
                startActivity(intent);
            }
        });

        mFindUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),FindUsersActivity.class);
                startActivity(intent);
            }
        });
        mStoryFragmentIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);
                storyFragmentView();
            }
        });
        mChatFragmentIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
                chatFragmentView();
            }
        });
        mCameraFragmentIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
                cameraFragmentView();
            }
        });
    }

    private void storyFragmentView() {
        mCameraTools.setVisibility(View.GONE);
        mStoryFragmentIcon.setColorFilter(ContextCompat.getColor( MainActivity.this ,R.color.purple));
        mCameraFragmentIcon.setColorFilter(ContextCompat.getColor( MainActivity.this ,R.color.white));
        mChatFragmentIcon.setColorFilter(ContextCompat.getColor( MainActivity.this ,R.color.white));
    }

    private void cameraFragmentView() {
        mCameraTools.setVisibility(View.VISIBLE);
        mCameraFragmentIcon.setColorFilter(ContextCompat.getColor( MainActivity.this ,R.color.yellow));
        mStoryFragmentIcon.setColorFilter(ContextCompat.getColor( MainActivity.this ,R.color.white));
        mChatFragmentIcon.setColorFilter(ContextCompat.getColor( MainActivity.this ,R.color.white));
    }

    private void chatFragmentView() {
        mCameraTools.setVisibility(View.GONE);
        mChatFragmentIcon.setColorFilter(ContextCompat.getColor( MainActivity.this ,R.color.blue));
        mCameraFragmentIcon.setColorFilter(ContextCompat.getColor( MainActivity.this ,R.color.white));
        mStoryFragmentIcon.setColorFilter(ContextCompat.getColor( MainActivity.this ,R.color.white));
    }
}