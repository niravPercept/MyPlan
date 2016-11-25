package percept.myplan.POJO;

/**
 * Created by percept on 16/8/16.
 */

public class SidaSchedule {

    private String name;

    private boolean selected;

    public SidaSchedule(String name, boolean selected) {
        this.name = name;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
