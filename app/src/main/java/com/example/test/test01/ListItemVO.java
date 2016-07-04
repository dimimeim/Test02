package com.example.test.test01;

/**
 * Created by dmeimaroglou on 28/6/2016.
 */
public class ListItemVO {
    boolean existing = false;
    String label = "";
    String packageName = "";
    String application = "";
    String ID = "";
    boolean checked = false;

    ListItemVO (String application, String packageName, boolean existing) {
        this.label = label;
        this.packageName = packageName;
        this.existing = existing;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isExisting() {
        return existing;
    }

    public void setExisting(boolean existing) {
        this.existing = existing;
    }
}
