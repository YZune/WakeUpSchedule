package com.suda.yzune.wakeupschedule.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.model.bean.Course;
import com.suda.yzune.wakeupschedule.utils.CourseUtils;
import com.suda.yzune.wakeupschedule.utils.DisplayUtil;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;
import com.suda.yzune.wakeupschedule.view.ScheduleWidget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by yzune on 2018/3/24.
 */

public class ScheduleWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ScheduleRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    private class ScheduleRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context mContext;

        private List<String> mList = new ArrayList<>();

        RemoteViews mRemoteViews;

        LinearLayout weekPanels[] = new LinearLayout[7];
        int classNum;

        String startDay;
        private List courseData[] = new ArrayList[7];
        int itemHeight;
        int marTop, marLeft;
        int whichWeek;
        private List<Course> day1 = new ArrayList<Course>();
        private List<Course> day2 = new ArrayList<Course>();
        private List<Course> day3 = new ArrayList<Course>();
        private List<Course> day4 = new ArrayList<Course>();
        private List<Course> day5 = new ArrayList<Course>();
        private List<Course> day6 = new ArrayList<Course>();
        private List<Course> day7 = new ArrayList<Course>();

        public ScheduleRemoteViewsFactory(Context context, Intent intent) {
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
            getCourseString(mContext);
            Log.d("小部件", "要更新了");
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
            mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.item_schedule_widget);
            initView(mContext, mRemoteViews);
            initData(mContext, mRemoteViews, position);
            Intent intent = new Intent(ScheduleWidget.ITEM_CLICK);
            mRemoteViews.setOnClickFillInIntent(R.id.ll_contentPanel, intent);
            return mRemoteViews;
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

        public void initView(Context context, RemoteViews views) {
            if (SharedPreferencesUtils.getStringFromSP(context, "course", "").equals("")) {
                views.setViewVisibility(R.id.none_tip, View.VISIBLE);
                views.setViewVisibility(R.id.tv_10, View.VISIBLE);
                views.setViewVisibility(R.id.tv_11, View.VISIBLE);
                views.setViewVisibility(R.id.tv_12, View.VISIBLE);
                views.setViewVisibility(R.id.tv_13, View.VISIBLE);
                views.setViewVisibility(R.id.tv_14, View.VISIBLE);
                views.setViewVisibility(R.id.tv_15, View.VISIBLE);
                views.setViewVisibility(R.id.tv_16, View.VISIBLE);
            }
        }

        public void initData(Context context, RemoteViews views, int position) {
            try {
                whichWeek = countWeek(context);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            startDay = SharedPreferencesUtils.getStringFromSP(context, "termStart", "2018-03-05");
            classNum = SharedPreferencesUtils.getIntFromSP(context, "classNum", 11);
            refresh(context, views, position);
            //Toasty.success(context, whichWeek+"周").show();
        }

        private void refresh(Context context, RemoteViews views, int position) {
            itemHeight = DisplayUtil.dip2px(context, 48);
            for (int i = 0; i <= 16; i++) {
                views.setInt(R.id.tv_1 + i, "setHeight", itemHeight);
            }
            switch (classNum) {
                case 8:
                    views.setViewVisibility(R.id.tv_10, View.GONE);
                case 9:
                    views.setViewVisibility(R.id.tv_11, View.GONE);
                case 10:
                    views.setViewVisibility(R.id.tv_12, View.GONE);
                case 11:
                    views.setViewVisibility(R.id.tv_13, View.GONE);
                case 12:
                    views.setViewVisibility(R.id.tv_14, View.GONE);
                case 13:
                    views.setViewVisibility(R.id.tv_15, View.GONE);
                case 14:
                    views.setViewVisibility(R.id.tv_16, View.GONE);
                    break;
            }
            //Log.d("高度", DisplayUtil.dip2px(context, 56)+"");
            marTop = context.getResources().getDimensionPixelSize(R.dimen.weekItemMarTop);
            marLeft = context.getResources().getDimensionPixelSize(R.dimen.weekItemMarLeft);
            Gson gson = new Gson();
            List<Course> allCourse = gson.fromJson(mList.get(position), new TypeToken<List<Course>>() {
            }.getType());
            day1.clear();
            day2.clear();
            day3.clear();
            day4.clear();
            day5.clear();
            day6.clear();
            day7.clear();
            for (Course course : allCourse) {
                String oldRoom = course.getRoom();
                if (oldRoom.contains("</a>")) {
                    course.setRoom(oldRoom.substring(0, oldRoom.indexOf("</a>")));
                }
                if (course.getIsOdd() == 1 && whichWeek % 2 == 0) {
                    continue;
                }
                if (course.getIsOdd() == 2 && whichWeek % 2 != 0) {
                    continue;
                }
                if ((course.getStartWeek() > whichWeek || course.getEndWeek() < whichWeek)) {
                    continue;
                }
                switch (course.getDay()) {
                    case 1:
                        day1.add(course);
                        break;
                    case 2:
                        day2.add(course);
                        break;
                    case 3:
                        day3.add(course);
                        break;
                    case 4:
                        day4.add(course);
                        break;
                    case 5:
                        day5.add(course);
                        break;
                    case 6:
                        day6.add(course);
                        break;
                    case 7:
                        day7.add(course);
                        break;
                }
            }
            //Log.d("处理", "旧"+gson.toJson(day1));
            courseData[0] = CourseUtils.makeCourseTogether(day1);
            courseData[1] = CourseUtils.makeCourseTogether(day2);
            courseData[2] = CourseUtils.makeCourseTogether(day3);
            courseData[3] = CourseUtils.makeCourseTogether(day4);
            courseData[4] = CourseUtils.makeCourseTogether(day5);
            courseData[5] = CourseUtils.makeCourseTogether(day6);
            courseData[6] = CourseUtils.makeCourseTogether(day7);

            for (int i = 0; i < 7; i++) {
                Collections.sort(courseData[i], new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        if (((Course) o1).getStart() < ((Course) o2).getStart()) {
                            return -1;
                        } else if (((Course) o1).getStart() > ((Course) o2).getStart()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });
            }

            for (int i = 0; i < weekPanels.length; i++) {
                views.removeAllViews(R.id.weekPanel_1 + i);
                initWeekPanel(R.id.weekPanel_1 + i, courseData[i], views, context);
            }
            if (courseData[6].size() == 0) {
                views.setViewVisibility(R.id.title7, View.GONE);
                views.setViewVisibility(R.id.weekPanel_7, View.GONE);
            }
        }

        private void initWeekPanel(int ll_id, List<Course> data, RemoteViews views, Context context) {
            if (data == null || data.size() < 1) return;
            Log.i("Msg", "小部件初始化面板");
            Course pre = data.get(0);
            for (int i = 0; i < data.size(); i++) {
                final Course c = data.get(i);
                RemoteViews tv = new RemoteViews(context.getPackageName(), R.layout.item_textview);
                tv.setInt(R.id.item_tv, "setHeight", itemHeight * c.getStep() + marTop * (c.getStep() - 1));
                if (i > 0) {
                    RemoteViews tv1 = new RemoteViews(context.getPackageName(), R.layout.item_textview);
                    //tv.setInt(R.id.tv_left, "setWidth", marLeft);
                    tv1.setInt(R.id.item_tv, "setHeight", (c.getStart() - (pre.getStart() + pre.getStep())) * (itemHeight + marTop) + marTop);
                    views.addView(ll_id, tv1);
                    //tv.setTextViewCompoundDrawables(R.id.item_tv, marLeft, (c.getStart() - (pre.getStart() + pre.getStep())) * (itemHeight + marTop) + marTop, 0, 0);
                    //lp.setMargins(marLeft, (c.getStart() - (pre.getStart() + pre.getStep())) * (itemHeight + marTop) + marTop, 0, 0);
                } else {
                    RemoteViews tv1 = new RemoteViews(context.getPackageName(), R.layout.item_textview);
                    //tv.setInt(R.id.tv_left, "setWidth", marLeft);
                    tv1.setInt(R.id.item_tv, "setHeight", (c.getStart() - 1) * (itemHeight + marTop) + marTop);
                    views.addView(ll_id, tv1);
                    //tv.setTextViewCompoundDrawables(R.id.item_tv, marLeft, (c.getStart() - 1) * (itemHeight + marTop) + marTop, 0, 0);
                    //lp.setMargins(marLeft, (c.getStart() - 1) * (itemHeight + marTop) + marTop, 0, 0);
                }
                //tv.setInt(R.id.item_tv, "setGravity", Gravity.CENTER_VERTICAL);
                //tv.setInt(R.id.item_tv, "setGravity", Gravity.CENTER_HORIZONTAL);

                //tv.setBackgroundColor(getResources().getColor(R.color.classIndex));
                int colorNum = c.getName().charAt(0) % 10;
                Log.d("数字", "" + colorNum);
                GradientDrawable myGrad = (GradientDrawable) context.getResources().getDrawable(R.drawable.lessonbackground);
                float _a = SharedPreferencesUtils.getIntFromSP(context, "sb_widget_alpha", 64);
                String a = "";
                if (Math.round(255*(_a/100)) != 0){
                    a = Integer.toHexString(Math.round(255*(_a/100)));
                }else {
                    a = "00";
                }


                if (c.getId().equals("")) {
                    switch (colorNum) {
                        case 0:
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"8af28d"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.one));
                            break;
                        case 1:
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"00ff9d"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.two));
                            break;
                        case 2:
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"ff6200"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.three));
                            break;
                        case 3:
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"e7554e"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.four));
                            break;
                        case 4:
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"4695ff"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.five));
                            break;
                        case 5:
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"ca71ff"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.six));
                            break;
                        case 6:
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"3daff1"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.seven));
                            break;
                        case 7:
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"5d5dff"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.eight));
                            break;
                        case 8:
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"80deea"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.nine));
                            break;
                        case 9:
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"2f88ff"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.ten));
                            break;
                    }
                } else {
                    switch (c.getId()) {
                        case "0":
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"8af28d"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.one));
                            break;
                        case "1":
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"00ff9d"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.two));
                            break;
                        case "2":
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"ff6200"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.three));
                            break;
                        case "3":
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"e7554e"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.four));
                            break;
                        case "4":
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"4695ff"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.five));
                            break;
                        case "5":
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"ca71ff"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.six));
                            break;
                        case "6":
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"3daff1"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.seven));
                            break;
                        case "7":
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"5d5dff"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.eight));
                            break;
                        case "8":
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"80deea"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.nine));
                            break;
                        case "9":
                            tv.setInt(R.id.item_tv, "setBackgroundColor", Color.parseColor("#"+a+"2f88ff"));
                            //tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.ten));
                            break;
                    }
                }

                switch (c.getIsOdd()) {
                    case 0:
                        tv.setTextViewText(R.id.item_tv, c.getName() + "@" + c.getRoom());
                        break;
                    case 1:
                        tv.setTextViewText(R.id.item_tv, c.getName() + "@" + c.getRoom() + "\n单周");
                        if (whichWeek % 2 == 0) {
                            if (SharedPreferencesUtils.getBooleanFromSP(context, "s_show", false)) {
                                tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.none));
                                tv.setTextViewText(R.id.item_tv, "[非本周]" + c.getName() + "@" + c.getRoom() + "\n单周");
                            } else {
                                tv.setViewVisibility(R.id.item_tv, View.GONE);
                            }
                        }
                        break;
                    case 2:
                        tv.setTextViewText(R.id.item_tv, c.getName() + "@" + c.getRoom() + "\n双周");
                        if (whichWeek % 2 != 0) {
                            if (SharedPreferencesUtils.getBooleanFromSP(context, "s_show", false)) {
                                tv.setInt(R.id.item_tv, "setBackgroundColor", context.getResources().getColor(R.color.none));
                                tv.setTextViewText(R.id.item_tv, "[非本周]" + c.getName() + "@" + c.getRoom() + "\n双周");
                            } else {
                                tv.setViewVisibility(R.id.item_tv, View.GONE);
                            }
                        }
                        break;
                }

                //tv.setImageViewBitmap(R.id.iv_bg, drawableToBitmap(myGrad));
                views.addView(ll_id, tv);
                pre = c;
            }
        }

        private int daysBetween(Context context) throws ParseException {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String todayTime = sdf.format(new Date());// 获取当前的日期
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(SharedPreferencesUtils.getStringFromSP(context, "termStart", "2018-03-05")));
            long time1 = cal.getTimeInMillis();
            cal.setTime(sdf.parse(todayTime));
            long time2 = cal.getTimeInMillis();
            long between_days = (time2 - time1) / (1000 * 3600 * 24);
            //Log.d("日期", Integer.parseInt(String.valueOf(between_days)) + "");
            return Integer.parseInt(String.valueOf(between_days));
        }

        private int countWeek(Context context) throws ParseException {
            //Log.d("日期", (daysBetween(smdate, bdate) / 7 + 1) + "");
            return (daysBetween(context) / 7) + 1;
        }

        public void getCourseString(Context context) {
            mList.clear();
            String sp = SharedPreferencesUtils.getStringFromSP(getApplicationContext(), "course", "");
            mList.add(sp);
        }
    }
}
