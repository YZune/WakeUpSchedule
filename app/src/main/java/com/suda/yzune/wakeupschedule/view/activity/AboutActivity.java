package com.suda.yzune.wakeupschedule.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.utils.UpdateUtil;

public class AboutActivity extends AppCompatActivity {

    TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        tv_version = (TextView) findViewById(R.id.tv_version);
        try {
            tv_version.setText("版本号：" + UpdateUtil.getVersionName(AboutActivity.this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
