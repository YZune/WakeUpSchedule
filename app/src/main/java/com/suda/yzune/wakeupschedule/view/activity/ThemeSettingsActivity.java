package com.suda.yzune.wakeupschedule.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.utils.GlideAppEngine;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import es.dmoral.toasty.Toasty;

public class ThemeSettingsActivity extends AppCompatActivity {

    RelativeLayout rl_nav_pic, rl_bg_pic, rl_bg_clear, rl_item_height;
    Switch s_seekbar, s_color;
    SeekBar sb_height, sb_bg_blur, sb_alpha, sb_widget_alpha;
    TextView tv_height, tv_bg_blur, tv_item_alpha, tv_widget_alpha;
    private static final int REQUEST_CODE_CHOOSE_NAV = 23;
    private static final int REQUEST_CODE_CHOOSE_MAIN = 24;
    EditText itemHeight;
    int mNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_settings);

        initView();
        initEvent();
    }

    public void initView() {
        tv_height = (TextView) findViewById(R.id.tv_item_height);
        tv_height.setText(SharedPreferencesUtils.getIntFromSP(this, "item_height", 56) + " dp");
        tv_bg_blur = (TextView) findViewById(R.id.tv_bg_blur);
        tv_item_alpha = (TextView) findViewById(R.id.tv_item_alpha);
        tv_widget_alpha = (TextView) findViewById(R.id.tv_widget_alpha);
        rl_nav_pic = (RelativeLayout) findViewById(R.id.rl_nav_pic);
        rl_bg_pic = (RelativeLayout) findViewById(R.id.rl_bg_pic);
        rl_bg_clear = (RelativeLayout) findViewById(R.id.rl_bg_clear);
        rl_item_height = (RelativeLayout) findViewById(R.id.rl_item_height);
        s_seekbar = (Switch) findViewById(R.id.s_seekbar);
        s_color = (Switch) findViewById(R.id.s_color);
        s_color.setChecked(SharedPreferencesUtils.getBooleanFromSP(this, "s_color", false));
        s_seekbar.setChecked(SharedPreferencesUtils.getBooleanFromSP(this, "s_seekbar", true));
        sb_bg_blur = (SeekBar) findViewById(R.id.sb_bg_blur);
        sb_bg_blur.setProgress(SharedPreferencesUtils.getIntFromSP(this, "sb_bg_blur", 0));
        tv_bg_blur.setText(sb_bg_blur.getProgress() + "%");
        sb_alpha = (SeekBar) findViewById(R.id.sb_alpha);
        sb_alpha.setProgress(SharedPreferencesUtils.getIntFromSP(this, "sb_alpha", 64));
        tv_item_alpha.setText(sb_alpha.getProgress() + "%");
        sb_widget_alpha = (SeekBar) findViewById(R.id.sb_widget_alpha);
        sb_widget_alpha.setProgress(SharedPreferencesUtils.getIntFromSP(this, "sb_widget_alpha", 64));
        tv_widget_alpha.setText(sb_widget_alpha.getProgress() + "%");
    }

    public void initEvent() {
        sb_bg_blur.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_bg_blur.setText(progress + "%");
                SharedPreferencesUtils.saveIntToSP(ThemeSettingsActivity.this, "sb_bg_blur", progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_item_alpha.setText(progress + "%");
                SharedPreferencesUtils.saveIntToSP(ThemeSettingsActivity.this, "sb_alpha", progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_widget_alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_widget_alpha.setText(progress + "%");
                SharedPreferencesUtils.saveIntToSP(ThemeSettingsActivity.this, "sb_widget_alpha", progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rl_item_height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setHeightDialog(ThemeSettingsActivity.this, 0);
            }
        });
        rl_bg_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtils.saveStringToSP(ThemeSettingsActivity.this, "pic_uri", "");
                Toasty.success(ThemeSettingsActivity.this,"还原成功").show();
            }
        });
        rl_nav_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ThemeSettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ThemeSettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    Matisse.from(ThemeSettingsActivity.this)
                            .choose(MimeType.allOf())
                            .countable(true)
                            .maxSelectable(1)
                            .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(new GlideAppEngine())
                            .forResult(REQUEST_CODE_CHOOSE_NAV);
                }
            }
        });
        rl_bg_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ThemeSettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ThemeSettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                } else {
                    Matisse.from(ThemeSettingsActivity.this)
                            .choose(MimeType.allOf())
                            .countable(true)
                            .maxSelectable(1)
                            .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(new GlideAppEngine())
                            .forResult(REQUEST_CODE_CHOOSE_MAIN);
                }
            }
        });
        s_color.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.saveBooleanToSP(ThemeSettingsActivity.this, "s_color", isChecked);
            }
        });
        s_seekbar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.saveBooleanToSP(ThemeSettingsActivity.this, "s_seekbar", isChecked);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Matisse.from(ThemeSettingsActivity.this)
                            .choose(MimeType.allOf())
                            .countable(true)
                            .maxSelectable(1)
                            .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(new GlideAppEngine())
                            .forResult(REQUEST_CODE_CHOOSE_NAV);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toasty.error(ThemeSettingsActivity.this, "你取消了授权，无法更换背景。").show();
                }
                break;
            }
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Matisse.from(ThemeSettingsActivity.this)
                            .choose(MimeType.allOf())
                            .countable(true)
                            .maxSelectable(1)
                            .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(new GlideAppEngine())
                            .forResult(REQUEST_CODE_CHOOSE_MAIN);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toasty.error(ThemeSettingsActivity.this, "你取消了授权，无法更换背景。").show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_MAIN && resultCode == RESULT_OK) {
            SharedPreferencesUtils.saveStringToSP(ThemeSettingsActivity.this, "pic_uri", Matisse.obtainResult(data).get(0).toString());
        }
        if (requestCode == REQUEST_CODE_CHOOSE_NAV && resultCode == RESULT_OK) {
            SharedPreferencesUtils.saveStringToSP(ThemeSettingsActivity.this, "nav_pic_uri", Matisse.obtainResult(data).get(0).toString());
        }
    }

    public void setHeightDialog(final Context context, final int type) {

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_item_height)
                .setCancelable(true)
                .setPositiveButton("确定", null)
                .create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNum = sb_height.getProgress();
                tv_height.setText(mNum + " dp");
                if (type == 0) {
                    SharedPreferencesUtils.saveIntToSP(ThemeSettingsActivity.this, "item_height", mNum);
                } else {
                    SharedPreferencesUtils.saveIntToSP(ThemeSettingsActivity.this, "widget_height", mNum);
                }
                dialog.dismiss();
            }

        });

        sb_height = (SeekBar) dialog.findViewById(R.id.sb_height);
        itemHeight = (EditText) dialog.findViewById(R.id.et_number);

        sb_height.setProgress(SharedPreferencesUtils.getIntFromSP(ThemeSettingsActivity.this, "item_height", 56));
        itemHeight.setText(SharedPreferencesUtils.getIntFromSP(ThemeSettingsActivity.this, "item_height", 56) + " dp");

        sb_height.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                itemHeight.setText(progress + " dp");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
