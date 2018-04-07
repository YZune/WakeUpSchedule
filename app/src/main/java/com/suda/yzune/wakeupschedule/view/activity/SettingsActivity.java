package com.suda.yzune.wakeupschedule.view.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class SettingsActivity extends AppCompatActivity {

    int mYear, mMonth, mDay;
    int mNum;
    String mDate;
    RelativeLayout rl_termStart, rl_classNum, rl_classTime, rl_clean, rl_save, rl_item_height, rl_widget_height;
    TextView dateDisplay, tv_classNum, tv_height, tv_widget;
    ImageButton ib_less, ib_more;
    EditText classNum, itemHeight;
    Switch s_show, s_update, s_seekbar, s_color;
    SeekBar sb_height;
    final int DATE_DIALOG = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mNum = SharedPreferencesUtils.getIntFromSP(SettingsActivity.this, "classNum", 11);

        initView();
        initEvent();

        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG:
                return new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
        }
        return null;
    }

    /**
     * 设置日期 利用StringBuffer追加
     */
    public void display() {
        mDate = new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay).append("").toString();
        dateDisplay.setText(mDate);
        SharedPreferencesUtils.saveStringToSP(this, "termStart", mDate);
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            display();
        }
    };

    public void initView() {
        tv_widget = (TextView) findViewById(R.id.tv_widget_height);
        tv_widget.setText(SharedPreferencesUtils.getIntFromSP(this, "widget_height", 56) + " dp");
        tv_height = (TextView) findViewById(R.id.tv_item_height);
        tv_height.setText(SharedPreferencesUtils.getIntFromSP(this, "item_height", 56) + " dp");
        rl_item_height = (RelativeLayout) findViewById(R.id.rl_item_height);
        rl_widget_height = (RelativeLayout) findViewById(R.id.rl_widget_height);
        rl_termStart = (RelativeLayout) findViewById(R.id.rl_termStart);
        dateDisplay = (TextView) findViewById(R.id.dateDisplay);
        dateDisplay.setText(SharedPreferencesUtils.getStringFromSP(this, "termStart", "2018-03-05"));
        rl_classNum = (RelativeLayout) findViewById(R.id.rl_classNum);
        tv_classNum = (TextView) findViewById(R.id.tv_classNum);
        rl_classTime = (RelativeLayout) findViewById(R.id.rl_classTime);
        rl_clean = (RelativeLayout) findViewById(R.id.rl_clean);
        rl_save = (RelativeLayout) findViewById(R.id.rl_save);
        s_color = (Switch) findViewById(R.id.s_color);
        s_seekbar = (Switch) findViewById(R.id.s_seekbar);
        s_update = (Switch) findViewById(R.id.s_update);
        s_show = (Switch) findViewById(R.id.s_show);
        s_show.setChecked(SharedPreferencesUtils.getBooleanFromSP(SettingsActivity.this, "s_show", false));
        s_color.setChecked(SharedPreferencesUtils.getBooleanFromSP(SettingsActivity.this, "s_color", false));
        s_seekbar.setChecked(SharedPreferencesUtils.getBooleanFromSP(SettingsActivity.this, "s_seekbar", true));
        s_update.setChecked(SharedPreferencesUtils.getBooleanFromSP(SettingsActivity.this, "s_update", true));
    }

    public void initEvent() {
        s_show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.saveBooleanToSP(SettingsActivity.this, "s_show", isChecked);
            }
        });
        s_update.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.saveBooleanToSP(SettingsActivity.this, "s_update", isChecked);
            }
        });
        s_color.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.saveBooleanToSP(SettingsActivity.this, "s_color", isChecked);
            }
        });
        s_seekbar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.saveBooleanToSP(SettingsActivity.this, "s_seekbar", isChecked);
            }
        });
        tv_classNum.setText(mNum + "节");
        rl_termStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
                Toasty.success(SettingsActivity.this, "为了周数计算准确，建议选择周一哦").show();
            }
        });
        rl_classNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClassNumDialog(SettingsActivity.this);
            }
        });
        rl_widget_height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setHeightDialog(SettingsActivity.this, 1);
            }
        });
        rl_item_height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setHeightDialog(SettingsActivity.this, 0);
            }
        });
        rl_classTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingsActivity.this, TimeSettingActivity.class);
                startActivity(i);
            }
        });
        rl_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CleanDialog(SettingsActivity.this);
            }
        });
        rl_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toasty.success(SettingsActivity.this, "努力开发中(ง •_•)ง").show();
            }
        });
    }

    public void setClassNumDialog(final Context context) {

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_choose_number)
                .setCancelable(true)
                .setPositiveButton("确定", null)
                .create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNum = Integer.valueOf(classNum.getText().toString());
                tv_classNum.setText(mNum + "节");
                SharedPreferencesUtils.saveIntToSP(SettingsActivity.this, "classNum", mNum);
                dialog.dismiss();
            }

        });

        ib_less = (ImageButton) dialog.findViewById(R.id.ib_less);
        ib_more = (ImageButton) dialog.findViewById(R.id.ib_more);
        classNum = (EditText) dialog.findViewById(R.id.et_number);

        classNum.setText(mNum + "");

        ib_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = classNum.getText().toString();
                if (s.equals("")) {
                    Toasty.error(context, "请认真填写_(:з)∠)_").show();
                } else {
                    int n = Integer.valueOf(s);
                    if (n <= 8) {
                        Toasty.error(context, "不能再少啦_(:з)∠)_").show();
                        classNum.setText("8");
                    } else {
                        n--;
                        classNum.setText(n + "");
                    }
                }
            }
        });

        ib_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = classNum.getText().toString();
                if (s.equals("")) {
                    Toasty.error(context, "请认真填写_(:з)∠)_").show();
                } else {
                    int n = Integer.valueOf(s);
                    if (n >= 15) {
                        Toasty.error(context, "不能再多啦_(:з)∠)_").show();
                        classNum.setText("15");
                    } else {
                        n++;
                        classNum.setText(n + "");
                    }
                }
            }
        });
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
                    SharedPreferencesUtils.saveIntToSP(SettingsActivity.this, "item_height", mNum);
                } else {
                    SharedPreferencesUtils.saveIntToSP(SettingsActivity.this, "widget_height", mNum);
                }
                dialog.dismiss();
            }

        });

        sb_height = (SeekBar) dialog.findViewById(R.id.sb_height);
        itemHeight = (EditText) dialog.findViewById(R.id.et_number);

        sb_height.setProgress(SharedPreferencesUtils.getIntFromSP(SettingsActivity.this, "item_height", 56));
        itemHeight.setText(SharedPreferencesUtils.getIntFromSP(SettingsActivity.this, "item_height", 56) + " dp");

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

    public void CleanDialog(final Context context) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("警告");
        builder.setMessage("确定要重置App吗？这将清空所有数据。");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferencesUtils.clean(context);
                Toasty.success(context, "重置成功，重启App后生效。").show();
            }
        });
        builder.setNegativeButton("手滑了", null);
        builder.show();
    }
}
