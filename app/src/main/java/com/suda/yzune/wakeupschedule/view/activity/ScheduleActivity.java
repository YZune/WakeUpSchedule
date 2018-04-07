package com.suda.yzune.wakeupschedule.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.florent37.glidepalette.GlidePalette;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.adapter.SchedulePagerAdapter;
import com.suda.yzune.wakeupschedule.model.bean.Course;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;
import com.suda.yzune.wakeupschedule.utils.UpdateUtil;
import com.suda.yzune.wakeupschedule.utils.ViewUtil;
import com.suda.yzune.wakeupschedule.view.fragment.ScheduleFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ScheduleActivity extends AppCompatActivity {

    TextView title;
    ImageButton ib_recatch, ib_nav, ib_edit, ib_save;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ImageView iv_background, iv_nav;
    FloatingActionButton fab_add;
    EditText input_name, input_room, input_teacher, input_day, input_start_index, input_end_index, input_startWeek, input_endWeek, input_odd;
    SeekBar sb_week;
    TextView tv_day;
    View headerLayout, v_bg;
    static int chooseSchool = 0;

    static String startDay;
    String today;
    int whichWeek;

    private ViewPager viewPager;
    private SchedulePagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtil.fullScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        initData();
        initView();
        initEvent();

        if (SharedPreferencesUtils.getBooleanFromSP(this, "s_update", true)) {
            UpdateUtil.checkUpdate(ScheduleActivity.this, 0);
        }
    }

    public void initData() {
        chooseSchool = SharedPreferencesUtils.getIntFromSP(ScheduleActivity.this, "chooseSchool");
        if (chooseSchool == 0) {
            startActivity(new Intent(ScheduleActivity.this, IntroActivity.class));
            finish();
        }
        startDay = SharedPreferencesUtils.getStringFromSP(ScheduleActivity.this, "termStart", "2018-03-05");
        try {
            whichWeek = countWeek(ScheduleActivity.this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("M月d日");
        String todayTime = sdf.format(new Date());// 获取当前的日期
        today = todayTime + "  " + getWeekday();
    }

    public void initView() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        title = (TextView) findViewById(R.id.title);
        iv_background = (ImageView) findViewById(R.id.iv_background);
        tv_day = (TextView) findViewById(R.id.tv_day);
        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        ib_edit = (ImageButton) findViewById(R.id.ib_edit);
        ib_save = (ImageButton) findViewById(R.id.ib_save);
        ib_recatch = (ImageButton) findViewById(R.id.ib_recatch);
        ib_nav = (ImageButton) findViewById(R.id.ib_nav);
        sb_week = (SeekBar) findViewById(R.id.sb_week);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        headerLayout = navigationView.getHeaderView(0);
        iv_nav = headerLayout.findViewById(R.id.headerImageView);
        v_bg = headerLayout.findViewById(R.id.v_bg);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        initNav();
        tv_day.setText(today);
        refreshSeekBar();

        //<editor-fold desc="如果课程表里面还是空的">
        if (SharedPreferencesUtils.getStringFromSP(getApplicationContext(), "course", "").equals("")) {
            fab_add.setVisibility(View.VISIBLE);
            //floatingActionsMenu.setVisibility(View.VISIBLE);
            sb_week.setVisibility(View.INVISIBLE);
            ib_recatch.setVisibility(View.INVISIBLE);
            ib_nav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawerLayout.openDrawer(Gravity.START);
                }
            });

        }
        //</editor-fold>

        //<editor-fold desc="如果有课表了">
        else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.containsKey("who")) {
                int who = bundle.getInt("who");
                switch (who) {
                    case 1:
                        Toasty.success(getApplicationContext(), "课表获取成功，以后不需要联网啦，可以切换回流量~").show();
                        break;
                }
            }

            if (chooseSchool == 2) {
                ib_recatch.setVisibility(View.GONE);
                ib_edit.setVisibility(View.VISIBLE);
                ib_save.setVisibility(View.GONE);
            }
        }

        //</editor-fold>
        mAdapter = new SchedulePagerAdapter(getSupportFragmentManager());
        if (!SharedPreferencesUtils.getBooleanFromSP(ScheduleActivity.this, "is_show_donate", false)) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setTitle("嘿");
            builder.setMessage("非常感谢你选择使用WakeUp课程表，随意捐赠了解一下~");
            builder.setPositiveButton("先去看看", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startA(ScheduleActivity.this, DonateActivity.class);
                    SharedPreferencesUtils.saveBooleanToSP(ScheduleActivity.this, "is_show_donate", true);
                }
            });
            builder.setNegativeButton("不了不了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferencesUtils.saveBooleanToSP(ScheduleActivity.this, "is_show_donate", true);
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }

    public void initEvent() {
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    sb_week.setProgress(position + 1, true);
                } else {
                    sb_week.setProgress(position + 1);
                }
                whichWeek = sb_week.getProgress();
                try {
                    if (whichWeek == countWeek(ScheduleActivity.this)) {
                        tv_day.setText(today);
                    } else {
                        tv_day.setText("点击此处以回到本周");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrolled(int a, float b, int c) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chooseSchool == 1) {
                    startAFinish(ScheduleActivity.this, LoginXKActivity.class);
                } else {
                    //Todo 弹出添加课程的对话框
                    addCourseDialog(ScheduleActivity.this);
                }
                //finish();
            }
        });

        ib_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_add.show();
                ib_edit.setVisibility(View.GONE);
                ib_save.setVisibility(View.VISIBLE);
            }
        });

        ib_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_add.hide();
                ib_save.setVisibility(View.GONE);
                ib_edit.setVisibility(View.VISIBLE);
            }
        });

        ib_recatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAFinish(ScheduleActivity.this, LoginXKActivity.class);
            }
        });

        ib_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        sb_week.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                title.setText("第" + progress + "周");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                whichWeek = seekBar.getProgress();
                try {
                    if (whichWeek == countWeek(ScheduleActivity.this)) {
                        tv_day.setText(today);
                    } else {
                        tv_day.setText("点击此处以回到本周");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                viewPager.setCurrentItem(whichWeek - 1);
            }
        });
        tv_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    whichWeek = countWeek(ScheduleActivity.this);
                    refreshSeekBar();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                tv_day.setText(today);
                viewPager.setCurrentItem(whichWeek - 1);
            }
        });
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    whichWeek = countWeek(ScheduleActivity.this);
                    refreshSeekBar();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                tv_day.setText(today);
                viewPager.setCurrentItem(whichWeek - 1);
            }
        });
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

    public void initNav() {
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Log.d("侧栏", menuItem.getTitle().toString());
                switch (menuItem.getItemId()) {
                    case R.id.nav_feedback:
                        //menuItem.setChecked(false);
                        //drawerLayout.closeDrawers();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        drawerLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isQQClientAvailable(getApplicationContext())) {
                                    final String qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=1055614742&version=1";
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)));
                                } else {
                                    Toasty.error(getApplicationContext(), "手机上没有安装QQ，无法启动聊天窗口:-(").show();
                                }
                            }
                        }, 360);
                        break;
                    case R.id.nav_fix:
                        //menuItem.setChecked(false);
                        //drawerLayout.closeDrawers();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        drawerLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startA(ScheduleActivity.this, FixActivity.class);
                            }
                        }, 360);
                        break;
                    case R.id.nav_about:
                        //menuItem.setChecked(false);
                        //drawerLayout.closeDrawers();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        drawerLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startA(ScheduleActivity.this, AboutActivity.class);
                            }
                        }, 360);
                        break;
                    case R.id.nav_donate:
                        //menuItem.setChecked(false);
                        //drawerLayout.closeDrawers();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        drawerLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startA(ScheduleActivity.this, DonateActivity.class);
                            }
                        }, 360);
                        break;
                    case R.id.nav_otherCourse:
                        //menuItem.setChecked(false);
                        //drawerLayout.closeDrawers();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        if (chooseSchool == 1) {
                            drawerLayout.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startA(ScheduleActivity.this, ViewOtherCourse.class);
                                }
                            }, 360);
                        } else {
                            Toasty.warning(ScheduleActivity.this, "蹭课功能目前仅对苏大学生开放_(:з)∠)_").show();
                        }

                        break;
                    case R.id.nav_update:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        drawerLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                UpdateUtil.checkUpdate(ScheduleActivity.this, 1);
                            }
                        }, 360);
                        break;
                    case R.id.nav_today:
                        //menuItem.setChecked(false);
                        //drawerLayout.closeDrawers();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        if (SharedPreferencesUtils.getStringFromSP(ScheduleActivity.this, "course", "").equals("")) {
                            Toasty.error(ScheduleActivity.this, "你还没有添加课程哦_(:з)∠)_").show();
                        } else {
                            drawerLayout.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startA(ScheduleActivity.this, TodayCourseActivity.class);
                                }
                            }, 360);
                        }
                        break;
                    case R.id.nav_setting:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        drawerLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startA(ScheduleActivity.this, SettingsActivity.class);
                            }
                        }, 360);
                        break;
                    case R.id.nav_share:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        drawerLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startA(ScheduleActivity.this, ShareActivity.class);
                            }
                        }, 360);
                        break;
                    case R.id.nav_changeBg:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        drawerLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startA(ScheduleActivity.this, ThemeSettingsActivity.class);
                            }
                        }, 360);
                        break;
                    case R.id.nav_joinUs:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        drawerLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog dialog = new AlertDialog.Builder(ScheduleActivity.this)
                                        .setView(R.layout.dialog_joinus)
                                        .setCancelable(false)
                                        .setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setPositiveButton("加入我们", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (chooseSchool) {
                                                    case 0:
                                                        if (joinQQGroup("lAMxNCYJAYdkOOJl0wD9efB5IBHDKzQJ")) {
                                                            Toasty.success(getApplicationContext(), "谢谢支持").show();
                                                        } else {
                                                            Toasty.error(getApplicationContext(), "手机上没有安装QQ，无法启动聊天窗口:-(").show();
                                                        }
                                                        break;
                                                    case 1:
                                                        if (joinQQGroup("lAMxNCYJAYdkOOJl0wD9efB5IBHDKzQJ")) {
                                                            Toasty.success(getApplicationContext(), "谢谢支持").show();
                                                        } else {
                                                            Toasty.error(getApplicationContext(), "手机上没有安装QQ，无法启动聊天窗口:-(").show();
                                                        }
                                                        break;
                                                    case 2:
                                                        if (joinQQGroup("Pri3Cx8x9AH0HS-C4v9hpSPGODop14Oc")) {
                                                            Toasty.success(getApplicationContext(), "谢谢支持").show();
                                                        } else {
                                                            Toasty.error(getApplicationContext(), "手机上没有安装QQ，无法启动聊天窗口:-(").show();
                                                        }
                                                        break;
                                                }
                                            }
                                        }).create();
                                dialog.show();
                                TextView tv_about = (TextView) dialog.findViewById(R.id.tv_about);
                                tv_about.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent();
                                        intent.setAction("android.intent.action.VIEW");
                                        Uri content_url = Uri.parse("https://mp.weixin.qq.com/s?__biz=MzAwNzg3NzAzOA==&mid=2247484322&idx=1&sn=12ed8a4201692a3cb709b25eb9284ead&chksm=9b763b63ac01b27590884c10489ad0f6cea38d9ee88a42708c949370ac2e64e9ed8eb1763af7&mpshare=1&scene=23&srcid=0916Mb5NRPEs0TO3s0mnJciH#rd");
                                        intent.setData(content_url);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }, 360);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("状态", "开始");
        startDay = SharedPreferencesUtils.getStringFromSP(ScheduleActivity.this, "termStart", "2018-03-05");
        if (!SharedPreferencesUtils.getStringFromSP(getApplicationContext(), "course", "").equals("")) {
            try {
                whichWeek = countWeek(ScheduleActivity.this);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            refresh();
            refreshSeekBar();
            tv_day.setText(today);
        }
        String Image_URI = SharedPreferencesUtils.getStringFromSP(ScheduleActivity.this, "pic_uri", "");
        if (!Image_URI.equals("")) {
            showImage(Uri.parse(Image_URI));
        }
        String Image_Nav_URI = SharedPreferencesUtils.getStringFromSP(ScheduleActivity.this, "nav_pic_uri", "");
        if (!Image_Nav_URI.equals("")) {
            Glide.with(ScheduleActivity.this).load(Image_Nav_URI).listener(
                    GlidePalette
                            .with(Image_Nav_URI)
                            .use(GlidePalette.Profile.MUTED_DARK).intoBackground(v_bg)
                            .use(GlidePalette.Profile.VIBRANT)
                            .intoCallBack(
                                    new GlidePalette.CallBack() {
                                        @Override
                                        public void onPaletteLoaded(Palette palette) {
                                            navigationView.setItemIconTintList(ColorStateList.valueOf(palette.getVibrantColor(getResources().getColor(R.color.colorAccent))));
                                        }
                                    })).into(iv_nav);
        }
        if (SharedPreferencesUtils.getBooleanFromSP(this, "s_seekbar", true)) {
            sb_week.setVisibility(View.VISIBLE);
        } else {
            sb_week.setVisibility(View.INVISIBLE);
        }
        if (SharedPreferencesUtils.getBooleanFromSP(this, "s_color", false)) {
            title.setTextColor(getResources().getColor(R.color.white));
            tv_day.setTextColor(getResources().getColor(R.color.white));
            ib_edit.setColorFilter(getResources().getColor(R.color.white));
            ib_nav.setColorFilter(getResources().getColor(R.color.white));
            ib_recatch.setColorFilter(getResources().getColor(R.color.white));
            ib_save.setColorFilter(getResources().getColor(R.color.white));
        } else {
            title.setTextColor(getResources().getColor(R.color.black));
            tv_day.setTextColor(getResources().getColor(R.color.black));
            ib_edit.setColorFilter(getResources().getColor(R.color.black));
            ib_nav.setColorFilter(getResources().getColor(R.color.black));
            ib_recatch.setColorFilter(getResources().getColor(R.color.black));
            ib_save.setColorFilter(getResources().getColor(R.color.black));
        }
    }

    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    public void refresh() {
        if (whichWeek >= 1) {
            title.setText("第" + whichWeek + "周");
        } else {
            title.setText("第1周");
            whichWeek = 1;
        }
        mAdapter.removeAll();
        Bundle bundle = new Bundle();
        //Toasty.success(this, whichWeek + "周").show();
        for (int i = 1; i <= 25; i++) {
            bundle.putInt("week", i);
            mAdapter.addFragment(ScheduleFragment.newInstance(i));
        }
        mAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(whichWeek - 1);
    }

    public void refreshSeekBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sb_week.setProgress(whichWeek, true);
        } else {
            sb_week.setProgress(whichWeek);
        }
    }

    public void showImage(Uri uri) {
        Glide.with(ScheduleActivity.this).load(uri).into(iv_background);
    }

    public static int daysBetween(Context context) throws ParseException {
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

    public static int countWeek(Context context) throws ParseException {
        //Log.d("日期", (daysBetween(smdate, bdate) / 7 + 1) + "");
        return (daysBetween(context) / 7) + 1;
    }

    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void startA(Context mContext, Class<?> c) {
        Intent intent = new Intent(mContext, c);
        mContext.startActivity(intent);
        //((Activity)mContext).finish();
    }

    public static void startAFinish(Context mContext, Class<?> c) {
        Intent intent = new Intent(mContext, c);
        mContext.startActivity(intent);
        ((Activity) mContext).finish();
    }

    public void addCourseDialog(final Context context) {
        final List<Course> allCourse;
        final Gson gson = new Gson();
        if (!SharedPreferencesUtils.getStringFromSP(context, "course", "").equals("")) {
            allCourse = gson.fromJson(SharedPreferencesUtils.getStringFromSP(getApplicationContext(), "course", ""), new TypeToken<List<Course>>() {
            }.getType());
        } else {
            allCourse = new ArrayList<Course>();
        }
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_add)
                .setCancelable(true)
                .setPositiveButton("确定", null)
                .create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = input_name.getText().toString();
                String room = input_room.getText().toString();
                String teacher = input_teacher.getText().toString();
                String _day = input_day.getText().toString();
                String _startIndex = input_start_index.getText().toString();
                String _endIndex = input_end_index.getText().toString();
                String _startWeek = input_startWeek.getText().toString();
                String _endWeek = input_endWeek.getText().toString();
                String _isOdd = input_odd.getText().toString();
                int day, startIndex, endIndex, startWeek, endWeek, isOdd;
                if (!(name.equals("") || _day.equals("") || _startIndex.equals("") || _endIndex.equals("") || _startWeek.equals("") || _endWeek.equals("") || _isOdd.equals(""))) {
                    day = Integer.valueOf(input_day.getText().toString());
                    startIndex = Integer.valueOf(input_start_index.getText().toString());
                    endIndex = Integer.valueOf(input_end_index.getText().toString());
                    startWeek = Integer.valueOf(input_startWeek.getText().toString());
                    endWeek = Integer.valueOf(input_endWeek.getText().toString());
                    isOdd = Integer.valueOf(input_odd.getText().toString());
                    if (day >= 1 && day <= 7 && endIndex >= startIndex && endWeek >= startWeek && startIndex != 0 && startWeek != 0 && (isOdd == 0 || isOdd == 1 || isOdd == 2)) {
                        allCourse.add(new Course(name, room, startIndex, endIndex - startIndex + 1, teacher, "", day, startWeek, endWeek, isOdd));
                        SharedPreferencesUtils.saveStringToSP(context, "course", gson.toJson(allCourse));
                        Toasty.success(context, "添加成功").show();
                        //todo
                        mAdapter.notifyDataSetChanged();
                        sb_week.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                        if (!SharedPreferencesUtils.getStringFromSP(context, "course", "").equals("")) {
                            refresh();
                        }
                    } else {
                        Toasty.error(ScheduleActivity.this, "请认真填写_(:з)∠)_").show();
                    }
                } else {
                    Toasty.error(ScheduleActivity.this, "请认真填写_(:з)∠)_").show();
                }
                return;
            }
        });

        input_name = (EditText) dialog.findViewById(R.id.et_courseName);
        input_room = (EditText) dialog.findViewById(R.id.et_room);
        input_teacher = (EditText) dialog.findViewById(R.id.et_teacher);
        input_day = (EditText) dialog.findViewById(R.id.et_day);
        input_start_index = (EditText) dialog.findViewById(R.id.et_start);
        input_end_index = (EditText) dialog.findViewById(R.id.et_end);
        input_startWeek = (EditText) dialog.findViewById(R.id.et_startWeek);
        input_endWeek = (EditText) dialog.findViewById(R.id.et_endWeek);
        input_odd = (EditText) dialog.findViewById(R.id.et_odd);
    }

}
