package com.suda.yzune.wakeupschedule.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.suda.yzune.wakeupschedule.R;

public class Intro1Fragment extends Fragment {
    ImageView iv_logo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro1, container, false);
        TranslateAnimation translateAnimation = (TranslateAnimation) AnimationUtils.loadAnimation(getContext(), R.anim.logo_jump);
        iv_logo = (ImageView) view.findViewById(R.id.iv_logo_deer);
        iv_logo.startAnimation(translateAnimation);
        return view;
    }
}
