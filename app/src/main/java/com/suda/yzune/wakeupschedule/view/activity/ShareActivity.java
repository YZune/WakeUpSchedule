package com.suda.yzune.wakeupschedule.view.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.EditText;

import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.utils.JsonUtils;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;
import com.suda.yzune.wakeupschedule.utils.ViewUtil;

import es.dmoral.toasty.Toasty;

public class ShareActivity extends AppCompatActivity {
    CardView cv_share, cv_receive, cv_edit;
    FloatingActionButton fab_done;
    EditText et_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtil.fullScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        initView();
        initEvent();
    }

    public void initView() {
        cv_share = (CardView) findViewById(R.id.cv_share);
        cv_receive = (CardView) findViewById(R.id.cv_receive);
        cv_edit = (CardView) findViewById(R.id.cv_edit);
        fab_done = (FloatingActionButton) findViewById(R.id.fab_done);
        cv_edit.setScaleX(0);
        cv_edit.setScaleY(0);
        fab_done.setScaleX(0);
        fab_done.setScaleY(0);
        cv_edit.setVisibility(View.INVISIBLE);
        fab_done.setVisibility(View.INVISIBLE);
        et_code = (EditText) findViewById(R.id.et_code);
    }

    public void initEvent() {
        cv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String course = SharedPreferencesUtils.getStringFromSP(ShareActivity.this, "course", "");
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("WakeUpSchedule", course);
                if (clipboardManager != null && !course.equals("")) {
                    clipboardManager.setPrimaryClip(clipData);
                    Toasty.success(ShareActivity.this, "复制成功~").show();
                } else if (course.equals("")) {
                    Toasty.error(ShareActivity.this, "看起来你的课表还是空的哦w(ﾟДﾟ)w").show();
                } else {
                    Toasty.error(ShareActivity.this, "复制失败w(ﾟДﾟ)w").show();
                }
            }
        });

        cv_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAnim();
            }
        });

        fab_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = et_code.getText().toString();
                if (!s.contains("[")) {
                    Toasty.error(ShareActivity.this, "别闹哦╭(╯^╰)╮").show();
                }
                if (JsonUtils.isGoodJson(s)) {
                    SharedPreferencesUtils.saveStringToSP(ShareActivity.this, "course", s);
                    Toasty.success(ShareActivity.this, "导入成功").show();
                    finish();
                } else {
                    Toasty.error(ShareActivity.this, "请确保正确复制了哦╭(╯^╰)╮").show();
                }
            }
        });
    }

    public void initAnim() {
        cv_edit.setVisibility(View.VISIBLE);
        fab_done.setVisibility(View.VISIBLE);
        float toY = cv_share.getY();
        float posY = cv_receive.getY();
        int height = cv_receive.getLayoutParams().height;
        ObjectAnimator shareAnim = ObjectAnimator.ofFloat(cv_share, "Y", toY, toY - Math.abs(toY - posY) * 2);
        ObjectAnimator rec_Y_Anim = ObjectAnimator.ofFloat(cv_receive, "Y", posY, toY - Math.abs(toY - posY));

        AnimatorSet CardSet = new AnimatorSet();
        CardSet.playTogether(shareAnim, rec_Y_Anim);
        CardSet.setDuration(1000);
        CardSet.setInterpolator(new AnticipateOvershootInterpolator());
        CardSet.start();


        ObjectAnimator xAnim = ObjectAnimator.ofFloat(cv_edit, "ScaleX", 0, 1);
        ObjectAnimator yAnim = ObjectAnimator.ofFloat(cv_edit, "ScaleY", 0, 1);
        ObjectAnimator _xAnim = ObjectAnimator.ofFloat(fab_done, "ScaleX", 0, 1);
        ObjectAnimator _yAnim = ObjectAnimator.ofFloat(fab_done, "ScaleY", 0, 1);
        AnimatorSet EditSet = new AnimatorSet();
        EditSet.playTogether(xAnim, yAnim, _xAnim, _yAnim);
        EditSet.setDuration(1000);
        EditSet.setStartDelay(1000);
        EditSet.setInterpolator(new AnticipateOvershootInterpolator());
        EditSet.start();
    }
}
