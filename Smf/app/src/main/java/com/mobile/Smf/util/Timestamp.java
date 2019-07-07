package com.mobile.Smf.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Timestamp {
    private int year;
    private int month;
    private int date;
    private int hour;
    private int minute;
    private int sec;

    private int localYear;
    private int localMonth;
    private int localDate;
    private int localHour;
    private int localMinute;
    private int localSec;

    GregorianCalendar local;
    GregorianCalendar universal;

    long systemTime;
    String localTime;
    String universalTime;

    public Timestamp() {
        local = new GregorianCalendar();
        universal = new GregorianCalendar(TimeZone.getTimeZone("Europe/London"));
        systemTime = System.currentTimeMillis();
        initialize();
    }

    public void printTest() {
        System.out.println("Local Values:");
        System.out.println("Year "+localYear);
        System.out.println("Month "+localMonth);
        System.out.println("Date "+localDate);
        System.out.println("Hour "+localHour);
        System.out.println("Minute "+localMinute);
        System.out.println("Sec "+localSec);
        System.out.println("Universal Values:");
        System.out.println("Year "+year);
        System.out.println("Month "+month);
        System.out.println("Date "+date);
        System.out.println("Hour "+hour);
        System.out.println("Minute "+minute);
        System.out.println("Sec "+sec);
        System.out.println("Universal Time "+universalTime);
        System.out.println("Local Time "+localTime);
        System.out.println("System Time "+systemTime);
    }

    private void initialize(){
        //local & universal values (GMT Europe/London)
        localSec = local.get(Calendar.SECOND);
        sec = universal.get(Calendar.SECOND);
        localMinute = local.get(Calendar.MINUTE);
        minute = universal.get(Calendar.MINUTE);
        localHour = local.get(Calendar.HOUR_OF_DAY);
        hour = universal.get(Calendar.HOUR_OF_DAY);
        localDate = local.get(Calendar.DATE);
        date = universal.get(Calendar.DATE);
        localMonth = local.get(Calendar.MONTH) + 1;
        month = universal.get(Calendar.MONTH) + 1;
        localYear = local.get(Calendar.YEAR);
        year = universal.get(Calendar.YEAR);
        stringTimes();
    }

    //Create string for both local and universal time
    private void stringTimes() {
        String time = "";
        time += year;
        time += month < 10 ? "0"+month : month;;
        time += date < 10 ? "0"+date : date;
        time += hour < 10 ? "0"+hour : hour;
        time += minute < 10 ? "0"+minute : minute;
        time += sec < 10 ? "0"+sec : sec;
        universalTime = time;

        String lTime = "";
        lTime += localYear;
        lTime += localMonth< 10 ? "0"+localMonth : localMonth;
        lTime += localDate < 10 ? "0"+localDate : localDate;
        lTime += localHour < 10 ? "0"+localHour : localHour;
        lTime += localMinute < 10 ? "0"+localMinute : localMinute;
        lTime += localSec < 10 ? "0"+localSec : localSec;
        localTime = lTime;
    }

    /*  Getter methods  */

    public long getSystemTime() {
        return systemTime;
    }

    public String getLocalTime() {
        return localTime;
    }

    public String getUniversalTime() {
        return universalTime;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDate() {
        return date;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSec() {
        return sec;
    }

    public int getLocalYear() {
        return localYear;
    }

    public int getLocalMonth() {
        return localMonth;
    }

    public int getLocalDate() {
        return localDate;
    }

    public int getLocalHour() {
        return localHour;
    }

    public int getLocalMinute() {
        return localMinute;
    }

    public int getLocalSec() {
        return localSec;
    }

}