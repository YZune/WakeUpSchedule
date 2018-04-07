package com.suda.yzune.wakeupschedule.adapter;

import android.app.TimePickerDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.utils.CourseUtils;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;

import java.util.List;

/**
 * Created by YZune on 2017/11/15.
 */

public class ClassTimeAdapter extends RecyclerView.Adapter<ClassTimeAdapter.ViewHolder> {
    private List<String> TimeList;
    private List<String> endTimeList;

    int hour, min, classLong;
    String str_time;
    Gson gson;

//    private String[] startList = {"8:00", "9:00", "10:10", "11:10", "13:30", "14:30", "15:40", "16:40", "18:30", "19:30", "20:30"};
//    private String[] endList = {"8:50", "9:50", "11:00", "12:00", "14:20", "15:20", "16:30", "17:30", "19:20", "20:20", "21:20"};

    static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rl_ClassTime;
        TextView tv_ClassTitle, tv_ClassTime;

        public ViewHolder(View view) {
            super(view);
            rl_ClassTime = (RelativeLayout) view.findViewById(R.id.rl_class_time);
            tv_ClassTitle = (TextView) view.findViewById(R.id.tv_class_title);
            tv_ClassTime = (TextView) view.findViewById(R.id.tv_class_time);
        }
    }


    public ClassTimeAdapter(List<String> timeList, List<String> end_timeList) {
        TimeList = timeList;
        endTimeList = end_timeList;
    }

    @Override
    public ClassTimeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time, parent, false);
        ClassTimeAdapter.ViewHolder holder = new ClassTimeAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ClassTimeAdapter.ViewHolder holder, final int position) {
        gson = new Gson();
        str_time = TimeList.get(position);
        holder.tv_ClassTitle.setText("第 " + (position + 1) + " 节");
        holder.tv_ClassTime.setText(str_time);
        holder.rl_ClassTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        //Log.d("时间",SharedPreferencesUtils.getStringFromSP(view.getContext(), "endTimeList",""));
                        //endTimeList = gson.fromJson(SharedPreferencesUtils.getStringFromSP(view.getContext(), "endTimeList",""), new TypeToken<List<String>>(){}.getType());
                        classLong = SharedPreferencesUtils.getIntFromSP(view.getContext(), "classMin", 50);
                        hour = i;
                        min = i1;
                        String str_time = "";
                        if (hour < 10 && min >= 10) {
                            str_time = "0" + hour + ":" + min;
                        } else if (hour < 10 && min < 10) {
                            str_time = "0" + hour + ":0" + min;
                        } else if (hour >= 10 && min >= 10) {
                            str_time = hour + ":" + min;
                        } else if (hour >= 10 && min < 10) {
                            str_time = hour + ":0" + min;
                        }
                        holder.tv_ClassTime.setText(str_time);
                        TimeList.set(position, str_time);
                        endTimeList.set(position, CourseUtils.calAfterTime(str_time, classLong));
                        SharedPreferencesUtils.saveStringToSP(view.getContext(), "timeList", gson.toJson(TimeList));
                        SharedPreferencesUtils.saveStringToSP(view.getContext(), "endTimeList", gson.toJson(endTimeList));
                    }
                }, 0, 0, true);
                timePickerDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return TimeList.size();
    }


}
