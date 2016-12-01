package percept.myplan.POJO;

/**
 * Created by percept on 26/7/16.
 */

public class SymptomStrategy {


    private String id;
    private String title;

    public SymptomStrategy(String id, String title) {
        this.id = id;
        this.title = title;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
