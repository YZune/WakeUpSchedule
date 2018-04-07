package com.suda.yzune.wakeupschedule.view;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.model.bean.AppWidgetBean;
import com.suda.yzune.wakeupschedule.model.bean.Course;
import com.suda.yzune.wakeupschedule.service.ScheduleWidgetService;
import com.suda.yzune.wakeupschedule.utils.CourseUtils;
import com.suda.yzune.wakeupschedule.utils.DaoUtils;
import com.suda.yzune.wakeupschedule.utils.DisplayUtil;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Implementation of App Widget functionality.
 */
public class ScheduleWidget extends AppWidgetProvider {

    RemoteViews mRemoteViews;
    int classNum;
    public static final String ITEM_CLICK = "schedule.TYPE_LIST";

    static String startDay;
    public static List courseData[] = new ArrayList[7];
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

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        DaoUtils.init(context);
        if (DaoUtils.getWidgetInstance().daoSession.load(AppWidgetBean.class, (long)appWidgetIds[0]) == null){
            DaoUtils.getWidgetInstance().daoSession.insert(new AppWidgetBean((long) appWidgetIds[0], 0));
        }
        List<AppWidgetBean> beanList = DaoUtils.getWidgetInstance().daoSession.loadAll(AppWidgetBean.class);
        for (AppWidgetBean a : beanList) {
            if (a.getType() == 0) {
                mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.schedule_widget);
                initData(context, mRemoteViews);
                Intent lvIntent = new Intent(context, ScheduleWidgetService.class);
                mRemoteViews.setRemoteAdapter(R.id.lv_schedule, lvIntent);
                Intent intent = new Intent(ITEM_CLICK);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mRemoteViews.setPendingIntentTemplate(R.id.lv_schedule, pendingIntent);
                appWidgetManager.notifyAppWidgetViewDataChanged(a.getId().intValue(), R.id.lv_schedule);
                appWidgetManager.updateAppWidget(a.getId().intValue(), mRemoteViews);
            }
        }
    }

    public void initData(Context context, RemoteViews views) {
        try {
            whichWeek = countWeek(context);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        startDay = SharedPreferencesUtils.getStringFromSP(context, "termStart", "2018-03-05");
        classNum = SharedPreferencesUtils.getIntFromSP(context, "classNum", 11);
        refresh(context, views);
        //Toasty.success(context, whichWeek+"周").show();
    }

    public void refresh(Context context, RemoteViews views) {
        itemHeight = DisplayUtil.dip2px(context, SharedPreferencesUtils.getIntFromSP(context, "item_height", 56));
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
        List<Course> allCourse = gson.fromJson(SharedPreferencesUtils.getStringFromSP(context, "course", ""), new TypeToken<List<Course>>() {
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

        if (courseData[6].size() == 0) {
            views.setViewVisibility(R.id.title7, View.GONE);
        }
    }


    public int daysBetween(Context context) throws ParseException {
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

    public int countWeek(Context context) throws ParseException {
        //Log.d("日期", (daysBetween(smdate, bdate) / 7 + 1) + "");
        return (daysBetween(context) / 7) + 1;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(ITEM_CLICK)) {
            DaoUtils.init(context);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            List<AppWidgetBean> beanList = DaoUtils.getWidgetInstance().daoSession.loadAll(AppWidgetBean.class);
            for (AppWidgetBean a : beanList) {
                if (a.getType() == 0) {
                    mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.schedule_widget);
                    initData(context, mRemoteViews);
                    Intent lvIntent = new Intent(context, ScheduleWidgetService.class);
                    mRemoteViews.setRemoteAdapter(R.id.lv_schedule, lvIntent);
                    Intent intent1 = new Intent(ITEM_CLICK);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                    mRemoteViews.setPendingIntentTemplate(R.id.lv_schedule, pendingIntent);
                    appWidgetManager.notifyAppWidgetViewDataChanged(a.getId().intValue(), R.id.lv_schedule);
                    appWidgetManager.updateAppWidget(a.getId().intValue(), mRemoteViews);
                }
            }
            Toasty.success(context, "小部件刷新成功").show();
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        DaoUtils.init(context);
        for (int i : appWidgetIds) {
            DaoUtils.getWidgetInstance().daoSession.getAppWidgetBeanDao().deleteByKey((long) i);
        }
    }
}

