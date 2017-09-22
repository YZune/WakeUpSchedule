package com.suda.yzune.wakeupschedule.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.model.bean.Course;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MWebViewClient";
    private static final String GET_FRAME_CONTENT_STR = "document.getElementById('iframeautoheight').contentWindow.document.body.innerHTML";
    private static boolean isFirst;

    private List<Course> allCourse = new ArrayList<Course>();

    WebView mWebView;
    FloatingActionButton btn_start;
    ImageButton ib_refresh;
    ImageButton ib_help;

    @Override
    @JavascriptInterface
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ib_help = (ImageButton) findViewById(R.id.ib_help);
        ib_refresh = (ImageButton) findViewById(R.id.ib_refresh);
        btn_start = (FloatingActionButton) findViewById(R.id.btn_start);
        mWebView = (WebView) findViewById(R.id.wv_login);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        mWebView.loadUrl("http://xk.suda.edu.cn");
        mWebView.setWebViewClient(new WebViewClient());
        isFirst = SharedPreferencesUtils.getBooleanFromSP(getApplicationContext(), "isFirst");
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            @JavascriptInterface
            public void onClick(View view) {
                mWebView.loadUrl("javascript:window.local_obj.showSource('<head>'+"
                        + GET_FRAME_CONTENT_STR
                        + "+'</head>');");
            }
        });

        ib_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.reload();
            }
        });

        ib_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        if (isFirst) {
            showDialog();
        }
    }

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            int tableStart = html.indexOf("Table1");
            String Table = html.substring(html.indexOf(">", tableStart), html.indexOf("</table>", tableStart));
            int start = 0;
            String single;
            String rowLater = "";
            String[] singleSplit;
            while (Table.indexOf("<tr>", start) != -1) {
                int end = Table.indexOf("</tr>", Table.indexOf("<tr>", start));
                String row = Table.substring(Table.indexOf("<tr>", start), end);
                if (row.contains("left=100,top=50')\">")) {
                    int startIndex = Integer.parseInt(row.substring(row.indexOf(">第") + 2, row.indexOf("节")));
                    int rowStart = 0;
                    while (row.indexOf("left=100,top=50')\">", rowStart) != -1) {
                        boolean againFlag = false;
                        int day = 0;
                        int rowEnd = row.indexOf("</td>", row.indexOf("left=100,top=50')\">", rowStart));
                        single = row.substring(row.indexOf("left=100,top=50')\">", rowStart) + 19, rowEnd);
                        singleSplit = single.split("<br>");
                        if (single.contains("left=100,top=50')\">")) {
                            rowLater = single;
                            againFlag = true;
                        }
                        int countStart = 0;
                        while (row.indexOf("align=\"Center\"", countStart) != -1 && countStart < rowEnd) {
                            if (row.indexOf("align=\"Center\"", countStart) < rowEnd) {
                                day += 1;
                                countStart = row.indexOf("align=\"Center\"", countStart) + 1;
                            } else {
                                break;
                            }
                        }

                        Pattern pattern = Pattern.compile("^(周)[一二三四五六日]");
                        Matcher matcher = pattern.matcher(singleSplit[1]);
                        if (matcher.find()) {
                            switch (matcher.group()) {
                                case "周一":
                                    day = 1;
                                    break;
                                case "周二":
                                    day = 2;
                                    break;
                                case "周三":
                                    day = 3;
                                    break;
                                case "周四":
                                    day = 4;
                                    break;
                                case "周五":
                                    day = 5;
                                    break;
                                case "周六":
                                    day = 6;
                                    break;
                                case "周日":
                                    day = 7;
                                    break;
                            }
                        }

                        int isOdd = 0;
                        if (singleSplit[1].contains("单周")) {
                            isOdd = 1;
                        } else if (singleSplit[1].contains("双周")) {
                            isOdd = 2;
                        }
                        int step = 1;
                        if (singleSplit[1].contains("节/周")) {
                            int numLocate = singleSplit[1].indexOf("节/周");
                            step = Integer.parseInt(singleSplit[1].substring(numLocate - 1, numLocate));
                        } else if (singleSplit[1].contains(",")) {
                            int numLocate = 0;
                            step = 1;
                            while (singleSplit[1].indexOf(",", numLocate) != -1 && numLocate < singleSplit[1].length()) {
                                step += 1;
                                numLocate = singleSplit[1].indexOf(",", numLocate) + 1;
                            }
                            Log.d("计数", step + "");
                        }
                        int startWeek = 1;
                        int endWeek = 20;
                        startWeek = Integer.parseInt(singleSplit[1].substring(singleSplit[1].indexOf("{第") + 2, singleSplit[1].indexOf("-")));
                        endWeek = Integer.parseInt(singleSplit[1].substring(singleSplit[1].indexOf("-") + 1, singleSplit[1].indexOf("周", singleSplit[1].indexOf("-"))));
                        rowStart = rowEnd + 1;
                        Log.d("名称t", singleSplit[0] + "星期" + day + "长度" + step + "开始" + startWeek + "结束" + endWeek);
                        //allCourse.add(new Course(singleSplit[0], singleSplit[3], startIndex, step, singleSplit[2], "", day, startWeek, endWeek, isOdd));
                        if (singleSplit.length >= 4) {
                            allCourse.add(new Course(singleSplit[0], singleSplit[3], startIndex, step, singleSplit[2], "", day, startWeek, endWeek, isOdd));
                        } else {
                            String[] mix = singleSplit[2].split("/");
                            mix[1].indexOf("(");
                            allCourse.add(new Course(singleSplit[0], mix[1].substring(mix[1].indexOf("(") + 1, mix[1].length() - 1), startIndex, step, mix[1].substring(0, mix[1].indexOf("(")), "", day, startWeek, endWeek, isOdd));
                        }
                        if (againFlag) {
                            getfromLater(rowLater, startIndex, day, allCourse);
                        }

                    }
                }
                start = end + 1;
            }
            Gson gson = new Gson();
            String jsonCourse = gson.toJson(allCourse);
            Log.d("Gson", jsonCourse);
            SharedPreferencesUtils.saveStringToSP(getApplicationContext(), "course", jsonCourse);
            Intent i = new Intent(MainActivity.this, ScheduleActivity.class);
            i.putExtra("who", 1);
            startActivity(i);
            finish();
        }
    }

    public static void getfromLater(String later, int startIndex, int day, List<Course> allCourse) {
        String[] singles = later.split("left=100,top=50");
        for (int i = 1; i < singles.length; i++) {
            if (singles[i].contains("<br><")) {
                singles[i] = singles[i].substring(singles[i].indexOf(">") + 1, singles[i].indexOf("<br><"));
            } else {
                singles[i] = singles[i].substring(singles[i].indexOf(">") + 1);
            }
            String[] singleSplit = singles[i].split("<br>");
            int isOdd = 0;
            if (singleSplit[1].contains("单周")) {
                isOdd = 1;
            } else if (singleSplit[1].contains("双周")) {
                isOdd = 2;
            }
            int step = 1;
            if (singleSplit[1].contains("节/周")) {
                int numLocate = singleSplit[1].indexOf("节/周");
                step = Integer.parseInt(singleSplit[1].substring(numLocate - 1, numLocate));
            } else if (singleSplit[1].contains(",")) {
                int numLocate = 0;
                step = 1;
                while (singleSplit[1].indexOf(",", numLocate) != -1 && numLocate < singleSplit[1].length()) {
                    step += 1;
                    numLocate = singleSplit[1].indexOf(",", numLocate) + 1;
                }
            }
            int startWeek = 1;
            int endWeek = 20;
            startWeek = Integer.parseInt(singleSplit[1].substring(singleSplit[1].indexOf("{第") + 2, singleSplit[1].indexOf("-")));
            endWeek = Integer.parseInt(singleSplit[1].substring(singleSplit[1].indexOf("-") + 1, singleSplit[1].indexOf("周", singleSplit[1].indexOf("-"))));
            if (singleSplit.length >= 4) {
                allCourse.add(new Course(singleSplit[0], singleSplit[3], startIndex, step, singleSplit[2], "", day, startWeek, endWeek, isOdd));
            } else {
                String[] mix = singleSplit[2].split("/");
                mix[1].indexOf("(");
                allCourse.add(new Course(singleSplit[0], mix[1].substring(mix[1].indexOf("(") + 1, mix[1].length() - 1), startIndex, step, mix[1].substring(0, mix[1].indexOf("(")), "", day, startWeek, endWeek, isOdd));
            }
        }
    }

    public void showDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("使用帮助");
        builder.setMessage("欢迎使用WakeUp课程表。\n\n首次使用步骤：\n\n1.打开WiFi，连接苏大WiFi，不用登录网关，暂时关闭流量\n2.点击右上角的刷新按钮，登录教务系统\n3.点击信息查询-个人课表，点击右下角的勾号完成导入\n4.导入成功后会自动跳转到课程表，以后打开会直接进入课程表\n\n限于水平，目前还有一些功能没有实现，也许也会有一些小问题，可通过app侧栏的反馈功能向我们提出宝贵的建议:-)\n\n愿你不辜负每一个清晨。\n\nP.S. 若多次登录页面都不能跳转，请检查密码是否正确。");
        builder.setPositiveButton("我知道啦", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferencesUtils.saveBooleanToSP(getApplicationContext(), "isFirst", false);
            }
        });
        builder.show();
    }
}
