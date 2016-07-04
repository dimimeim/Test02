package com.example.test.test01;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dmeimaroglou on 28/6/2016.
 */
public class SettingsListAdapter extends ArrayAdapter<ListItemVO> {
    String application = "";

    public SettingsListAdapter(Context context, int resource, List<ListItemVO> items, String application) {
        super(context, resource, items);
        this.application = application;
    }

    public int addItem(ListItemVO item) {
        add(item);
        return this.getCount();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.scheduler_item, null);
        }
        ArrayAdapter<CharSequence> time_adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.timeList, android.R.layout.simple_spinner_item);

        ArrayAdapter<CharSequence> frequent_adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.everyList, android.R.layout.simple_spinner_item);

        ArrayAdapter<CharSequence> max_adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.numberList, android.R.layout.simple_spinner_item);

        ImageView deleteScheduleButton = (ImageView) v.findViewById(R.id.deleteSchedule);

        final String sceduleID = getItem(position).getID();
        HashMap<String, String> values = utils.loadValues(application, sceduleID);
        final String sceduleName = values.get("Name");

        final String packageName = getItem(position).getPackageName();
        final TextView packageNameText = (TextView) v.findViewById(R.id.packageName);
        packageNameText.setText(packageName);
        final TextView idText = (TextView) v.findViewById(R.id.scheduleID);
        final EditText editText = (EditText) v.findViewById(R.id.applicationSettingsTextEntry);
        editText.setText(sceduleName);

        Button saveBut = (Button) v.findViewById(R.id.saveButton);

        final Spinner frequent_spinner = (Spinner) v.findViewById(R.id.frequent_spinner);
        final Spinner time_spinner = (Spinner) v.findViewById(R.id.time_spinner);
        final Spinner max_spinner = (Spinner) v.findViewById(R.id.max_spinner);

        frequent_spinner.setAdapter(frequent_adapter);
        time_spinner.setAdapter(time_adapter);
        max_spinner.setAdapter(max_adapter);

        idText.setText(values.get("ID"));
        frequent_spinner.setSelection(frequent_adapter.getPosition(values.get("Freq")));
        time_spinner.setSelection(time_adapter.getPosition(values.get("Time")));
        max_spinner.setSelection(max_adapter.getPosition(values.get("Max")));

        deleteScheduleButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.deleteSchedule(application , sceduleID);
                remove(getItem(position));
            }
        })
        );

        saveBut.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String freq = frequent_spinner.getSelectedItem().toString();
                        String time = time_spinner.getSelectedItem().toString();
                        String max = max_spinner.getSelectedItem().toString();
                        String name = editText.getText().toString();
                        utils.changeSchedule(application, sceduleID, name, freq, time, max, packageName);


                        PendingIntent pendingIntent = null;
                        Intent intent = new Intent("dimimeim.intent.alarm");
                        intent.putExtra("Application", application);
                        intent.putExtra("Package", packageName);
                        pendingIntent = PendingIntent.getBroadcast( MainActivity.context, 0, intent, 0 );
                    }
                })
        );

        return v;
    }

}