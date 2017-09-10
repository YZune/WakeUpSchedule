package com.suda.yzune.wakeupschedule.view;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.adapter.TodayCourseAdapter;
import com.suda.yzune.wakeupschedule.model.bean.Course;
import com.suda.yzune.wakeupschedule.utils.CourseUtils;
import com.suda.yzune.wakeupschedule.utils.ImageUtil;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TodayCourseActivity extends AppCompatActivity {

    private List<Course> dayCourse = new ArrayList<Course>();
    RecyclerView recyclerView;
    ImageView iv_background;
    private Bitmap bitmap;
    private File tempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.transparent));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_course);
        String ImagePath = SharedPreferencesUtils.getStringFromSP(TodayCourseActivity.this, "ImagePath");
        String ImageName = SharedPreferencesUtils.getStringFromSP(TodayCourseActivity.this, "ImageName");
        if (!ImagePath.equals("")) {
            tempFile = new File(ImagePath, ImageName);
            showImage(ImageUtil.getImage(ImagePath + "/" + ImageName, 100));
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
        List<Course> allCourse = gson.fromJson(SharedPreferencesUtils.getStringFromSP(getApplicationContext(), "course"), new TypeToken<List<Course>>() {
        }.getType());
        for (Course course : allCourse) {
            String oldRoom = course.getRoom();
            if (oldRoom.contains("</a>")) {
                course.setRoom(oldRoom.substring(0, oldRoom.indexOf("</a>")));
            }
            if (course.getDay() == getWeekday() && (course.getStartWeek() <= ScheduleActivity.countWeek()) && (course.getEndWeek() >= ScheduleActivity.countWeek()) && (course.getIsOdd() == 0 || ((course.getIsOdd() != 0) && ((course.getIsOdd()) % 2 == (ScheduleActivity.countWeek() % 2))))) {
                dayCourse.add(course);
            }

        }
        dayCourse = CourseUtils.makeCourseTogether(dayCourse);
    }

    public void showImage(Bitmap bitmap) {
        iv_background = (ImageView) findViewById(R.id.headerBg);
        iv_background.setImageBitmap(bitmap);
    }
}
