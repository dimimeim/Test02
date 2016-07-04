package com.example.test.test01;

import android.app.LauncherActivity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmeimaroglou on 28/6/2016.
 */
public class ListAdapter extends ArrayAdapter<ListItemVO> {
    boolean existing = false;
    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int resource, List<ListItemVO> items, String viewMode) {
        super(context, resource, items);
        if (viewMode!=null && viewMode.equals("viewExisting")) {
            existing = true;
        } else {
            existing = false;
        }
    }

    public int addItem(ListItemVO item) {
        add(item);
        return this.getCount();
    }

    public boolean containsString(String item) {
        for (int i = 0; i<getCount(); i++) {
            if (getItem(i).getLabel().equals(item)) return true;
        }
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item, null);
        }

        final ListItemVO item = getItem(position);
        final String label = item.getLabel();
        if (label != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.textView);
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox);

            if (tt1 != null) {
                tt1.setText(label);
                tt1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (existing) {
                            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                            final List pkgAppsList = MainActivity.context.getPackageManager().queryIntentActivities(mainIntent, 0);
                            MainActivity.non_existing_adapter = new ListAdapter(MainActivity.context, R.layout.list_item, new ArrayList<ListItemVO>(), "addNew");
                            List<ListItemVO> ls = new ArrayList<ListItemVO>();
                            HashMap<String,HashMap<String,String>> schedules = utils.loadSchedules(label);
                            String packageName = null;
                            for (Map.Entry<String, HashMap<String, String>> e : schedules.entrySet()) {
                                String id = e.getKey();
                                String name = e.getValue().get("Name");
                                packageName = e.getValue().get("Package");
                                ListItemVO tmp = new ListItemVO(name, packageName, false);
                                tmp.setID(id);
                                ls.add(tmp);
                            }
                            MainActivity.editingApp = label;
                            MainActivity.editingPackage = getItem(position).getPackageName();
                            MainActivity.application_settings_adapter = new SettingsListAdapter(MainActivity.context, R.layout.scheduler_item, ls, label);
                            MainActivity.viewMode = "editSchedule";
                            MainActivity.main_list.setAdapter(MainActivity.application_settings_adapter);
                            MainActivity.fab2.setVisibility(View.VISIBLE);
                        } else {
                            for (int i = 0; i < getCount(); i++) {
                                if (getItem(i).getLabel().equals(label)) {
                                    changeCheckBox(i);
                                    break;
                                }
                            }
                        }
                    }
                });
            }

            if (checkBox != null) {
                checkBox.setChecked(getItem(position).isChecked());
            }
        }
        return v;
    }

    public void changeCheckBox(int position) {
        ListItemVO item = getItem(position);
        remove(item);
        item.setChecked(!item.isChecked());
        insert(item, position);
        notifyDataSetChanged();
    }

}