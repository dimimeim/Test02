package com.example.test.test01;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmeimaroglou on 29/6/2016.
 */
public class utils {
    public static void deleteSchedule(String application, String ID) {
        HashMap<String,HashMap<String,HashMap<String,String>>> appSchedulers = MainActivity.appSchedulers;
        if (appSchedulers.containsKey(application)) {
            HashMap<String,HashMap<String,String>> snSchedulers = appSchedulers.get(application);
            if (snSchedulers.containsKey(ID)) {
                snSchedulers.remove(ID);
                if (snSchedulers.isEmpty()) {
                    appSchedulers.remove(application);
                } else {
                    appSchedulers.put(application, snSchedulers);
                }
                MainActivity.appSchedulers = appSchedulers;
                saveSchedules();
            }
        }
    }

    public static void changeSchedule(String application, String ID, String name, String freq, String time, String max, String packageName) {
        HashMap<String,HashMap<String,HashMap<String,String>>> appSchedulers = MainActivity.appSchedulers;
        HashMap<String,String> values = new HashMap<String,String>();
        values.put("Freq", freq);
        values.put("Time", time);
        values.put("Max", max);
        values.put("Name", name);
        values.put("Package", packageName);
        if (appSchedulers.containsKey(application)) {
            System.out.println("containsValue app: " + application);
            HashMap<String,HashMap<String,String>> snSchedulers = appSchedulers.get(application);
            snSchedulers.put(ID, values);
            appSchedulers.put(application, snSchedulers);
        } else {
            HashMap<String,HashMap<String,String>> snSchedulers = new HashMap<String,HashMap<String,String>>();
            snSchedulers.put(ID, values);
            appSchedulers.put(application, snSchedulers);
        }
        MainActivity.appSchedulers = appSchedulers;
        saveSchedules();
    }

    public static void loadSchedulers() {
        HashMap<String,HashMap<String,HashMap<String,String>>> appSchedulers = new HashMap<String,HashMap<String,HashMap<String,String>>>();

        SharedPreferences prefs = MainActivity.context.getSharedPreferences(MainActivity.MY_PREFS_NAME, MainActivity.context.MODE_PRIVATE);
        String saved = prefs.getString(MainActivity.MY_PREFS_NAME, "");
        System.out.println("newSaved saved:" + saved);
        if ("".equals(saved)) return;
        for (String appPart : saved.split("___")) {
            String application = appPart.split(";;;")[0];
            String schedulerNamesPart =  appPart.split(";;;")[1];
            HashMap<String,HashMap<String,String>> schedulesHash = new HashMap<String,HashMap<String,String>>();
            for (String schedulerPart : schedulerNamesPart.split(":::"))
            {
                HashMap<String,String> values = new HashMap<String,String>();
                String schedulerID = schedulerPart.split(",,,")[0];
                String schedulerFreq = schedulerPart.split(",,,")[1];
                String schedulerTime = schedulerPart.split(",,,")[2];
                String schedulerMax = schedulerPart.split(",,,")[3];
                String schedulerName= schedulerPart.split(",,,")[4];
                String packageName= schedulerPart.split(",,,")[5];
                values.put("Freq", schedulerFreq);
                values.put("Time", schedulerTime);
                values.put("Max", schedulerMax);
                values.put("Name", schedulerName);
                values.put("Package", packageName);
                schedulesHash.put(schedulerID, values);
            }
            appSchedulers.put(application, schedulesHash);
        }

        MainActivity.appSchedulers = appSchedulers;
    }

    public static void saveSchedules() {
        String newSaved = "";
        for (Map.Entry<String, HashMap<String, HashMap<String, String>>> e : MainActivity.appSchedulers.entrySet()) {
            String application    = e.getKey();
            HashMap<String,HashMap<String,String>> appSchedules  = e.getValue();
            System.out.println("dimimeim app: " + appSchedules.toString());
            newSaved += application + ";;;";
            for (Map.Entry<String, HashMap<String, String>> f : appSchedules.entrySet()) {
                String scheduleName = f.getKey();
                if (!"".equals(scheduleName)) {
                    HashMap<String, String> values = f.getValue();
                    String freq = values.get("Freq");
                    String time = values.get("Time");
                    String max = values.get("Max");
                    String name = values.get("Name");
                    String packageName = values.get("Package");
                    String newSN = scheduleName + ",,," + freq + ",,," + time + ",,," + max + ",,," + name + ",,," + packageName + ":::";
                    System.out.println("newSN:" + newSN);
                    newSaved += newSN;
                }
            }

            newSaved += "___";
        }
        System.out.println("newSaved:" + newSaved);
        SharedPreferences.Editor editor = MainActivity.context.getSharedPreferences(MainActivity.MY_PREFS_NAME, MainActivity.context.MODE_PRIVATE).edit();
        editor.putString(MainActivity.MY_PREFS_NAME, newSaved);
        editor.commit();
    }

    public static HashMap<String,HashMap<String,String>> loadSchedules(String application) {
        HashMap<String,HashMap<String,String>> schedules  = new HashMap<String,HashMap<String,String>>();
        if (MainActivity.appSchedulers.containsKey(application)) {
            schedules = MainActivity.appSchedulers.get(application);
        }
        return schedules;
    }

    public static HashMap<String,String> loadValues(String application, String id) {
        HashMap<String,String> values = new HashMap<String,String>();
        HashMap<String,HashMap<String,String>> schedules  = new HashMap<String,HashMap<String,String>>();
        if (MainActivity.appSchedulers.containsKey(application)) {
            schedules = MainActivity.appSchedulers.get(application);
            if (schedules.containsKey(id)) {
                values = schedules.get(id);
            }
        }
        return values;
    }

    public static String getNextSid (String application) {
        HashMap<String,HashMap<String,String>> schedules  = loadSchedules(application);
        int counter = 0;
        while (true) {
            if (!schedules.containsKey(Integer.toString(counter))) break;
            counter++;
        }
        return Integer.toString(counter);
    }

}
