package com.suda.yzune.wakeupschedule.utils;

import android.content.Context;

/**
 * Created by yzune on 2018/3/24.
 */

public class DaoUtils{
    private  static WidgetDaoManager widgetDaoManager;
    public  static Context context;

    public static void init(Context context){
        DaoUtils.context = context.getApplicationContext();
    }

    public static WidgetDaoManager getWidgetInstance(){
        if (widgetDaoManager == null) {
            widgetDaoManager = new WidgetDaoManager(context);
        }
        return widgetDaoManager;
    }
}