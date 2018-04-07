package com.suda.yzune.wakeupschedule.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suda.yzune.wakeupschedule.model.bean.UpdateInfo;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;

/**
 * Created by YZune on 2017/10/3.
 */

public class UpdateUtil {
    private static UpdateInfo updateInfo;

    public static int getVersionCode(Context context) throws Exception {
        //获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        Log.e("TAG", "版本号" + packInfo.versionCode);
        Log.e("TAG", "版本名" + packInfo.versionName);
        return packInfo.versionCode;
    }

    public static String getVersionName(Context context) throws Exception {
        //获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        Log.e("TAG", "版本号" + packInfo.versionCode);
        Log.e("TAG", "版本名" + packInfo.versionName);
        return packInfo.versionName;
    }

    public static void checkUpdate(final Context context, final int signal) {
        OkHttpUtil.getAsync(Constants.COURSE_API + "school/getupdate", new OkHttpUtil.ResultCallback() {
            @Override
            public void onError(Call call, Exception e) {
                //Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(byte[] response) {
                String result = new String(response);
                Gson gson = new Gson();
                updateInfo = gson.fromJson(result, new TypeToken<UpdateInfo>() {
                }.getType());
                try {
                    if (updateInfo.getId() > getVersionCode(context)) {
                        showAlert(context, updateInfo.getVersionName(), updateInfo.getVersionInfo());
                    } else {
                        switch (signal) {
                            case 0:
                                break;
                            case 1:
                                Toasty.success(context, "目前已是最新版本~").show();
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static void showAlert(final Context context, String version, String info) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle("有可用更新");
        try {
            builder.setMessage("当前版本：" + getVersionName(context) + "\n更新版本：" + version + "\n\n更新内容：\n" + info);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("去看看", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("https://www.coolapk.com/apk/com.suda.yzune.wakeupschedule");
                intent.setData(content_url);
                context.startActivity(intent);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}
