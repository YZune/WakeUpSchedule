package com.suda.yzune.wakeupschedule.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.model.bean.Course;
import com.suda.yzune.wakeupschedule.utils.Constants;
import com.suda.yzune.wakeupschedule.utils.CourseUtils;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecodeActivity extends AppCompatActivity {
    EditText editText;
    FloatingActionButton floatingActionButton;
    private List<Course> allCourse = new ArrayList<Course>();
    String studentName, student_num, jsonCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);
        editText = (EditText) findViewById(R.id.et_code);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_done);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSource(editText.getText().toString());
            }
        });
    }

    public void showSource(String html) {
        int tableStart = html.indexOf("Table1");
        String Table = html.substring(html.indexOf(">", tableStart), html.indexOf("</table>", tableStart));
        Log.d("下学期", Table);
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
                        if (singleSplit[2].contains("/") && singleSplit[2].contains("(")) {
                            String[] mix = singleSplit[2].split("/");
                            mix[1].indexOf("(");
                            allCourse.add(new Course(singleSplit[0],
                                    mix[1].substring(mix[1].indexOf("(") + 1, mix[1].length()), startIndex, step,
                                    mix[1].substring(0, mix[1].indexOf("(")), "", day, startWeek, endWeek, isOdd));
                        } else {
                            allCourse.add(new Course(singleSplit[0], "", startIndex, step, singleSplit[2], "",
                                    day, startWeek, endWeek, isOdd));
                        }
                    }
                    if (againFlag) {
                        getfromLater(rowLater, startIndex, day, allCourse);
                    }

                }
            }
            start = end + 1;
        }
        for (Course c : allCourse) {
            c.setCampus(SharedPreferencesUtils.getStringFromSP(DecodeActivity.this, "campus", ""));
        }
        //Log.d("campus", "生成时"+SharedPreferencesUtils.getStringFromSP(LoginXKActivity.this, "campus"));
        Gson gson = new Gson();
        jsonCourse = gson.toJson(allCourse);
        Log.d("Gson", jsonCourse);
        SharedPreferencesUtils.saveStringToSP(getApplicationContext(), "course", jsonCourse);
        Intent i = new Intent(DecodeActivity.this, ScheduleActivity.class);
        i.putExtra("who", 1);
        startActivity(i);
        new Thread() {
            @Override
            public void run() {
                try {
                    String callStr = CourseUtils.post(Constants.COURSE_API + "school/all_courses/sendcourse", jsonCourse);
                    Log.d("上传", callStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
        finish();
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
                if (mix.length > 1) {
                    if (mix[1].contains("(")) {
                        allCourse.add(new Course(singleSplit[0], mix[1].substring(mix[1].indexOf("(") + 1, mix[1].length()),
                                startIndex, step, mix[1].substring(0, mix[1].indexOf("(")), "", day, startWeek, endWeek,
                                isOdd));

                    } else {
                        allCourse.add(new Course(singleSplit[0], "",
                                startIndex, step, mix[1], "", day, startWeek, endWeek,
                                isOdd));
                    }
                } else {
                    allCourse.add(new Course(singleSplit[0], "",
                            startIndex, step, mix[0], "", day, startWeek, endWeek,
                            isOdd));
                }
            }
        }
    }

}
