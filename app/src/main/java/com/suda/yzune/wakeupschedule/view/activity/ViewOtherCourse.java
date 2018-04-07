package com.suda.yzune.wakeupschedule.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.adapter.OtherCourseAdapter;
import com.suda.yzune.wakeupschedule.model.bean.Course;
import com.suda.yzune.wakeupschedule.utils.Constants;
import com.suda.yzune.wakeupschedule.utils.OkHttpUtil;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;
import com.suda.yzune.wakeupschedule.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class ViewOtherCourse extends AppCompatActivity {

    private List<Course> otherCourse = new ArrayList<Course>();
    AppCompatSpinner daySpinner, beginSpinner, campusSpinner;
    RecyclerView recyclerView;
    ImageView iv_background;
    TextView tv_none;
    ImageButton ib_search;
    int day, begin;
    String campus;
    private String[] campusList = {"天赐庄校区", "北校区", "独墅湖校区", "阳澄湖校区"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtil.fullScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other_course);
        String Image_URI = SharedPreferencesUtils.getStringFromSP(ViewOtherCourse.this, "pic_uri", "");
        if (!Image_URI.equals("")) {
            showImage(Uri.parse(Image_URI));
        }

        initView();
        initEvent();
    }

    public void initView() {
        daySpinner = (AppCompatSpinner) findViewById(R.id.day_spinner);
        beginSpinner = (AppCompatSpinner) findViewById(R.id.begin_spinner);
        campusSpinner = (AppCompatSpinner) findViewById(R.id.campus_spinner);
        ib_search = (ImageButton) findViewById(R.id.ib_search);
    }

    public void initEvent() {
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                day = i + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        beginSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                begin = i + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        campusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                campus = campusList[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ib_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCourse();
            }
        });
    }

    public void getCourse() {
        OkHttpUtil.getAsync(Constants.COURSE_API + "school/all_courses/getcourse?day=" + day + "&start=" + begin + "&campus=" + campus, new OkHttpUtil.ResultCallback() {
            @Override
            public void onError(Call call, Exception e) {
                Toast.makeText(ViewOtherCourse.this, "网络错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(byte[] response) {
                String result = new String(response);
                Gson gson = new Gson();
                otherCourse = gson.fromJson(result, new TypeToken<List<Course>>() {
                }.getType());
                //Toast.makeText(ViewOtherCourse.this, result, Toast.LENGTH_SHORT).show();
                if (otherCourse.size() != 0) {
                    recyclerView = (RecyclerView) findViewById(R.id.rv_otherCourse);
                    recyclerView.setVisibility(View.VISIBLE);
                    tv_none = (TextView) findViewById(R.id.tv_none);
                    tv_none.setVisibility(View.GONE);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(ViewOtherCourse.this);
                    recyclerView.setLayoutManager(layoutManager);
                    OtherCourseAdapter adapter = new OtherCourseAdapter(otherCourse);
                    recyclerView.setAdapter(adapter);
                } else {
                    recyclerView = (RecyclerView) findViewById(R.id.rv_otherCourse);
                    recyclerView.setVisibility(View.GONE);
                    tv_none = (TextView) findViewById(R.id.tv_none);
                    tv_none.setVisibility(View.VISIBLE);
                    tv_none.setText("没有查询到符合条件的课呢＞﹏＜\n可能是数据太少啦……\n使用的人越多\n课程库里的课程越多哦");
                }
            }
        });
    }

    public void showImage(Uri uri) {
        iv_background = (ImageView) findViewById(R.id.headerBg);
        Glide.with(ViewOtherCourse.this).load(uri).into(iv_background);
    }
}
