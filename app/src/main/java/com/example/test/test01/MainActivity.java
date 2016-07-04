package com.example.test.test01;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static HashMap<String,HashMap<String,HashMap<String,String>>> appSchedulers = new HashMap<String,HashMap<String,HashMap<String,String>>>();
    String TAG = "test01";
    static Context context = null;
    static ListAdapter non_existing_adapter = null;
    static ListAdapter existing_adapter = null;
    static SettingsListAdapter application_settings_adapter = null;
    static ListView main_list = null;
    static String viewMode = "viewExisting";
    FloatingActionButton fab = null;
    static FloatingActionButton fab2 = null;
    static String MY_PREFS_NAME = "SCHEDULES";
    static String saveList = "";
    static String editingApp = "";
    static String editingPackage = "";
    PendingIntent pendingIntent = null;
    long day = 1000 * 60 * 60 * 24;
    long week = 1000 * 60 * 60 * 24 * 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        List<ListItemVO> ls = new ArrayList<ListItemVO>();
        utils.loadSchedulers();
        for (Map.Entry<String, HashMap<String, HashMap<String, String>>> e : MainActivity.appSchedulers.entrySet()) {
            String application    = e.getKey();
            String packageName = null;
            HashMap<String, HashMap<String, String>> sn = e.getValue();
            for (Map.Entry<String, HashMap<String, String>> value : sn.entrySet()){
                packageName = value.getValue().get("Package");
                break;
            }
            ls.add(new ListItemVO(application, packageName, true));
        }

        existing_adapter = new ListAdapter(context, R.layout.list_item, ls, "viewExisting");
        main_list = (ListView) findViewById(R.id.main_list);

        main_list.setAdapter(existing_adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewMode.equals("viewExisting")) {
                    PackageManager pm = getPackageManager();
                    Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

                    //get a list of installed apps.
                    List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
                    non_existing_adapter = new ListAdapter(context, R.layout.list_item, new ArrayList<ListItemVO>(), "addNew");
                    for (ApplicationInfo packageInfo : packages) {
                        String applicationName = (String) (packageInfo != null ? pm.getApplicationLabel(packageInfo) : "Unknown");
                        String applicationPackage = packageInfo.packageName;
                        String applicationSource = packageInfo.sourceDir;
                        if (!existing_adapter.containsString(applicationName) && !applicationPackage.equals(applicationName))
                            non_existing_adapter.add(new ListItemVO(applicationName, applicationPackage, false));
                    }

                    main_list.setAdapter(non_existing_adapter);
                    viewMode = "addNew";
                } else if (viewMode.equals("addNew")) {
                    for (int i = 0; i<non_existing_adapter.getCount(); i++) {
                        if (!existing_adapter.containsString(non_existing_adapter.getItem(i).getLabel()) &&
                                non_existing_adapter.getItem(i).isChecked() ) {
                            existing_adapter.add(new ListItemVO(non_existing_adapter.getItem(i).getLabel(), non_existing_adapter.getItem(i).getPackageName(), true));
                    }

                    }
                    main_list.setAdapter(existing_adapter);
                    viewMode = "viewExisting";
                } else if (viewMode.equals("editSchedule")) {
                    ListItemVO tmp = new ListItemVO(editingApp, editingPackage, false );
                    String id = utils.getNextSid(editingApp);
                    tmp.setID(id);
                    application_settings_adapter.addItem(tmp);
                }
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });


        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setVisibility(View.INVISIBLE);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        pendingIntent = PendingIntent.getBroadcast( this, 0, new Intent("dimimeim.intent.alarm"), 0 );
        //SetAlarm();
        //startAt10(17, 42);
        //Scheduler.getDates("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            SetAlarm();
            for (int i = 0; i<non_existing_adapter.getCount(); i++) {
                if (!existing_adapter.containsString(non_existing_adapter.getItem(i).getLabel()) &&
                        non_existing_adapter.getItem(i).isChecked() ) {
                    existing_adapter.add(new ListItemVO(non_existing_adapter.getItem(i).getLabel(), non_existing_adapter.getItem(i).getPackageName(), true));
                }

            }
            main_list.setAdapter(existing_adapter);
            viewMode = "viewExisting";
            fab2.setVisibility(View.INVISIBLE);
            return false; //I have tried here true also
        }
        return super.onKeyDown(keyCode, event);
    }


    public void SetAlarm()
    {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 8000;
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

    }

    public void cancel() {

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
    }

    public void startAt10(int hour, int min) {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 60 * 20;

        /* Set the alarm to start at 10:30 AM */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        System.out.println(calendar.getTime());
        //calendar.set(Calendar.HOUR_OF_DAY, hour);
        //calendar.set(Calendar.MINUTE, min);
        calendar.add(Calendar.MINUTE, 1);
        System.out.println(calendar.getTime());

        /* Repeating on every 10s interval */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 90, pendingIntent);
        calendar.add(Calendar.MINUTE, 1);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 90, pendingIntent);
        calendar.add(Calendar.MINUTE, 1);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 90, pendingIntent);
    }
}
