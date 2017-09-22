package com.suda.yzune.wakeupschedule.view;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.model.bean.Course;
import com.suda.yzune.wakeupschedule.utils.CourseUtils;
import com.suda.yzune.wakeupschedule.utils.GetImageUtils;
import com.suda.yzune.wakeupschedule.utils.ImageUtil;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;
import com.xiaomi.market.sdk.UpdateResponse;
import com.xiaomi.market.sdk.UpdateStatus;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;
import com.xiaomi.market.sdk.XiaomiUpdateListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ScheduleActivity extends AppCompatActivity {

    LinearLayout weekPanels[] = new LinearLayout[7];
    TextView title, none_tip;
    ImageButton ib_recatch, ib_nav, ib_back, ib_more;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ImageView iv_background;
    FloatingActionButton fab_add;
    private Bitmap bitmap;
    private File tempFile;

    private static String startDay = "2017-9-4";
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
    private String[] startList = {"8:00", "9:00", "10:10", "11:10", "13:30", "14:30", "15:40", "16:40", "18:30", "19:30", "20:30"};
    private String[] endList = {"8:50", "9:50", "11:00", "12:00", "14:20", "15:20", "16:30", "17:30", "19:20", "20:20", "21:20"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.transparent));
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.transparent));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        try {
            whichWeek = countWeek();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        XiaomiUpdateAgent.update(this);

        if (SharedPreferencesUtils.getStringFromSP(getApplicationContext(), "course").equals("")) {
            none_tip = (TextView) findViewById(R.id.none_tip);
            fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
            ib_recatch = (ImageButton) findViewById(R.id.ib_recatch);
            ib_nav = (ImageButton) findViewById(R.id.ib_nav);
            ib_back = (ImageButton) findViewById(R.id.ib_back);
            ib_more = (ImageButton) findViewById(R.id.ib_more);
            none_tip.setVisibility(View.VISIBLE);
            fab_add.setVisibility(View.VISIBLE);
            ib_back.setVisibility(View.INVISIBLE);
            ib_more.setVisibility(View.INVISIBLE);
            ib_recatch.setVisibility(View.INVISIBLE);
            navigationView = (NavigationView) findViewById(R.id.navigation_view);
            drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

            ib_nav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawerLayout.openDrawer(Gravity.START);
                }
            });
            fab_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ScheduleActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
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
                                        Toast.makeText(getApplicationContext(), "手机上没有安装QQ，无法启动聊天窗口:-(", Toast.LENGTH_LONG).show();
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
                        case R.id.nav_today:
                            //menuItem.setChecked(false);
                            //drawerLayout.closeDrawers();
                            drawerLayout.closeDrawer(GravityCompat.START);
                            drawerLayout.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "请登录后再使用哦~", Toast.LENGTH_LONG).show();
                                }
                            }, 360);
                            break;
                        case R.id.nav_setting:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            drawerLayout.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "努力开发中(ง •_•)ง", Toast.LENGTH_LONG).show();
                                }
                            }, 360);
                            break;
                        case R.id.nav_changeBg:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            drawerLayout.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "请登录后再使用哦~", Toast.LENGTH_LONG).show();
                                }
                            }, 360);
                            break;
                        case R.id.nav_update:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            drawerLayout.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.VIEW");
                                    Uri content_url = Uri.parse("https://www.coolapk.com/apk/com.suda.yzune.wakeupschedule");
                                    intent.setData(content_url);
                                    startActivity(intent);
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
                                                    if (joinQQGroup("lAMxNCYJAYdkOOJl0wD9efB5IBHDKzQJ")) {
                                                        Toast.makeText(getApplicationContext(), "谢谢支持", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "手机上没有安装QQ，无法启动聊天窗口:-(", Toast.LENGTH_LONG).show();
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
        } else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.containsKey("who")) {
                int who = bundle.getInt("who");
                switch (who) {
                    case 1:
                        Toast.makeText(getApplicationContext(), "课表获取成功，以后不需要联网啦，可以切换回流量~", Toast.LENGTH_LONG).show();
                        break;
                }
            }


            String ImagePath = SharedPreferencesUtils.getStringFromSP(ScheduleActivity.this, "ImagePath");
            String ImageName = SharedPreferencesUtils.getStringFromSP(ScheduleActivity.this, "ImageName");
            if (!ImagePath.equals("")) {
                tempFile = new File(ImagePath, ImageName);
                showImage(ImageUtil.getImage(ImagePath + "/" + ImageName, 100));
            }

            refresh();

            ib_recatch = (ImageButton) findViewById(R.id.ib_recatch);
            ib_nav = (ImageButton) findViewById(R.id.ib_nav);
            ib_back = (ImageButton) findViewById(R.id.ib_back);
            ib_more = (ImageButton) findViewById(R.id.ib_more);
            ib_recatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAlert();
                }
            });

            navigationView = (NavigationView) findViewById(R.id.navigation_view);
            drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

            ib_nav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawerLayout.openDrawer(Gravity.START);
                }
            });

            ib_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (whichWeek > 1) {
                        whichWeek--;
                        title = (TextView) findViewById(R.id.title);
                        title.setText("第" + whichWeek + "周");
                        refresh();
                    } else {
                        Toast.makeText(ScheduleActivity.this, "不能再往前啦~", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ib_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whichWeek++;
                    title = (TextView) findViewById(R.id.title);
                    title.setText("第" + whichWeek + "周");
                    refresh();
                }
            });

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
                                        Toast.makeText(getApplicationContext(), "手机上没有安装QQ，无法启动聊天窗口:-(", Toast.LENGTH_LONG).show();
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
                        case R.id.nav_update:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            drawerLayout.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.VIEW");
                                    Uri content_url = Uri.parse("https://www.coolapk.com/apk/com.suda.yzune.wakeupschedule");
                                    intent.setData(content_url);
                                    startActivity(intent);
                                }
                            }, 360);
                            break;
                        case R.id.nav_today:
                            //menuItem.setChecked(false);
                            //drawerLayout.closeDrawers();
                            drawerLayout.closeDrawer(GravityCompat.START);
                            drawerLayout.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startA(ScheduleActivity.this, TodayCourseActivity.class);
                                }
                            }, 360);
                            break;
                        case R.id.nav_setting:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            drawerLayout.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "努力开发中(ง •_•)ง", Toast.LENGTH_LONG).show();
                                }
                            }, 360);
                            break;
                        case R.id.nav_changeBg:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            drawerLayout.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (ContextCompat.checkSelfPermission(ScheduleActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(ScheduleActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                                    } else {
                                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                "image/*");
                                        startActivityForResult(intent, GetImageUtils.PHOTO_PICK);
                                    }
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
                                                    if (joinQQGroup("lAMxNCYJAYdkOOJl0wD9efB5IBHDKzQJ")) {
                                                        Toast.makeText(getApplicationContext(), "谢谢支持", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "手机上没有安装QQ，无法启动聊天窗口:-(", Toast.LENGTH_LONG).show();
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*");
                    startActivityForResult(intent, GetImageUtils.PHOTO_PICK);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(ScheduleActivity.this, "你取消了授权，无法更换背景。", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GetImageUtils.PHOTO_CARMERA:
                if (null != data) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    ImageUtil.saveCommonCompressBitmapFile(bitmap, tempFile);
                    showImage(bitmap);
                }
                break;
            case GetImageUtils.PHOTO_PICK:
                if (null != data) {
                    Uri uri = data.getData();
                    ContentResolver cr = getContentResolver();
                    try {
                        bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        tempFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), GetImageUtils.getPhotoFileName());
                        ImageUtil.saveCommonCompressBitmapFile(bitmap, tempFile);
                        showImage(bitmap);
                        SharedPreferencesUtils.saveStringToSP(ScheduleActivity.this, "ImagePath", Environment.getExternalStorageDirectory().getAbsolutePath());
                        SharedPreferencesUtils.saveStringToSP(ScheduleActivity.this, "ImageName", GetImageUtils.getPhotoFileName());
                    } catch (FileNotFoundException e) {
                        Log.e("Exception", e.getMessage(), e);
                    }
                }
                break;

            default:
                break;
        }
    }

    public void refresh() {
        itemHeight = getResources().getDimensionPixelSize(R.dimen.weekItemHeight);
        marTop = getResources().getDimensionPixelSize(R.dimen.weekItemMarTop);
        marLeft = getResources().getDimensionPixelSize(R.dimen.weekItemMarLeft);
        Gson gson = new Gson();
        List<Course> allCourse = gson.fromJson(SharedPreferencesUtils.getStringFromSP(getApplicationContext(), "course"), new TypeToken<List<Course>>() {
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
        courseData[0] = CourseUtils.makeCourseTogether(day1);
        courseData[1] = CourseUtils.makeCourseTogether(day2);
        courseData[2] = CourseUtils.makeCourseTogether(day3);
        courseData[3] = CourseUtils.makeCourseTogether(day4);
        courseData[4] = CourseUtils.makeCourseTogether(day5);
        courseData[5] = CourseUtils.makeCourseTogether(day6);
        courseData[6] = CourseUtils.makeCourseTogether(day7);

        title = (TextView) findViewById(R.id.title);
        title.setText("第" + whichWeek + "周");

        for (int i = 0; i < weekPanels.length; i++) {
            weekPanels[i] = (LinearLayout) findViewById(R.id.weekPanel_1 + i);
            weekPanels[i].removeAllViews();
            initWeekPanel(weekPanels[i], courseData[i]);
        }
        TextView title6 = (TextView) findViewById(R.id.title6);
        TextView title7 = (TextView) findViewById(R.id.title7);
        LinearLayout L6 = (LinearLayout) findViewById(R.id.weekPanel_6);
        LinearLayout L7 = (LinearLayout) findViewById(R.id.weekPanel_7);
//        if (courseData[5].size() == 0) {
//            title6.setVisibility(View.GONE);
//            L6.setVisibility(View.GONE);
//        }
        if (courseData[6].size() == 0) {
            title7.setVisibility(View.GONE);
            L7.setVisibility(View.GONE);
        }
    }

    public void showImage(Bitmap bitmap) {
        iv_background = (ImageView) findViewById(R.id.iv_background);
        iv_background.setImageBitmap(bitmap);
    }

    public void initWeekPanel(LinearLayout ll, List<Course> data) {
        if (ll == null || data == null || data.size() < 1) return;
        Log.i("Msg", "初始化面板");
        Course pre = data.get(0);
        for (int i = 0; i < data.size(); i++) {
            final Course c = data.get(i);
            TextView tv = new TextView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    itemHeight * c.getStep() + marTop * (c.getStep() - 1));
            if (i > 0) {
                lp.setMargins(marLeft, (c.getStart() - (pre.getStart() + pre.getStep())) * (itemHeight + marTop) + marTop, 0, 0);
            } else {
                lp.setMargins(marLeft, (c.getStart() - 1) * (itemHeight + marTop) + marTop, 0, 0);
            }
            tv.setLayoutParams(lp);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setTextSize(14);
            tv.setPadding(2, 2, 2, 2);
            tv.setTextColor(getResources().getColor(R.color.white));
            switch (c.getIsOdd()) {
                case 0:
                    tv.setText(c.getName() + "@" + c.getRoom());
                    break;
                case 1:
                    tv.setText(c.getName() + "@" + c.getRoom() + "\n单周");
                    if (whichWeek % 2 == 0) {
                        tv.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 2:
                    tv.setText(c.getName() + "@" + c.getRoom() + "\n双周");
                    if (whichWeek % 2 != 0) {
                        tv.setVisibility(View.INVISIBLE);
                    }
                    break;
            }

            //tv.setBackgroundColor(getResources().getColor(R.color.classIndex));
            int colorNum = c.getName().charAt(0) % 10;
            Log.d("数字", "" + colorNum);
            tv.setBackground(getResources().getDrawable(R.drawable.lessonbackground));
            GradientDrawable myGrad = (GradientDrawable) tv.getBackground();
            if (c.getId().equals("")) {
                switch (colorNum) {
                    case 0:
                        myGrad.setColor(getResources().getColor(R.color.one));
                        break;
                    case 1:
                        myGrad.setColor(getResources().getColor(R.color.two));
                        break;
                    case 2:
                        myGrad.setColor(getResources().getColor(R.color.three));
                        break;
                    case 3:
                        myGrad.setColor(getResources().getColor(R.color.four));
                        break;
                    case 4:
                        myGrad.setColor(getResources().getColor(R.color.five));
                        break;
                    case 5:
                        myGrad.setColor(getResources().getColor(R.color.six));
                        break;
                    case 6:
                        myGrad.setColor(getResources().getColor(R.color.seven));
                        break;
                    case 7:
                        myGrad.setColor(getResources().getColor(R.color.eight));
                        break;
                    case 8:
                        myGrad.setColor(getResources().getColor(R.color.nine));
                        break;
                    case 9:
                        myGrad.setColor(getResources().getColor(R.color.ten));
                        break;
                }
            } else {
                switch (c.getId()) {
                    case "0":
                        myGrad.setColor(getResources().getColor(R.color.one));
                        break;
                    case "1":
                        myGrad.setColor(getResources().getColor(R.color.two));
                        break;
                    case "2":
                        myGrad.setColor(getResources().getColor(R.color.three));
                        break;
                    case "3":
                        myGrad.setColor(getResources().getColor(R.color.four));
                        break;
                    case "4":
                        myGrad.setColor(getResources().getColor(R.color.five));
                        break;
                    case "5":
                        myGrad.setColor(getResources().getColor(R.color.six));
                        break;
                    case "6":
                        myGrad.setColor(getResources().getColor(R.color.seven));
                        break;
                    case "7":
                        myGrad.setColor(getResources().getColor(R.color.eight));
                        break;
                    case "8":
                        myGrad.setColor(getResources().getColor(R.color.nine));
                        break;
                    case "9":
                        myGrad.setColor(getResources().getColor(R.color.ten));
                        break;
                }
            }

            if (c.getStartWeek() > whichWeek || c.getEndWeek() < whichWeek) {
                //myGrad.setColor(getResources().getColor(R.color.none));
                tv.setVisibility(View.INVISIBLE);
            }
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(ScheduleActivity.this, "任课老师是" + c.getTeach(), Toast.LENGTH_SHORT).show();
                    AlertDialog dialog = new AlertDialog.Builder(ScheduleActivity.this)
                            .setView(R.layout.dialog_course)
                            .setCancelable(true)
                            .setPositiveButton("知道啦", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                    TextView courseName, teacherName, room, classTime, odd, classWeek, timeDetail;
                    courseName = (TextView) dialog.findViewById(R.id.cv_courseName);
                    teacherName = (TextView) dialog.findViewById(R.id.cv_teacher);
                    room = (TextView) dialog.findViewById(R.id.cv_room);
                    classTime = (TextView) dialog.findViewById(R.id.cv_time);
                    odd = (TextView) dialog.findViewById(R.id.cv_odd);
                    classWeek = (TextView) dialog.findViewById(R.id.cv_week);
                    timeDetail = (TextView) dialog.findViewById(R.id.cv_timeDetail);
                    courseName.setText(c.getName());
                    teacherName.setText("老师：" + c.getTeach());
                    room.setText("地点：" + c.getRoom());
                    classTime.setText("时间：第" + c.getStart() + " - " + (c.getStep() + c.getStart() - 1) + "节");
                    timeDetail.setText(startList[c.getStart() - 1] + " - " + endList[c.getStart() + c.getStep() - 2]);
                    classWeek.setText("第" + c.getStartWeek() + " - " + c.getEndWeek() + "周");
                    switch (c.getIsOdd()) {
                        case 0:
                            odd.setVisibility(View.GONE);
                            break;
                        case 1:
                            odd.setText("单周");
                            break;
                        case 2:
                            odd.setText("双周");
                            break;
                    }
                }
            });
            ll.addView(tv);
            pre = c;
        }
    }

    public static int daysBetween() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayTime = sdf.format(new Date());// 获取当前的日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(startDay));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(todayTime));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        //Log.d("日期", Integer.parseInt(String.valueOf(between_days)) + "");
        return Integer.parseInt(String.valueOf(between_days));
    }

    public static int countWeek() throws ParseException {
        //Log.d("日期", (daysBetween(smdate, bdate) / 7 + 1) + "");
        return (daysBetween() / 7) + 1;
    }

    public void showAlert() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("警告");
        builder.setMessage("确定要重新获取课表吗？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(ScheduleActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
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
    }
}
