package com.suda.yzune.wakeupschedule.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;
import com.suda.yzune.wakeupschedule.utils.ViewUtil;

import java.io.File;

public class FixActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private File tempFile;
    ImageView iv_background;
    CardView m1, m2, m3, m4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtil.fullScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix);
        String Image_URI = SharedPreferencesUtils.getStringFromSP(FixActivity.this, "pic_uri", "");
        if (!Image_URI.equals("")) {
            showImage(Uri.parse(Image_URI));
        }
        m1 = (CardView) findViewById(R.id.m1);
        m2 = (CardView) findViewById(R.id.m2);
        m3 = (CardView) findViewById(R.id.m3);
        m4 = (CardView) findViewById(R.id.m4);
        m1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQQChat("mqqwpa://im/chat?chat_type=wpa&uin=1055614742&version=1");
            }
        });
        m2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQQChat("mqqwpa://im/chat?chat_type=wpa&uin=1361421504&version=1");
            }
        });
        m3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQQChat("mqqwpa://im/chat?chat_type=wpa&uin=2174658579&version=1");
            }
        });
        m4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQQChat("mqqwpa://im/chat?chat_type=wpa&uin=627971428&version=1");
            }
        });
    }

    public void showImage(Uri uri) {
        iv_background = (ImageView) findViewById(R.id.headerBg);
        Glide.with(FixActivity.this).load(uri).into(iv_background);
    }

    public void startQQChat(String qqUrl) {
        if (ScheduleActivity.isQQClientAvailable(getApplicationContext())) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)));
        } else {
            Toast.makeText(getApplicationContext(), "手机上没有安装QQ，无法启动聊天窗口:-(", Toast.LENGTH_LONG).show();
        }
    }
}
