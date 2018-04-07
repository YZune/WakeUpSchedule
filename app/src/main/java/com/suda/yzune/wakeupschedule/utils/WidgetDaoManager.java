package com.suda.yzune.wakeupschedule.utils;

import android.content.Context;

import com.suda.yzune.wakeupschedule.model.bean.AppWidgetBean;

/**
 * Created by yzune on 2018/3/24.
 */

public class WidgetDaoManager extends BaseDao<AppWidgetBean>{
    public WidgetDaoManager(Context context) {
        super(context);
    }
}