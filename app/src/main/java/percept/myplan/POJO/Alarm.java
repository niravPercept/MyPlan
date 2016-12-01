package percept.myplan.POJO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by percept on 1/8/16.
 */

public class Alarm {

    @SerializedName("alarmname")
    private String alarmName;
    @SerializedName("alarmtime")
    private String alarmTime;
    @SerializedName("status")
    private boolean status;
    @SerializedName("alarmtune")
    private String alarmTune;
    @SerializedName("alarmtunename")
    private String alarmTuneName;
    @SerializedName("alarmrepeat")
    private String alarmRepeat;
    @SerializedName("strategy_id")
    private String strategyId;

    public Alarm(String alarmName, String alarmTime, boolean status, String alarmTune, String alarmRepeat, String strategyId,
                 String alarmTuneName) {
        this.alarmName = alarmName;
        this.alarmTime = alarmTime;
        this.status = status;
        this.alarmTune = alarmTune;
        this.alarmRepeat = alarmRepeat;
        this.alarmTuneName = alarmTuneName;
        this.strategyId = strategyId;
    }

    public String getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(String strategyId) {
        this.strategyId = strategyId;
    }

    public String getAlarmTune() {
        return alarmTune;
    }

    public void setAlarmTune(String alarmTune) {
        this.alarmTune = alarmTune;
    }

    public String getAlarmRepeat() {
        return alarmRepeat;
    }

    public void setAlarmRepeat(String alarmRepeat) {
        this.alarmRepeat = alarmRepeat;
    }


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }


    public String getAlarmTuneName() {
        return alarmTuneName;
    }

    public void setAlarmTuneName(String alarmTuneName) {
        this.alarmTuneName = alarmTuneName;
    }
}
