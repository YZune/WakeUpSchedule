package com.suda.yzune.wakeupschedule.view;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.model.bean.AppWidgetBean;
import com.suda.yzune.wakeupschedule.service.ListViewService;
import com.suda.yzune.wakeupschedule.utils.DaoUtils;

import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Implementation of App Widget functionality.
 */
public class TodayWidget extends AppWidgetProvider {

    private RemoteViews mRemoteViews;
    public static final String ITEM_CLICK = "day.TYPE_LIST";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        DaoUtils.init(context);
        if (DaoUtils.getWidgetInstance().daoSession.load(AppWidgetBean.class, (long)appWidgetIds[0]) == null){
            DaoUtils.getWidgetInstance().daoSession.insert(new AppWidgetBean((long) appWidgetIds[0], 1));
        }
        List<AppWidgetBean> beanList = DaoUtils.getWidgetInstance().daoSession.loadAll(AppWidgetBean.class);
        for (AppWidgetBean a : beanList) {
            if (a.getType() == 1) {
                mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.today_widget);
                mRemoteViews.setTextViewText(R.id.widget_week, getWeekday());
                Intent lvIntent = new Intent(context, ListViewService.class);
                mRemoteViews.setRemoteAdapter(R.id.lv_test, lvIntent);
                mRemoteViews.setEmptyView(R.id.lv_test, android.R.id.empty);
                Intent intent = new Intent(ITEM_CLICK);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mRemoteViews.setPendingIntentTemplate(R.id.lv_test, pendingIntent);
                appWidgetManager.notifyAppWidgetViewDataChanged(a.getId().intValue(), R.id.lv_test);
                appWidgetManager.updateAppWidget(a.getId().intValue(), mRemoteViews);
            }
        }
    }

    public String getWeekday() {
        String str = "";
        int weekDay = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK);
        if (weekDay == 1) {
            weekDay = 7;
        } else {
            weekDay = weekDay - 1;
        }
        switch (weekDay) {
            case 1:
                str = "周一";
                break;
            case 2:
                str = "周二";
                break;
            case 3:
                str = "周三";
                break;
            case 4:
                str = "周四";
                break;
            case 5:
                str = "周五";
                break;
            case 6:
                str = "周六";
                break;
            case 7:
                str = "周日";
                break;
        }
        return str;
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        DaoUtils.init(context);
        for (int i : appWidgetIds) {
            DaoUtils.getWidgetInstance().daoSession.getAppWidgetBeanDao().deleteByKey((long) i);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(ITEM_CLICK)) {
            DaoUtils.init(context);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            List<AppWidgetBean> beanList = DaoUtils.getWidgetInstance().daoSession.loadAll(AppWidgetBean.class);
            for (AppWidgetBean a : beanList) {
                if (a.getType() == 1) {
                    mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.today_widget);
                    mRemoteViews.setTextViewText(R.id.widget_week, getWeekday());
                    Intent lvIntent = new Intent(context, ListViewService.class);
                    mRemoteViews.setRemoteAdapter(R.id.lv_test, lvIntent);
                    mRemoteViews.setEmptyView(R.id.lv_test, android.R.id.empty);
                    Intent intent1 = new Intent(ITEM_CLICK);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                    mRemoteViews.setPendingIntentTemplate(R.id.lv_test, pendingIntent);
                    appWidgetManager.notifyAppWidgetViewDataChanged(a.getId().intValue(), R.id.lv_test);
                    appWidgetManager.updateAppWidget(a.getId().intValue(), mRemoteViews);
                }
            }
            Toasty.success(context, "小部件刷新成功").show();
        }
    }

}

