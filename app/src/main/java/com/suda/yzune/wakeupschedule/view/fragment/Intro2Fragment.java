package com.suda.yzune.wakeupschedule.view.fragment;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;

public class Intro2Fragment extends Fragment {

    TextView tv_suda, tv_other, tv_head, tv_tip_suda, tv_tip_other;
    ImageView iv_school, iv_done;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro2, container, false);

        initView(view);
        initEvent();

        return view;
    }

    public void initView(View view) {
        tv_suda = (TextView) view.findViewById(R.id.tv_suda);
        tv_other = (TextView) view.findViewById(R.id.tv_other);
        tv_head = (TextView) view.findViewById(R.id.tv_intro_head);
        iv_school = (ImageView) view.findViewById(R.id.iv_school);
        iv_done = (ImageView) view.findViewById(R.id.iv_intro_done);
        tv_tip_other = (TextView) view.findViewById(R.id.tip_other);
        tv_tip_suda = (TextView) view.findViewById(R.id.tip_suda);
    }

    public void initEvent() {
        tv_suda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAnimToChoice(tv_suda, tv_other, tv_tip_suda);
                tv_suda.setClickable(false);
                tv_other.setClickable(false);
                SharedPreferencesUtils.saveIntToSP(getContext(), "chooseSchool", 1);

            }
        });

        tv_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAnimToChoice(tv_other, tv_suda, tv_tip_other);
                tv_suda.setClickable(false);
                tv_other.setClickable(false);
                SharedPreferencesUtils.saveIntToSP(getContext(), "chooseSchool", 2);
            }
        });
    }

    public void setAnimToChoice(TextView tv1, TextView tv2, TextView tv3) {
        float oY = tv_suda.getY();

        ObjectAnimator hideAnim1 = ObjectAnimator.ofFloat(tv_head, "alpha", 1, 0);
        ObjectAnimator hideAnim2 = ObjectAnimator.ofFloat(iv_school, "alpha", 1, 0);
        ObjectAnimator hideAnim3 = ObjectAnimator.ofFloat(tv2, "alpha", 1, 0);

        ObjectAnimator yAnim = ObjectAnimator.ofFloat(tv1, "y", tv1.getY(), tv_head.getY() - tv1.getHeight() / 4);
        ObjectAnimator sizeAnim = ObjectAnimator.ofFloat(tv1, "textSize", 16, 28);

        ObjectAnimator doneShowAnim = ObjectAnimator.ofFloat(iv_done, "alpha", 0, 1);
        ObjectAnimator doneRotationAnim = ObjectAnimator.ofFloat(iv_done, "rotation", 0, 1800);
        ObjectAnimator tipShowAnim = ObjectAnimator.ofFloat(tv3, "alpha", 0, 1);
        ObjectAnimator tipYAnim = ObjectAnimator.ofFloat(tv3, "y", tv3.getY(), oY + tv1.getHeight() / 2);

        AnimatorSet hideSet = new AnimatorSet();
        hideSet.playTogether(hideAnim1, hideAnim2, hideAnim3);
        hideSet.setDuration(300);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(sizeAnim, yAnim);
        set.setDuration(1000);

        AnimatorSet showSet = new AnimatorSet();
        showSet.playTogether(doneRotationAnim, doneShowAnim, tipShowAnim, tipYAnim);
        showSet.setDuration(1000);

        hideSet.start();
        set.start();

        showSet.setStartDelay(1000);
        showSet.start();
    }


}
