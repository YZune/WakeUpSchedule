package com.suda.yzune.wakeupschedule.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.net.URLEncoder;
import java.util.List;

/**
 * Created by yzune on 2018/3/28.
 */

public class DonateUtils {
    public static boolean openAlipayPayPage(Context context) {
        return openAlipayPayPage(context, "https://qr.alipay.com/ap9meauipfitn4t148");
    }

    public static boolean openAlipayPayPage(Context context, String qrcode) {
        try {
            //https%3A%2F%2Fqr.alipay.com%2Fap9meauipfitn4t148
            qrcode = URLEncoder.encode(qrcode, "utf-8");
        } catch (Exception e) {
        }
        try {
            final String alipayqr = "alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + qrcode;
            Uri uri = Uri.parse(alipayqr + "%3F_s%3Dweb-other&_t=" + System.currentTimeMillis());
            Intent intent = new Intent(Intent.ACTION_ALL_APPS, uri);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isAppInstalled(Context context, String pkgName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(pkgName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
