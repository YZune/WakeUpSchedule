package com.suda.yzune.wakeupschedule.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;
import com.suda.yzune.wakeupschedule.view.fragment.Intro1Fragment;
import com.suda.yzune.wakeupschedule.view.fragment.Intro2Fragment;

import es.dmoral.toasty.Toasty;

public class IntroActivity extends AppIntro2 {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setOffScreenPageLimit(3);

        addSlide(new Intro1Fragment());
        addSlide(new Intro2Fragment());

        //setBarColor(Color.parseColor("#58B2DC"));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        int chooseSchool = SharedPreferencesUtils.getIntFromSP(IntroActivity.this, "chooseSchool", 0);
        switch (chooseSchool){
            case 0:
                Toasty.error(IntroActivity.this, "请先选择学校哦~").show();
                break;
            case 1:
            case 2:
                startActivity(new Intent(IntroActivity.this, ScheduleActivity.class));
                finish();
                break;
        }
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }


}
