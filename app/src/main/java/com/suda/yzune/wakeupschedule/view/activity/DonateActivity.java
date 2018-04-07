package com.suda.yzune.wakeupschedule.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.utils.DonateUtils;

import es.dmoral.toasty.Toasty;

public class DonateActivity extends AppCompatActivity {

    AppCompatButton btn_alipay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        btn_alipay = (AppCompatButton) findViewById(R.id.btn_alipay);
        btn_alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DonateUtils.isAppInstalled(DonateActivity.this, "com.eg.android.AlipayGphone")) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri qrcode_url = Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=HTTPS://QR.ALIPAY.COM/FKX09148M0LN2VUUZENO9B?_s=web-other");
                    intent.setData(qrcode_url);
                    intent.setClassName("com.eg.android.AlipayGphone", "com.alipay.mobile.quinox.LauncherActivity");
                    startActivity(intent);
                    Toasty.success(DonateActivity.this, "非常感谢(*^▽^*)").show();
                } else {
                    Toasty.error(DonateActivity.this, "没有检测到支付宝客户端o(╥﹏╥)o").show();
                }
            }
        });
    }
}
