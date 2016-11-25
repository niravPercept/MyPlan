package percept.myplan.POJO;

/**
 * Created by percept on 26/7/16.
 */

public class Symptom {

    private String state;
    private String id;
    private String ordering;
    private String title;
    private String strategy_id;
    private String description;

    public Symptom(String state, String id, String ordering, String title, String strategy_id, String description) {
        this.state = state;
        this.id = id;
        this.ordering = ordering;
        this.title = title;
        this.strategy_id = strategy_id;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrdering() {
        return ordering;
    }

    public void setOrdering(String ordering) {
        this.ordering = ordering;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStrategy_id() {
        return strategy_id;
    }

    public void setStrategy_id(String strategy_id) {
        this.strategy_id = strategy_id;
    }
}
