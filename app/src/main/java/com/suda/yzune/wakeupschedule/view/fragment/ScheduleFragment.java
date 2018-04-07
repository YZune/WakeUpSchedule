package com.suda.yzune.wakeupschedule.view.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.model.bean.Course;
import com.suda.yzune.wakeupschedule.utils.CourseUtils;
import com.suda.yzune.wakeupschedule.utils.DisplayUtil;
import com.suda.yzune.wakeupschedule.utils.MyRadioGroup;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ScheduleFragment extends Fragment {

    View view;
    LinearLayout weekPanels[] = new LinearLayout[7];
    TextView none_tip;
    TextView tv_10, tv_11, tv_12, tv_13, tv_14, tv_15, tv_16;
    EditText courseName, teacherName, room, classTimeStart, classTimeEnd, WeekStart, WeekEnd, et_odd;
    TextView odd, timeDetail;
    MyRadioGroup myRadioGroup;
    RadioButton rb1, rb2, rb3, rb4, rb5, rb6, rb7, rb8, rb9, rb10;
    LinearLayout ll_left, ll_week;
    static int chooseSchool = 0;
    int l = 0;
    int classNum;

    static String startDay;
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
    private String[] startList = {"8:00", "9:00", "10:10", "11:10", "13:30", "14:30", "15:40", "16:40", "18:30", "19:30", "20:30", "00:00", "00:00", "00:00", "00:00"};
    private String[] endList = {"8:50", "9:50", "11:00", "12:00", "14:20", "15:20", "16:30", "17:30", "19:20", "20:20", "21:20", "00:00", "00:00", "00:00", "00:00"};

    public static ScheduleFragment newInstance(int week) {
        ScheduleFragment newFragment = new ScheduleFragment();
        newFragment.whichWeek = week;
        return newFragment;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        whichWeek = args.getInt("week");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedule, container, false);

        initView(view);
        initData();

        return view;
    }

    public void initView(View view) {
        ll_week = (LinearLayout) view.findViewById(R.id.weekName);
        ll_left = (LinearLayout) view.findViewById(R.id.weekPanel_0);
        tv_10 = (TextView) view.findViewById(R.id.tv_10);
        tv_11 = (TextView) view.findViewById(R.id.tv_11);
        tv_12 = (TextView) view.findViewById(R.id.tv_12);
        tv_13 = (TextView) view.findViewById(R.id.tv_13);
        tv_14 = (TextView) view.findViewById(R.id.tv_14);
        tv_15 = (TextView) view.findViewById(R.id.tv_15);
        tv_16 = (TextView) view.findViewById(R.id.tv_16);
        none_tip = (TextView) view.findViewById(R.id.none_tip);
        if (SharedPreferencesUtils.getStringFromSP(getContext(), "course", "").equals("")) {
            none_tip.setVisibility(View.VISIBLE);
        }
    }

    public void initData() {
        chooseSchool = SharedPreferencesUtils.getIntFromSP(getContext(), "chooseSchool");
        startDay = SharedPreferencesUtils.getStringFromSP(getContext(), "termStart", "2018-03-05");
        classNum = SharedPreferencesUtils.getIntFromSP(getContext(), "classNum", 11);
        refresh();
        //Toasty.success(getContext(), whichWeek+"周").show();
    }

    @Override
    public void onStart() {
        super.onStart();
        tv_10.setVisibility(View.VISIBLE);
        tv_11.setVisibility(View.VISIBLE);
        tv_12.setVisibility(View.VISIBLE);
        tv_13.setVisibility(View.VISIBLE);
        tv_14.setVisibility(View.VISIBLE);
        tv_15.setVisibility(View.VISIBLE);
        tv_16.setVisibility(View.VISIBLE);
        startDay = SharedPreferencesUtils.getStringFromSP(getContext(), "termStart", "2018-03-05");
        classNum = SharedPreferencesUtils.getIntFromSP(getContext(), "classNum", 11);
        refresh();
    }

    public void refresh() {
        if (SharedPreferencesUtils.getBooleanFromSP(getContext(), "s_color", false)){
            for (int i = 0; i < ll_left.getChildCount(); i++) {
                TextView tv = (TextView) ll_left.getChildAt(i);
                tv.setTextColor(getResources().getColor(R.color.white));
            }
            for (int i = 0; i < ll_week.getChildCount(); i++) {
                TextView tv = (TextView) ll_week.getChildAt(i);
                tv.setTextColor(getResources().getColor(R.color.white));
            }
        }
        else {
            for (int i = 0; i < ll_left.getChildCount(); i++) {
                TextView tv = (TextView) ll_left.getChildAt(i);
                tv.setTextColor(getResources().getColor(R.color.black));
            }
            for (int i = 0; i < ll_week.getChildCount(); i++) {
                TextView tv = (TextView) ll_week.getChildAt(i);
                tv.setTextColor(getResources().getColor(R.color.black));
            }
        }
        itemHeight = DisplayUtil.dip2px(getContext(), SharedPreferencesUtils.getIntFromSP(getContext(), "item_height", 56));
        for (int i = 0; i < ll_left.getChildCount(); i++) {
            ViewGroup.LayoutParams lp = ll_left.getChildAt(i).getLayoutParams();
            lp.height = itemHeight;
            ll_left.getChildAt(i).setLayoutParams(lp);
        }
        switch (classNum) {
            case 8:
                tv_10.setVisibility(View.GONE);
            case 9:
                tv_11.setVisibility(View.GONE);
            case 10:
                tv_12.setVisibility(View.GONE);
            case 11:
                tv_13.setVisibility(View.GONE);
            case 12:
                tv_14.setVisibility(View.GONE);
            case 13:
                tv_15.setVisibility(View.GONE);
            case 14:
                tv_16.setVisibility(View.GONE);
                break;
        }
        //Log.d("高度", DisplayUtil.dip2px(getContext(), 56)+"");
        marTop = getResources().getDimensionPixelSize(R.dimen.weekItemMarTop);
        marLeft = getResources().getDimensionPixelSize(R.dimen.weekItemMarLeft);
        Gson gson = new Gson();
        List<Course> allCourse = gson.fromJson(SharedPreferencesUtils.getStringFromSP(getContext(), "course", ""), new TypeToken<List<Course>>() {
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
        //Log.d("处理", "旧"+gson.toJson(day1));
        courseData[0] = CourseUtils.makeCourseTogether(day1);
        courseData[1] = CourseUtils.makeCourseTogether(day2);
        courseData[2] = CourseUtils.makeCourseTogether(day3);
        courseData[3] = CourseUtils.makeCourseTogether(day4);
        courseData[4] = CourseUtils.makeCourseTogether(day5);
        courseData[5] = CourseUtils.makeCourseTogether(day6);
        courseData[6] = CourseUtils.makeCourseTogether(day7);

        for (int i = 0; i < weekPanels.length; i++) {
            weekPanels[i] = (LinearLayout) view.findViewById(R.id.weekPanel_1 + i);
            weekPanels[i].removeAllViews();
            initWeekPanel(weekPanels[i], courseData[i]);
        }
        TextView title7 = (TextView) view.findViewById(R.id.title7);
        LinearLayout L7 = (LinearLayout) view.findViewById(R.id.weekPanel_7);
        if (courseData[6].size() == 0) {
            title7.setVisibility(View.GONE);
            L7.setVisibility(View.GONE);
        }
    }

    public void initWeekPanel(LinearLayout ll, List<Course> data) {
        if (ll == null || data == null || data.size() < 1) return;
        Log.i("Msg", "初始化面板");
        Course pre = data.get(0);
        for (int i = 0; i < data.size(); i++) {
            final Course c = data.get(i);
            TextView tv = new TextView(getContext());
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

            //tv.setBackgroundColor(getResources().getColor(R.color.classIndex));
            int colorNum = c.getName().charAt(0) % 10;
            Log.d("数字", "" + colorNum);
            tv.setBackground(getResources().getDrawable(R.drawable.lessonbackground));
            GradientDrawable myGrad = (GradientDrawable) tv.getBackground();
            float a = SharedPreferencesUtils.getIntFromSP(getContext(), "sb_alpha", 64);
            if (c.getId().equals("")) {
                switch (colorNum) {
                    case 0:
                        Log.d("颜色",a/100.0+"");
                        myGrad.setColor(getResources().getColor(R.color.one));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                    case 1:
                        myGrad.setColor(getResources().getColor(R.color.two));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                    case 2:
                        myGrad.setColor(getResources().getColor(R.color.three));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                    case 3:
                        myGrad.setColor(getResources().getColor(R.color.four));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                    case 4:
                        myGrad.setColor(getResources().getColor(R.color.five));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                    case 5:
                        myGrad.setColor(getResources().getColor(R.color.six));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                    case 6:
                        myGrad.setColor(getResources().getColor(R.color.seven));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                    case 7:
                        myGrad.setColor(getResources().getColor(R.color.eight));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                    case 8:
                        myGrad.setColor(getResources().getColor(R.color.nine));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                    case 9:
                        myGrad.setColor(getResources().getColor(R.color.ten));                        myGrad.setAlpha(Math.round(255*(a/100)));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                }
            } else {
                switch (c.getId()) {
                    case "0":
                        myGrad.setColor(getResources().getColor(R.color.one));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                    case "1":
                        myGrad.setColor(getResources().getColor(R.color.two));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                    case "2":
                        myGrad.setColor(getResources().getColor(R.color.three));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                    case "3":
                        myGrad.setColor(getResources().getColor(R.color.four));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                    case "4":
                        myGrad.setColor(getResources().getColor(R.color.five));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                    case "5":
                        myGrad.setColor(getResources().getColor(R.color.six));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                    case "6":
                        myGrad.setColor(getResources().getColor(R.color.seven));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                    case "7":
                        myGrad.setColor(getResources().getColor(R.color.eight));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                    case "8":
                        myGrad.setColor(getResources().getColor(R.color.nine));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                    case "9":
                        myGrad.setColor(getResources().getColor(R.color.ten));
                        myGrad.setAlpha(Math.round(255*(a/100)));
                        break;
                }
            }

            switch (c.getIsOdd()) {
                case 0:
                    tv.setText(c.getName() + "@" + c.getRoom());
                    break;
                case 1:
                    tv.setText(c.getName() + "@" + c.getRoom() + "\n单周");
                    if (whichWeek % 2 == 0) {
                        if (SharedPreferencesUtils.getBooleanFromSP(getContext(), "s_show", false)) {
                            myGrad.setColor(getResources().getColor(R.color.none));
                            tv.setText("[非本周]" + tv.getText());
                        } else {
                            tv.setVisibility(View.INVISIBLE);
                        }
                    }
                    break;
                case 2:
                    tv.setText(c.getName() + "@" + c.getRoom() + "\n双周");
                    if (whichWeek % 2 != 0) {
                        if (SharedPreferencesUtils.getBooleanFromSP(getContext(), "s_show", false)) {
                            myGrad.setColor(getResources().getColor(R.color.none));
                            tv.setText("[非本周]" + tv.getText());
                        } else {
                            tv.setVisibility(View.INVISIBLE);
                        }
                    }
                    break;
            }

            if ((c.getStartWeek() > whichWeek || c.getEndWeek() < whichWeek)) {
                if (SharedPreferencesUtils.getBooleanFromSP(getContext(), "s_show", false)) {
                    myGrad.setColor(getResources().getColor(R.color.none));
                    tv.setText("[非本周]" + tv.getText());
                } else {
                    tv.setVisibility(View.INVISIBLE);
                }
            }

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(getContext(), "任课老师是" + c.getTeach(), Toast.LENGTH_SHORT).show();
                    final List<Course> allCourse;
                    final Gson gson = new Gson();
                    List<Course> temp = new ArrayList<Course>();
                    temp.addAll(day1);
                    temp.addAll(day2);
                    temp.addAll(day3);
                    temp.addAll(day4);
                    temp.addAll(day5);
                    temp.addAll(day6);
                    temp.addAll(day7);
                    allCourse = CourseUtils.makeCourseTogether(temp);

                    for (int i = 0; i < allCourse.size(); i++) {
                        if (allCourse.get(i).getName().equals(c.getName()) && allCourse.get(i).getStart() == c.getStart() && allCourse.get(i).getDay() == c.getDay()) {
                            l = i;
                        }
                    }

                    final AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setView(R.layout.dialog_course)
                            .setCancelable(true)
                            .setNegativeButton("我知道啦", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setNeutralButton("删除", null)
                            .setPositiveButton("修改", null).create();
                    dialog.show();

                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                        int makeSure = 0;

                        @Override
                        public void onClick(View view) {
                            if (makeSure == 0) {
                                Toasty.warning(getContext(), "再点一次确认删除").show();
                                makeSure++;
                            } else {
                                allCourse.remove(l);
                                SharedPreferencesUtils.saveStringToSP(getContext(), "course", gson.toJson(allCourse));
                                //Log.d("保存", SharedPreferencesUtils.getStringFromSP(getContext(), "course", ""));
                                refresh();
                                Toasty.success(getContext(), "删除成功").show();
                                dialog.dismiss();
                            }
                        }
                    });

                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (dialog.getButton(AlertDialog.BUTTON_POSITIVE).getText().toString().equals("修改")) {
                                makeEditTextEnable(courseName);
                                makeEditTextEnable(teacherName);
                                makeEditTextEnable(room);
                                makeEditTextEnable(classTimeStart);
                                makeEditTextEnable(classTimeEnd);
                                makeEditTextEnable(WeekStart);
                                makeEditTextEnable(WeekEnd);
                                makeEditTextEnable(et_odd);
                                myRadioGroup.setVisibility(View.VISIBLE);

                                myRadioGroup.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(MyRadioGroup radioGroup, int i) {
                                        //Toast.makeText(getContext(), i+"", Toast.LENGTH_SHORT).show();
                                        switch (i) {
                                            case R.id.rb1:
                                                c.setId("0");
                                                break;
                                            case R.id.rb2:
                                                c.setId("1");
                                                break;
                                            case R.id.rb3:
                                                c.setId("2");
                                                break;
                                            case R.id.rb4:
                                                c.setId("3");
                                                break;
                                            case R.id.rb5:
                                                c.setId("4");
                                                break;
                                            case R.id.rb6:
                                                c.setId("5");
                                                break;
                                            case R.id.rb7:
                                                c.setId("6");
                                                break;
                                            case R.id.rb8:
                                                c.setId("7");
                                                break;
                                            case R.id.rb9:
                                                c.setId("8");
                                                break;
                                            case R.id.rb10:
                                                c.setId("9");
                                                break;
                                        }
                                    }
                                });

                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setText("保存修改");
                            } else if (dialog.getButton(AlertDialog.BUTTON_POSITIVE).getText().toString().equals("保存修改")) {
                                //dialog.getButton(AlertDialog.BUTTON_POSITIVE).setText("修改");
                                myRadioGroup.setVisibility(View.GONE);
                                String _courseName = courseName.getText().toString();
                                String _teacherName = teacherName.getText().toString();
                                String _room = room.getText().toString();
                                String _classStart = classTimeStart.getText().toString();
                                String _classEnd = classTimeEnd.getText().toString();
                                String _WeekStart = WeekStart.getText().toString();
                                String _WeekEnd = WeekEnd.getText().toString();
                                String _Odd = et_odd.getText().toString();

                                //spinner_odd.setSelection(c.getIsOdd());

                                if (!(_courseName.equals("") || _classStart.equals("") || _classEnd.equals("") || _WeekStart.equals("") || _WeekEnd.equals("")) && (_Odd.equals("单") || _Odd.equals("双") || _Odd.equals("全"))) {
                                    int int_classStart = Integer.valueOf(_classStart);
                                    int int_classEnd = Integer.valueOf(_classEnd);
                                    int int_WeekStart = Integer.valueOf(_WeekStart);
                                    int int_WeekEnd = Integer.valueOf(_WeekEnd);
                                    if (int_classEnd >= int_classStart && int_WeekEnd >= int_WeekStart && int_classStart > 0 && int_WeekStart > 0) {
                                        courseName.setEnabled(false);
                                        teacherName.setEnabled(false);
                                        room.setEnabled(false);
                                        classTimeStart.setEnabled(false);
                                        classTimeEnd.setEnabled(false);
                                        WeekStart.setEnabled(false);
                                        WeekEnd.setEnabled(false);
                                        et_odd.setEnabled(false);
                                        allCourse.get(l).setName(_courseName);
                                        allCourse.get(l).setTeach(_teacherName);
                                        allCourse.get(l).setRoom(_room);
                                        allCourse.get(l).setStart(int_classStart);
                                        allCourse.get(l).setStep(int_classEnd - int_classStart + 1);
                                        allCourse.get(l).setStartWeek(int_WeekStart);
                                        allCourse.get(l).setEndWeek(int_WeekEnd);
                                        switch (_Odd) {
                                            case "全":
                                                allCourse.get(l).setIsOdd(0);
                                                break;
                                            case "单":
                                                allCourse.get(l).setIsOdd(1);
                                                break;
                                            case "双":
                                                allCourse.get(l).setIsOdd(2);
                                                break;
                                        }
                                        SharedPreferencesUtils.saveStringToSP(getContext(), "course", gson.toJson(allCourse));
                                        //Log.d("保存", SharedPreferencesUtils.getStringFromSP(getContext(), "course", ""));
                                        refresh();
                                        Toasty.success(getContext(), "修改成功").show();
                                        dialog.dismiss();
                                    } else {
                                        Toasty.error(getContext(), "请认真填写::>_<::").show();
                                    }
                                } else {
                                    Toasty.error(getContext(), "请认真填写::>_<::").show();
                                }
                            }
                            return;
                        }
                    });

                    courseName = (EditText) dialog.findViewById(R.id.cv_courseName);
                    teacherName = (EditText) dialog.findViewById(R.id.cv_teacher);
                    room = (EditText) dialog.findViewById(R.id.cv_room);
                    classTimeStart = (EditText) dialog.findViewById(R.id.cv_time_start);
                    classTimeEnd = (EditText) dialog.findViewById(R.id.cv_time_end);
                    WeekStart = (EditText) dialog.findViewById(R.id.cv_week_start);
                    WeekEnd = (EditText) dialog.findViewById(R.id.cv_week_end);
                    odd = (TextView) dialog.findViewById(R.id.cv_odd);
                    et_odd = (EditText) dialog.findViewById(R.id.et_odd);
                    timeDetail = (TextView) dialog.findViewById(R.id.cv_timeDetail);
                    courseName.setText(c.getName());
                    teacherName.setText(c.getTeach());
                    room.setText(c.getRoom());
                    classTimeStart.setText(c.getStart() + "");
                    classTimeEnd.setText((c.getStep() + c.getStart() - 1) + "");
                    if (chooseSchool == 1) {
                        timeDetail.setText(startList[c.getStart() - 1] + " - " + endList[c.getStart() + c.getStep() - 2]);
                    } else {
                        String str_time = SharedPreferencesUtils.getStringFromSP(getContext(), "timeList", "");
                        if (str_time.equals("")) {
                            timeDetail.setText("还未设置课程时间");
                        } else {
                            List<String> timeList = gson.fromJson(str_time, new TypeToken<List<String>>() {
                            }.getType());
                            List<String> end_timeList = gson.fromJson(SharedPreferencesUtils.getStringFromSP(getContext(), "endTimeList", ""), new TypeToken<List<String>>() {
                            }.getType());
                            timeDetail.setText(timeList.get(c.getStart() - 1) + " - " + end_timeList.get(c.getStart() + c.getStep() - 2));
                        }
                    }
                    WeekStart.setText(c.getStartWeek() + "");
                    WeekEnd.setText(c.getEndWeek() + "");

                    myRadioGroup = (MyRadioGroup) dialog.findViewById(R.id.my_rg);
                    rb1 = (RadioButton) dialog.findViewById(R.id.rb1);
                    rb2 = (RadioButton) dialog.findViewById(R.id.rb2);
                    rb3 = (RadioButton) dialog.findViewById(R.id.rb3);
                    rb4 = (RadioButton) dialog.findViewById(R.id.rb4);
                    rb5 = (RadioButton) dialog.findViewById(R.id.rb5);
                    rb6 = (RadioButton) dialog.findViewById(R.id.rb6);
                    rb7 = (RadioButton) dialog.findViewById(R.id.rb7);
                    rb8 = (RadioButton) dialog.findViewById(R.id.rb8);
                    rb9 = (RadioButton) dialog.findViewById(R.id.rb9);
                    rb10 = (RadioButton) dialog.findViewById(R.id.rb10);

                    switch (c.getIsOdd()) {
                        case 0:
                            et_odd.setText("全");
                            break;
                        case 1:
                            et_odd.setText("单");
                            break;
                        case 2:
                            et_odd.setText("双");
                            break;
                    }

                }
            });
            ll.addView(tv);
            pre = c;
        }
    }

    public void makeEditTextEnable(EditText et) {
        et.setEnabled(true);
        et.setSelection(et.getText().length());
    }

    public static int countWeek(Context context) throws ParseException {
        //Log.d("日期", (daysBetween(smdate, bdate) / 7 + 1) + "");
        return (daysBetween(context) / 7) + 1;
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
}