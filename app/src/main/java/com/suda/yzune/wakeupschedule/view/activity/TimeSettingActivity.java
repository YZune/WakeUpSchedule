package com.suda.yzune.wakeupschedule.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.adapter.ClassTimeAdapter;
import com.suda.yzune.wakeupschedule.utils.CourseUtils;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class TimeSettingActivity extends AppCompatActivity {
    List<String> timeList = new ArrayList<String>();
    List<String> end_timeList = new ArrayList<String>();
    RecyclerView recyclerView;
    RelativeLayout rl_classTime;
    TextView tv_classTime, tv_dialog_title;
    int mMin;
    EditText et_min;
    ImageButton ib_less, ib_more;
    ClassTimeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_setting);

        Gson gson = new Gson();
        String str_timeList = SharedPreferencesUtils.getStringFromSP(this, "timeList", "");
        mMin = SharedPreferencesUtils.getIntFromSP(this, "classMin", 50);
        //int classNum = SharedPreferencesUtils.getIntFromSP(this, "classNum", 11);
        if (str_timeList.equals("")) {
            for (int i = 0; i < 15; i++) {
                timeList.add("00:00");
                end_timeList.add("00:00");
            }
            SharedPreferencesUtils.saveStringToSP(this, gson.toJson(timeList), "timeList");
            SharedPreferencesUtils.saveStringToSP(this, gson.toJson(end_timeList), "endTimeList");
            //Log.d("时间", gson.toJson(end_timeList));
        } else {
            timeList = gson.fromJson(str_timeList, new TypeToken<List<String>>() {
            }.getType());
            end_timeList = gson.fromJson(SharedPreferencesUtils.getStringFromSP(this, "endTimeList", ""), new TypeToken<List<String>>() {
            }.getType());
        }

        recyclerView = (RecyclerView) findViewById(R.id.rv_time_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //timeList = gson.fromJson(str_timeList, new TypeToken<List<String>>(){}.getType());
        adapter = new ClassTimeAdapter(timeList, end_timeList);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        rl_classTime = (RelativeLayout) findViewById(R.id.rl_class_time);
        tv_classTime = (TextView) findViewById(R.id.tv_class_time);

        rl_classTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClassMinDialog(TimeSettingActivity.this);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMin = SharedPreferencesUtils.getIntFromSP(this, "classMin", 50);
        tv_classTime.setText(mMin + "分钟");
    }

    public void setClassMinDialog(final Context context) {
        final Gson gson = new Gson();

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_choose_number)
                .setCancelable(true)
                .setPositiveButton("确定", null)
                .create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMin = Integer.valueOf(et_min.getText().toString());
                tv_classTime.setText(mMin + "分钟");
                SharedPreferencesUtils.saveIntToSP(TimeSettingActivity.this, "classMin", mMin);
                List<String> start_temp = gson.fromJson(SharedPreferencesUtils.getStringFromSP(context, "timeList", ""), new TypeToken<List<String>>() {
                }.getType());
                List<String> end_temp = gson.fromJson(SharedPreferencesUtils.getStringFromSP(context, "endTimeList", ""), new TypeToken<List<String>>() {
                }.getType());
                for (int i = 0; i < 15; i++) {
                    end_temp.set(i, CourseUtils.calAfterTime(start_temp.get(i), mMin));
                }
                SharedPreferencesUtils.saveStringToSP(context,"endTimeList",gson.toJson(end_temp));
                dialog.dismiss();
            }

        });

        ib_less = (ImageButton) dialog.findViewById(R.id.ib_less);
        ib_more = (ImageButton) dialog.findViewById(R.id.ib_more);
        et_min = (EditText) dialog.findViewById(R.id.et_number);
        tv_dialog_title = (TextView) dialog.findViewById(R.id.dialog_title);
        tv_dialog_title.setText("设置课程时长");

        et_min.setText(mMin + "");

        ib_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = et_min.getText().toString();
                if (s.equals("")) {
                    Toast.makeText(context, "请认真填写_(:з)∠)_", Toast.LENGTH_SHORT).show();
                } else {
                    int n = Integer.valueOf(s);
                    if (n <= 10) {
                        Toast.makeText(context, "不能再少啦_(:з)∠)_", Toast.LENGTH_SHORT).show();
                        et_min.setText("10");
                    } else {
                        n--;
                        et_min.setText(n + "");
                    }
                }
            }
        });

        ib_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = et_min.getText().toString();
                if (s.equals("")) {
                    Toast.makeText(context, "请认真填写_(:з)∠)_", Toast.LENGTH_SHORT).show();
                } else {
                    int n = Integer.valueOf(s);
                    if (n >= 120) {
                        Toast.makeText(context, "不能再多啦_(:з)∠)_", Toast.LENGTH_SHORT).show();
                        et_min.setText("120");
                    } else {
                        n++;
                        et_min.setText(n + "");
                    }
                }
            }
        });
    }
}
