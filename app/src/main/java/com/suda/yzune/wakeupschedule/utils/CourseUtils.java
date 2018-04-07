package com.suda.yzune.wakeupschedule.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suda.yzune.wakeupschedule.model.bean.Course;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by YZune on 2017/9/9.
 */

public class CourseUtils {

    private static String campusResult = "";

    public static List<Course> makeCourseTogether(List<Course> courseList) {
        int i = 0;
        List<Course> list = new ArrayList<Course>();
        Gson gson = new Gson();
        while (i < courseList.size()) {
            if (!(i == courseList.size() - 1) && (courseList.get(i).getName().equals(courseList.get(i + 1).getName())) && (courseList.get(i).getRoom().equals(courseList.get(i + 1).getRoom())) && (courseList.get(i).getStartWeek() == courseList.get(i + 1).getStartWeek())) {
                //courseList.get(i).setStep(courseList.get(i).getStep() + courseList.get(i + 1).getStep());
                String s = gson.toJson(courseList.get(i));
                Course temp = gson.fromJson(s, new TypeToken<Course>() {
                }.getType());
                temp.setStep(courseList.get(i).getStep() + courseList.get(i + 1).getStep());
                list.add(temp);
                i += 2;
            } else {
                list.add(courseList.get(i));
                i += 1;
            }
        }
        return list;
    }

    public final static int CONNECT_TIMEOUT = 60;
    public final static int READ_TIMEOUT = 100;
    public final static int WRITE_TIMEOUT = 60;
    public static final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
            .build();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public static void showCampusChoose(final Context context, final TextView textView) {
        final String[] n = {"天赐庄校区", "北校区", "独墅湖校区", "阳澄湖校区"};
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setItems(n, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        textView.setText(n[i]);
                        SharedPreferencesUtils.saveStringToSP(context, "campus", n[i]);
                        Log.d("campus", "注入时" + n[i]);
                    }
                })
                .create();
        dialog.show();
    }

    public static void showCampusChoose(final Context context) {
        final String[] n = {"天赐庄校区", "北校区", "独墅湖校区", "阳澄湖校区"};
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("请选择校区")
                .setCancelable(false)
                .setItems(n, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, final int i) {
                        campusResult = n[i];
                        Gson gson = new Gson();
                        List<Course> allCourse = gson.fromJson(SharedPreferencesUtils.getStringFromSP(context, "course", ""), new TypeToken<List<Course>>() {
                        }.getType());
                        for (int j = 0; j < allCourse.size(); j++) {
                            String oldRoom = allCourse.get(j).getRoom();
                            if (oldRoom.contains("</a>")) {
                                allCourse.get(j).setRoom(oldRoom.substring(0, oldRoom.indexOf("</a>")));
                            }
                            allCourse.get(j).setCampus(campusResult);
                        }
                        String jsonCourse = gson.toJson(allCourse);
                        Log.d("Gson", jsonCourse);
                        SharedPreferencesUtils.saveStringToSP(context, "course", jsonCourse);
                        SharedPreferencesUtils.saveStringToSP(context, "campus", n[i]);
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    String callStr = CourseUtils.post(Constants.COURSE_API + "school/all_courses/sendcourse", SharedPreferencesUtils.getStringFromSP(context, "course", ""));
                                    //Log.d("更新后", SharedPreferencesUtils.getStringFromSP(context, "course"));
                                    Log.d("上传", callStr);
                                    //if (callStr.contains("成功")) {
                                    //}
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                })
                .create();
        dialog.show();
    }

    public static String calAfterTime(String time, int min) {
        int time_hour = Integer.valueOf(time.substring(0, 2));
        int time_min = Integer.valueOf(time.substring(3, 5));
        int add = time_min + min;
        int new_hour = time_hour + add / 60;
        int new_min = add % 60;
        String str_time = "";

        if (new_hour < 10 && new_min >= 10) {
            str_time = "0" + new_hour + ":" + new_min;
        } else if (new_hour < 10 && new_min < 10) {
            str_time = "0" + new_hour + ":0" + new_min;
        } else if (new_hour >= 10 && new_min >= 10) {
            str_time = new_hour + ":" + new_min;
        } else if (new_hour >= 10 && new_min < 10) {
            str_time = new_hour + ":0" + new_min;
        }
        return str_time;
    }
}
