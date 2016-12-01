package percept.myplan.POJO;

/**
 * Created by percept on 1/8/16.
 */

public class AlarmRepeat {

    private String alarmDay;
    private boolean selected;
    private int id;

    public String getAlarmDay() {
        return alarmDay;
    }

    public void setAlarmDay(String alarmDay) {
        this.alarmDay = alarmDay;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
