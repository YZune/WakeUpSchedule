package com.suda.yzune.wakeupschedule.utils;

import com.suda.yzune.wakeupschedule.model.bean.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YZune on 2017/9/9.
 */

public class CourseUtils {

    public static List<Course> makeCourseTogether(List<Course> courseList) {
        int i = 0;
        List<Course> list = new ArrayList<Course>();
        while (i < courseList.size()) {
            if (!(i == courseList.size() - 1) && (courseList.get(i).getName().equals(courseList.get(i + 1).getName())) && (courseList.get(i).getRoom().equals(courseList.get(i + 1).getRoom())) && (courseList.get(i).getStartWeek() == courseList.get(i + 1).getStartWeek())) {
                courseList.get(i).setStep(courseList.get(i).getStep() + courseList.get(i + 1).getStep());
                list.add(courseList.get(i));
                i += 2;
            } else {
                list.add(courseList.get(i));
                i += 1;
            }
        }
        return list;
    }

}
