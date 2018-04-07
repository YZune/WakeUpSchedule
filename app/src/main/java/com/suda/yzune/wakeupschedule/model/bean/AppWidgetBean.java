package com.suda.yzune.wakeupschedule.model.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yzune on 2018/3/24.
 */

@Entity
public class AppWidgetBean {

    @Id
    private Long id;

    private int type;

    @Generated(hash = 1409930062)
    public AppWidgetBean(Long id, int type) {
        this.id = id;
        this.type = type;
    }

    @Generated(hash = 1977486489)
    public AppWidgetBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
