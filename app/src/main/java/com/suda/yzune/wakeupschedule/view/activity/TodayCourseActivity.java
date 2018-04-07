package com.suda.yzune.wakeupschedule.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.adapter.TodayCourseAdapter;
import com.suda.yzune.wakeupschedule.model.bean.Course;
import com.suda.yzune.wakeupschedule.utils.CourseUtils;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;
import com.suda.yzune.wakeupschedule.utils.ViewUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TodayCourseActivity extends AppCompatActivity {

    private List<Course> dayCourse = new ArrayList<Course>();
    RecyclerView recyclerView;
    ImageView iv_background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtil.fullScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_course);
        String Image_URI = SharedPreferencesUtils.getStringFromSP(TodayCourseActivity.this, "pic_uri", "");
        if (!Image_URI.equals("")) {
            showImage(Uri.parse(Image_URI));
        }
        try {
            getDayCourse();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dayCourse.size() != 0) {
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_todayCourse);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            Collections.sort(dayCourse, new Comparator<Course>() {
                @Override
                public int compare(Course o1, Course o2) {
                    int i = o1.getStart() - o2.getStart();
                    return i;
                }
            });
            TodayCourseAdapter adapter = new TodayCourseAdapter(dayCourse);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView = (RecyclerView) findViewById(R.id.rv_todayCourse);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public int getWeekday() {
        int weekDay = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK);
        if (weekDay == 1) {
            weekDay = 7;
        } else {
            weekDay = weekDay - 1;
        }
        Log.d("星期", weekDay + "");
        return weekDay;
    }

    public void getDayCourse() throws ParseException {
        Gson gson = new Gson();
        List<Course> allCourse = gson.fromJson(SharedPreferencesUtils.getStringFromSP(getApplicationContext(), "course", ""), new TypeToken<List<Course>>() {
        }.getType());
        for (Course course : allCourse) {
            String oldRoom = course.getRoom();
            if (oldRoom.contains("</a>")) {
                course.setRoom(oldRoom.substring(0, oldRoom.indexOf("</a>")));
            }
            if (course.getDay() == getWeekday() && (course.getStartWeek() <= ScheduleActivity.countWeek(TodayCourseActivity.this)) && (course.getEndWeek() >= ScheduleActivity.countWeek(TodayCourseActivity.this)) && (course.getIsOdd() == 0 || ((course.getIsOdd() != 0) && ((course.getIsOdd()) % 2 == (ScheduleActivity.countWeek(TodayCourseActivity.this) % 2))))) {
                dayCourse.add(course);
            }

        }
        dayCourse = CourseUtils.makeCourseTogether(dayCourse);
    }

    public void showImage(Uri uri) {
        iv_background = (ImageView) findViewById(R.id.headerBg);
        Glide.with(TodayCourseActivity.this).load(uri).into(iv_background);
    }
}
