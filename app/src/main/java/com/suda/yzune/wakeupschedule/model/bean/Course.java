package com.suda.yzune.wakeupschedule.model.bean;

/**
 * Created by YZune on 2017/9/5.
 */

public class Course {
    private String name, room, teach, id;//课程名称、上课教室，教师，课程编号
    int start, step, day, startWeek, endWeek, isOdd;    //开始上课节次， 一共几节课

    public Course(String name, String room, int start, int step,
                  String teach, String id, int day, int startWeek, int endWeek, int isOdd) {
        super();
        this.name = name;
        this.room = room;
        this.start = start;
        this.step = step;
        this.teach = teach;
        this.id = id;
        this.day = day;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.isOdd = isOdd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getTeach() {
        return teach;
    }

    public void setTeach(String teach) {
        this.teach = teach;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(int startWeek) {
        this.startWeek = startWeek;
    }

    public int getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(int endWeek) {
        this.endWeek = endWeek;
    }

    public int getIsOdd() {
        return isOdd;
    }

    public void setIsOdd(int isOdd) {
        this.isOdd = isOdd;
    }
}

