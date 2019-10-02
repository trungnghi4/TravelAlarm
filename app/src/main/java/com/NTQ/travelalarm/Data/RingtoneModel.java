package com.NTQ.travelalarm.Data;

public class RingtoneModel {
    private String nameRingtone;
    private String timeRingtone;

    public RingtoneModel(String nameRingtone, String timeRingtone) {
        this.nameRingtone=nameRingtone;
        this.timeRingtone=timeRingtone;
    }

    public String getNameRingtone() {
        return nameRingtone;
    }

    public void setNameRingtone(String nameRingtone) {
        this.nameRingtone = nameRingtone;
    }

    public String getTimeRingtone() {
        return timeRingtone;
    }

    public void setTimeRingtone(String timeRingtone) {
        this.timeRingtone = timeRingtone;
    }
}
