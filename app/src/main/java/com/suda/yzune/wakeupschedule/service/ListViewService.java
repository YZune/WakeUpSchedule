package com.suda.yzune.wakeupschedule.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.model.bean.Course;
import com.suda.yzune.wakeupschedule.utils.CourseUtils;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;
import com.suda.yzune.wakeupschedule.view.ScheduleWidget;
import com.suda.yzune.wakeupschedule.view.activity.ScheduleActivity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YZune on 2017/9/10.
 */

public class ListViewService extends RemoteViewsService {

    private String[] startList = {"08:00", "09:00", "10:10", "11:10", "13:30", "14:30", "15:40", "16:40", "18:30", "19:30", "20:30"};
    private String[] endList = {"08:50", "09:50", "11:00", "12:00", "14:20", "15:20", "16:30", "17:30", "19:20", "20:20", "21:20"};

    static String str_time;
    static int chooseSchool;
    static List<String> timeList, end_timeList;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    private class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context mContext;

        private List<Course> mList = new ArrayList<>();

        public ListRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
        }

        @Override
        public void onCreate() {
//            try {
//                getDayCourse();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onDataSetChanged() {
            try {
                getDayCourse(mContext);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDestroy() {
            mList.clear();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Gson gson = new Gson();

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.item_today_widget);
            views.setTextViewText(R.id.tv_startTime, startList[mList.get(position).getStart() - 1]);
            views.setTextViewText(R.id.tv_endTime, endList[mList.get(position).getStart() + mList.get(position).getStep() - 2]);
            views.setTextViewText(R.id.widget_name, mList.get(position).getName());
            views.setTextViewText(R.id.widget_room, mList.get(position).getRoom());
            views.setTextViewText(R.id.widget_teacher, mList.get(position).getTeach());
            views.setTextViewText(R.id.tv_start, mList.get(position).getStart() + "");
            views.setTextViewText(R.id.tv_end, mList.get(position).getStart() + mList.get(position).getStep() - 1 + "");

            str_time = SharedPreferencesUtils.getStringFromSP(mContext, "timeList", "");
            timeList = gson.fromJson(str_time, new TypeToken<List<String>>(){}.getType());
            end_timeList = gson.fromJson(SharedPreferencesUtils.getStringFromSP(mContext,"endTimeList",""), new TypeToken<List<String>>(){}.getType());
            chooseSchool = SharedPreferencesUtils.getIntFromSP(mContext, "chooseSchool");

            if (chooseSchool == 1){
                views.setTextViewText(R.id.tv_startTime, startList[mList.get(position).getStart() - 1]);
                views.setTextViewText(R.id.tv_endTime, endList[mList.get(position).getStart() + mList.get(position).getStep() - 2]);
                //holder.timeDetail.setText(startList[course.getStart() - 1] + " - " + endList[course.getStart() + course.getStep() - 2]);
            }
            else {
                if (str_time.equals("")){
                    //holder.timeDetail.setText("还未设置课程时间");
                    views.setTextViewText(R.id.tv_startTime, "00:00");
                    views.setTextViewText(R.id.tv_endTime, "00:00");
                }else {
                    views.setTextViewText(R.id.tv_startTime, timeList.get(mList.get(position).getStart() - 1));
                    views.setTextViewText(R.id.tv_endTime, end_timeList.get(mList.get(position).getStart() + mList.get(position).getStep() - 2));
                    // holder.timeDetail.setText(timeList.get(course.getStart() - 1) + " - " + end_timeList.get(course.getStart() + course.getStep() - 2));
                }
            }

            Intent intent = new Intent(ScheduleWidget.ITEM_CLICK);
            views.setOnClickFillInIntent(R.id.ll_course, intent);
            return views;
        }

        /* 在更新界面的时候如果耗时就会显示 正在加载... 的默认字样，但是你可以更改这个界面
         * 如果返回null 显示默认界面
         * 否则 加载自定义的，返回RemoteViews
         */
        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
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

        public void getDayCourse(Context context) throws ParseException {
            mList.clear();
            Gson gson = new Gson();
            String sp = SharedPreferencesUtils.getStringFromSP(getApplicationContext(), "course", "");
            if (!sp.equals("")) {
                List<Course> allCourse = gson.fromJson(sp, new TypeToken<List<Course>>() {
                }.getType());
                for (Course course : allCourse) {
                    String oldRoom = course.getRoom();
                    if (oldRoom.contains("</a>")) {
                        course.setRoom(oldRoom.substring(0, oldRoom.indexOf("</a>")));
                    }
                    if (course.getDay() == getWeekday() && (course.getStartWeek() <= ScheduleActivity.countWeek(context)) && (course.getEndWeek() >= ScheduleActivity.countWeek(context)) && (course.getIsOdd() == 0 || ((course.getIsOdd() != 0) && ((course.getIsOdd()) % 2 == (ScheduleActivity.countWeek(context) % 2))))) {
                        mList.add(course);
                    }

                }
                mList = CourseUtils.makeCourseTogether(mList);
            }
//            Collections.sort(mList, new Comparator<Course>() {
//                @Override
//                public int compare(Course o1, Course o2) {
//                    int i = o1.getStart() - o2.getStart();
//                    return i;
//                }
//            });
        }
    }

}
