package percept.myplan.POJO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by percept on 26/7/16.
 */

public class StrategyContact {


    @SerializedName("id")
    private String ID;
    @SerializedName("first_name")
    private String NAME;

    public StrategyContact(String ID, String NAME) {
        this.ID = ID;
        this.NAME = NAME;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }


}
