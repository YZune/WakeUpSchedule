package com.suda.yzune.wakeupschedule.view.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.suda.yzune.wakeupschedule.R;
import com.suda.yzune.wakeupschedule.model.bean.Course;
import com.suda.yzune.wakeupschedule.utils.Constants;
import com.suda.yzune.wakeupschedule.utils.CourseUtils;
import com.suda.yzune.wakeupschedule.utils.DisplayUtil;
import com.suda.yzune.wakeupschedule.utils.OkHttpUtil;
import com.suda.yzune.wakeupschedule.utils.SharedPreferencesUtils;
import com.suda.yzune.wakeupschedule.utils.ViewUtil;
import com.tapadoo.alerter.Alerter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;

public class LoginXKActivity extends AppCompatActivity {

    private byte[] verificationCode;
    OkHttpUtil.RequestData[] requestDatas = new OkHttpUtil.RequestData[5];
    private List<Course> allCourse = new ArrayList<Course>();
    OkHttpUtil.RequestData student_id, password, sCode, button1, viewState;
    OkHttpUtil.RequestData header_Host, header_agent, header_Referer;
    OkHttpUtil.RequestData __EVENTTARGET, __EVENTARGUMENT, xnd, xqd;
    String studentName, student_num, jsonCourse;
    AppCompatButton btn_login;
    EditText et_username, et_password, et_code;
    CheckBox cbDisplayP;
    ImageView iv_code;
    TextView choose_campus, tv_for_help;
    RelativeLayout rl_Login;
    ImageButton ib_recatch;
    CardView cv_login;
    LinearLayout ll_login;
    boolean need_HTML = false;
    int code_error_time = 0;
    private static boolean isFirst;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtil.fullScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_xk);
        refreshCodeImage();
        initViews();
        initAnim();
        initEvents();
        isFirst = SharedPreferencesUtils.getBooleanFromSP(getApplicationContext(), "isFirst");
        if (isFirst) {
            //showDialog();
        }
    }

    private void initViews() {
        rl_Login = (RelativeLayout) findViewById(R.id.rl_Login);
        btn_login = (AppCompatButton) findViewById(R.id.btn_login);
        et_username = (EditText) findViewById(R.id.input_Login);
        et_password = (EditText) findViewById(R.id.input_password);
        et_code = (EditText) findViewById(R.id.input_code);
        iv_code = (ImageView) findViewById(R.id.iv_code);
        cbDisplayP = (CheckBox) findViewById(R.id.cbDisplayPassword);
        choose_campus = (TextView) findViewById(R.id.choose_campus);
        ib_recatch = (ImageButton) findViewById(R.id.ib_recatch);
        tv_for_help = (TextView) findViewById(R.id.tv_for_help);
        cv_login = (CardView) findViewById(R.id.cv_login);
        ll_login = (LinearLayout) findViewById(R.id.ll_login);
        String num_SP = SharedPreferencesUtils.getStringFromSP(LoginXKActivity.this, "student_id", "");
        //String password_SP = SharedPreferencesUtils.getStringFromSP(LoginXKActivity.this, "password");
        if (!num_SP.equals("")) {
            et_username.setText(num_SP);
            //et_password.setText(password_SP);
        }
    }

    public void initAnim() {
        cv_login.setZ(0);
        cv_login.setScaleX(0);
        cv_login.setScaleY(0);
        ll_login.setAlpha(0);

        ObjectAnimator zAnim = ObjectAnimator.ofFloat(cv_login, "Z", 0, DisplayUtil.dip2px(LoginXKActivity.this, 120));
        ObjectAnimator xAnim = ObjectAnimator.ofFloat(cv_login, "ScaleX", 0, 1);
        ObjectAnimator yAnim = ObjectAnimator.ofFloat(cv_login, "ScaleY", 0, 1);
        AnimatorSet CardSet = new AnimatorSet();
        CardSet.playTogether(xAnim, yAnim, zAnim);
        CardSet.setDuration(1000);
        CardSet.setInterpolator(new AnticipateOvershootInterpolator());
        CardSet.start();

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(ll_login, "alpha", 0, 1);
        alphaAnim.setStartDelay(1000);
        alphaAnim.setDuration(1000);
        alphaAnim.start();
    }

    private void initEvents() {

        tv_for_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alerter.create(LoginXKActivity.this)
                        .setTitle("使用帮助")
                        .setText("极小概率会出现验证码一直错误的情况。当验证码错误多于3次时，下方登录按钮会提示使用网页版登录。")
                        .setBackgroundColorRes(R.color.login_button)
                        .setTextAppearance(R.style.alert_text)
                        .setIcon(R.drawable.help)
                        .enableSwipeToDismiss()
                        .setDuration(30000)
                        .show();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!need_HTML) {
                    if (et_username.getText().toString().equals("") || et_password.getText().toString().equals("") || et_code.getText().toString().equals("") || choose_campus.getText().toString().equals("")) {
                        //Toast.makeText(LoginXKActivity.this, "请认真填写o(*￣▽￣*)o", Toast.LENGTH_SHORT).show();
                        Toasty.warning(LoginXKActivity.this, "请认真填写o(*￣▽￣*)o", 5000).show();
                    } else {
                        student_num = et_username.getText().toString();
                        student_id = new OkHttpUtil.RequestData("TextBox1", student_num);
                        password = new OkHttpUtil.RequestData("TextBox2", et_password.getText().toString());
                        sCode = new OkHttpUtil.RequestData("TextBox3", et_code.getText().toString());
                        button1 = new OkHttpUtil.RequestData("Button1", "");
                        viewState = new OkHttpUtil.RequestData("__VIEWSTATE", "dDwtMTE5ODQzMDQ1NDt0PDtsPGk8MT47PjtsPHQ8O2w8aTw0PjtpPDc+O2k8OT47PjtsPHQ8cDw7cDxsPHZhbHVlOz47bDxcZTs+Pj47Oz47dDxwPDtwPGw8b25jbGljazs+O2w8d2luZG93LmNsb3NlKClcOzs+Pj47Oz47dDx0PDs7bDxpPDI+Oz4+Ozs+Oz4+Oz4+Oz5527rVtbyXbkyZdrm5O4U8rQ4EHA==");
                        requestDatas[0] = viewState;
                        requestDatas[1] = button1;
                        requestDatas[2] = student_id;
                        requestDatas[3] = password;
                        requestDatas[4] = sCode;

                        header_Host = new OkHttpUtil.RequestData("Host", "xk.suda.edu.cn");
                        header_agent = new OkHttpUtil.RequestData("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko");

                        OkHttpUtil.postAsync(Constants.EDUCATION_SYSTEM_LOGIN_URL, new OkHttpUtil.ResultCallback() {
                                    @Override
                                    public void onError(Call call, Exception e) {
                                        Toasty.error(LoginXKActivity.this, "网络错误(>﹏<)\n请确保连接了苏大WiFi", 5000).show();
                                    }

                                    @Override
                                    public void onResponse(byte[] response) {
                                        try {
                                            String s = new String(response, "gb2312");
                                            Log.d("状态", s);
                                            System.out.println(s);
                                            if (s.contains("验证码不正确")) {
                                                Toasty.error(LoginXKActivity.this, "验证码错误(>﹏<)", 5000).show();
                                                if (code_error_time > 1) {
                                                    btn_login.setText("试试网页登录？");
                                                    need_HTML = true;
                                                }
                                                code_error_time++;
                                                refreshCodeImage();
                                            } else if (s.contains("密码错误，请重新再输入")) {
                                                Toasty.error(LoginXKActivity.this, "密码错误(>﹏<)", 5000).show();
                                                refreshCodeImage();
                                            } else if (s.contains("用户名不存在")) {
                                                Toasty.error(LoginXKActivity.this, "看看学号是不是输错啦(>﹏<)", 5000).show();
                                                refreshCodeImage();
                                            } else if (s.contains("欢迎您：")) {
                                                studentName = s.substring(s.indexOf("id=\"xhxm\">") + 10, s.indexOf("同学</span>"));
                                                Log.d("姓名", studentName);
                                                Toasty.success(LoginXKActivity.this, "登录成功( •̀ ω •́ )y", 5000).show();
                                                SharedPreferencesUtils.saveStringToSP(LoginXKActivity.this, "student_id", student_num);
                                                //SharedPreferencesUtils.saveStringToSP(LoginXKActivity.this, "password", et_password.getText().toString());
                                                linktoSchedule();
                                            }
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                                , requestDatas, header_Host, header_agent);
                    }
                } else {
                    Intent i = new Intent(LoginXKActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        iv_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshCodeImage();
            }
        });

        ib_recatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshCodeImage();
            }
        });

        cbDisplayP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    et_password.setSelection(et_password.getText().length());
                } else {
                    et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    et_password.setSelection(et_password.getText().length());
                }
            }
        });

        choose_campus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CourseUtils.showCampusChoose(LoginXKActivity.this, choose_campus);
            }
        });
    }

    /**
     * 设置验证码图片
     */
    private void setVerificationCodeBg() {
        if (verificationCode != null && verificationCode.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(verificationCode, 0, verificationCode.length);
            Bitmap resizeBitmap = changeBitmapSize(bitmap, 140, 60);
            iv_code.setImageBitmap(resizeBitmap);
            iv_code.setBackground(new BitmapDrawable(getResources(), resizeBitmap));
            verificationCode = null;
            iv_code.setVisibility(View.VISIBLE);
            ib_recatch.setVisibility(View.GONE);
        } else {
            ib_recatch.setVisibility(View.VISIBLE);
            iv_code.setVisibility(View.GONE);
            Toasty.error(LoginXKActivity.this, "请检查是否已连接到苏大WiFi").show();
        }
    }

    /**
     * 用于改变验证码图片尺寸
     */
    private Bitmap changeBitmapSize(Bitmap bitmap, float width, float height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleX = (float) width / w;
        float scaleY = (float) height / h;
        matrix.postScale(scaleX, scaleY);
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return resizeBitmap;
    }

    public void linktoSchedule() {
        String url = "";
        __EVENTTARGET = new OkHttpUtil.RequestData("__EVENTTARGET", "xqd");
        __EVENTARGUMENT = new OkHttpUtil.RequestData("__EVENTARGUMENT", "");
        viewState = new OkHttpUtil.RequestData("__VIEWSTATE", "dDwzOTI4ODU2MjU7dDw7bDxpPDE+Oz47bDx0PDtsPGk8MT47aTwyPjtpPDQ+O2k8Nz47aTw5PjtpPDExPjtpPDEzPjtpPDE1PjtpPDI0PjtpPDI2PjtpPDI4PjtpPDMwPjtpPDMyPjtpPDM0Pjs+O2w8dDxwPHA8bDxUZXh0Oz47bDxcZTs+Pjs+Ozs+O3Q8dDxwPHA8bDxEYXRhVGV4dEZpZWxkO0RhdGFWYWx1ZUZpZWxkOz47bDx4bjt4bjs+Pjs+O3Q8aTwyPjtAPDIwMTctMjAxODsyMDE2LTIwMTc7PjtAPDIwMTctMjAxODsyMDE2LTIwMTc7Pj47bDxpPDA+Oz4+Ozs+O3Q8dDw7O2w8aTwwPjs+Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWtpuWPt++8mjE2Mjc0MDYwNjc7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWnk+WQje+8muadqOWinjs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDs+O2w85a2m6Zmi77ya6K6h566X5py656eR5a2m5LiO5oqA5pyv5a2m6ZmiOz4+Oz47Oz47dDxwPHA8bDxUZXh0Oz47bDzkuJPkuJrvvJrova/ku7blt6XnqIs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOihjOaUv+ePre+8muiuoTE26L2v5Lu2Oz4+Oz47Oz47dDw7bDxpPDE+Oz47bDx0PEAwPDs7Ozs7Ozs7Ozs+Ozs+Oz4+O3Q8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47bDxpPDE+Oz47bDx0PEAwPDs7Ozs7Ozs7Ozs+Ozs+Oz4+O3Q8QDA8cDxwPGw8UGFnZUNvdW50O18hSXRlbUNvdW50O18hRGF0YVNvdXJjZUl0ZW1Db3VudDtEYXRhS2V5czs+O2w8aTwxPjtpPDA+O2k8MD47bDw+Oz4+Oz47Ozs7Ozs7Ozs7Pjs7Pjt0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwwPjtpPDA+O2w8Pjs+Pjs+Ozs7Ozs7Ozs7Oz47Oz47dDxAMDxwPHA8bDxQYWdlQ291bnQ7XyFJdGVtQ291bnQ7XyFEYXRhU291cmNlSXRlbUNvdW50O0RhdGFLZXlzOz47bDxpPDE+O2k8MD47aTwwPjtsPD47Pj47Pjs7Ozs7Ozs7Ozs+Ozs+O3Q8QDA8cDxwPGw8UGFnZUNvdW50O18hSXRlbUNvdW50O18hRGF0YVNvdXJjZUl0ZW1Db3VudDtEYXRhS2V5czs+O2w8aTwxPjtpPDA+O2k8MD47bDw+Oz4+Oz47Ozs7Ozs7Ozs7Pjs7Pjs+Pjs+Pjs+0xDBPkH/h+fwXWUGY3+f0cAAb7A=");
        xnd = new OkHttpUtil.RequestData("xqn", "2017-2018");
        xqd = new OkHttpUtil.RequestData("xqd", "2");
        OkHttpUtil.RequestData[] requestDatas1 = {viewState, __EVENTTARGET, __EVENTARGUMENT, xnd, xqd};
        try {
            url = "http://xk.suda.edu.cn/xskbcx.aspx?xh=" + student_num + "&xm=" + URLEncoder.encode(studentName, "gb2312") + "&gnmkdm=N121603";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        header_Referer = new OkHttpUtil.RequestData("Referer", url);
        OkHttpUtil.postAsync(url, new OkHttpUtil.ResultCallback() {
            @Override
            public void onError(Call call, Exception e) {
                Log.d("状态", "错误");
            }

            @Override
            public void onResponse(byte[] response) {
                try {
                    String s = new String(response, "gb2312");
                    //Log.d("下学期", s);
                    showSource(s);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }, requestDatas1, header_Referer, header_agent, header_Host);

    }

    public void refreshCodeImage() {
        OkHttpUtil.getAsync(Constants.VERIFICATION_CODE_URL, new OkHttpUtil.ResultCallback() {
            @Override
            public void onError(Call call, Exception e) {
                verificationCode = null;
                setVerificationCodeBg();
            }

            @Override
            public void onResponse(byte[] response) {
                verificationCode = response;
                setVerificationCodeBg();
            }
        });
//        OkHttpUtil.getAsync("10.0.2.2:8080/school/getCheckCode", new OkHttpUtil.ResultCallback() {
//            @Override
//            public void onError(Call call, Exception e) {
//                verificationCode = null;
//                setVerificationCodeBg();
//            }
//
//            @Override
//            public void onResponse(byte[] response) {
//                verificationCode = response;
//                setVerificationCodeBg();
//            }
//        });
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
            c.setCampus(SharedPreferencesUtils.getStringFromSP(LoginXKActivity.this, "campus", ""));
        }
        //Log.d("campus", "生成时"+SharedPreferencesUtils.getStringFromSP(LoginXKActivity.this, "campus"));
        Gson gson = new Gson();
        jsonCourse = gson.toJson(allCourse);
        Log.d("Gson", jsonCourse);
        SharedPreferencesUtils.saveStringToSP(getApplicationContext(), "course", jsonCourse);
        Intent i = new Intent(LoginXKActivity.this, ScheduleActivity.class);
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

    public void showDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("使用帮助");
        builder.setMessage("欢迎使用WakeUp课程表。\n\n首次使用步骤：\n\n1.打开WiFi，连接苏大WiFi，不用登录网关，暂时关闭流量\n\n2.点击白色的验证码显示区域更新验证码\n\n3.输入学号和密码登录\n\n限于水平，目前还有一些功能没有实现，也许也会有一些小问题，可通过app侧栏的反馈功能向我们提出宝贵的建议:-)\n\n愿你不辜负每一个清晨。");
        builder.setPositiveButton("我知道啦", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferencesUtils.saveBooleanToSP(getApplicationContext(), "isFirst", false);
            }
        });
        builder.show();
    }
}
