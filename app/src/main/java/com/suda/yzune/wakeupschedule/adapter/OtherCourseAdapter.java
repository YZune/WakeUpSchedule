package com.suda.yzune.wakeupschedule.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.model.bean.Course;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;

import java.util.List;

/**
 * Created by YZune on 2017/10/3.
 */

public class OtherCourseAdapter extends RecyclerView.Adapter<OtherCourseAdapter.ViewHolder> {
    private List<Course> otherCourseList;

    private String[] startList = {"8:00", "9:00", "10:10", "11:10", "13:30", "14:30", "15:40", "16:40", "18:30", "19:30", "20:30"};
    private String[] endList = {"8:50", "9:50", "11:00", "12:00", "14:20", "15:20", "16:30", "17:30", "19:20", "20:20", "21:20"};

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView courseName, teacherName, room, classTime, odd, classWeek, timeDetail;
        RelativeLayout rl_addCourse;
        ImageButton ib_addCourse;

        public ViewHolder(View view) {
            super(view);
            courseName = view.findViewById(R.id.cv_courseName);
            teacherName = view.findViewById(R.id.cv_teacher);
            room = view.findViewById(R.id.cv_room);
            classTime = view.findViewById(R.id.cv_time);
            odd = view.findViewById(R.id.cv_odd);
            ib_addCourse = view.findViewById(R.id.ib_addCourse);
            classWeek = view.findViewById(R.id.cv_week);
            timeDetail = view.findViewById(R.id.cv_timeDetail);
            rl_addCourse = view.findViewById(R.id.rl_addCourse);
            rl_addCourse.setVisibility(View.VISIBLE);
        }
    }


    public OtherCourseAdapter(List<Course> coursesList) {
        otherCourseList = coursesList;
    }

    @Override
    public OtherCourseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        OtherCourseAdapter.ViewHolder holder = new OtherCourseAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(OtherCourseAdapter.ViewHolder holder, int position) {
        final Gson gson = new Gson();
        final Course course = otherCourseList.get(position);
        holder.courseName.setText(course.getName());
        holder.teacherName.setText("老师：" + course.getTeach());
        holder.room.setText("地点：" + course.getRoom());
        holder.classTime.setText("时间：第" + course.getStart() + " - " + (course.getStep() + course.getStart() - 1) + "节");
        holder.timeDetail.setText(startList[course.getStart() - 1] + " - " + endList[course.getStart() + course.getStep() - 2]);
        holder.classWeek.setText("第" + course.getStartWeek() + " - " + course.getEndWeek() + "周");
        switch (course.getIsOdd()) {
            case 0:
                holder.odd.setVisibility(View.GONE);
                break;
            case 1:
                holder.odd.setText("单周");
                break;
            case 2:
                holder.odd.setText("双周");
                break;
        }
        holder.ib_addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isAble = true;
                List<Course> allCourse = gson.fromJson(SharedPreferencesUtils.getStringFromSP(view.getContext(), "course", ""), new TypeToken<List<Course>>() {
                }.getType());
                //Toast.makeText(view.getContext(), course.getName(), Toast.LENGTH_SHORT).show();
                for (Course c : allCourse) {
                    if (c.getDay() == course.getDay() && c.getIsOdd() == course.getIsOdd() && c.getStart() == course.getStart()) {
                        Toast.makeText(view.getContext(), "该课程与已有课程冲突\n不建议添加", Toast.LENGTH_SHORT).show();
                        isAble = false;
                        break;
                    }
                }
                if (isAble) {
                    allCourse.add(course);
                    String jsonCourse = gson.toJson(allCourse);
                    SharedPreferencesUtils.saveStringToSP(view.getContext(), "course", jsonCourse);
                    Toast.makeText(view.getContext(), "添加成功ヾ(≧▽≦*)o", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return otherCourseList.size();
    }
}
