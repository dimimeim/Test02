package com.example.test.test01;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dmeimaroglou on 1/7/2016.
 */
public class Scheduler {
    public static Calendar getNextCalendar(String application, String time, String frequent) {
        Calendar application_last_run = getLastBackUP(application);
        Calendar calendar = Calendar.getInstance();
        if ( application_last_run == null ) {
            String hour = time.substring(0, time.indexOf(":"));
            String minute = time.substring(time.indexOf(":") + 1);
            Calendar current_calendar = Calendar.getInstance();
            current_calendar.setTimeInMillis(System.currentTimeMillis());
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
            while (current_calendar.getTimeInMillis() > calendar.getTimeInMillis()) {
                calendar.add(Calendar.HOUR, 24);
            }
        } else {
            Calendar current_calendar = Calendar.getInstance();
            current_calendar.setTimeInMillis(System.currentTimeMillis());
            if (frequent.equals("DAY")) application_last_run.add(Calendar.HOUR, 24);
            if (frequent.equals("WEEK")) application_last_run.add(Calendar.HOUR, 24*7);
            while (current_calendar.getTimeInMillis() > application_last_run.getTimeInMillis()) {
                if (frequent.equals("DAY")) application_last_run.add(Calendar.HOUR, 24);
                if (frequent.equals("WEEK")) application_last_run.add(Calendar.HOUR, 24*7);
            }
            calendar = application_last_run;
        }
        return calendar;
    }

    public static List<String> getDates(String application) {
        List<String> dates = new ArrayList<String>();
        File f = new File("//sdcard//");
        File file[] = f.listFiles();
        if (file == null) return dates;
        Log.d("Files", "Size: "+ file.length);
        for (int i=0; i < file.length; i++)
        {
            Log.d("Files", "FileName:" + file[i].getName());
            // LOOP
            String filename = "gfdg_day_month_year.xip";
            filename = filename.substring(0, filename.lastIndexOf("."));
            String this_app = filename.substring(0, filename.indexOf("_"));
            String date = filename.substring(filename.indexOf(":")+1);
            String day = date.substring(0, date.indexOf("_"));
            date = date.substring(date.indexOf(":")+1);
            String month = date.substring(0, date.indexOf("_"));
            String year = date.substring(date.indexOf(":")+1);
            dates.add(day + "/" + month + "/" + year);
            // END LOOP
        }
        return dates;
    }

    public static Calendar getLastBackUP(String application) {
        Calendar retCalendar = null;
        List<String> application_dates = getDates(application);
        for (String date : application_dates) {
            String tmp = date;
            String day = tmp.substring(0, tmp.indexOf("/"));
            tmp = tmp.substring(tmp.indexOf("/") + 1 );
            String month = tmp.substring(0, tmp.indexOf("/"));
            tmp = tmp.substring(tmp.indexOf("/") + 1 );
            String year = tmp;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
            if (retCalendar == null || calendar.getTimeInMillis() > retCalendar.getTimeInMillis())
                retCalendar = calendar;
        }
        return retCalendar;
    }
}
