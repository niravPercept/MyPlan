package percept.myplan.POJO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by percept on 11/8/16.
 */

public class Mood {

    @SerializedName("state")
    private String State;
    @SerializedName("id")
    private String ID;
    @SerializedName("ordering")
    private String Ordering;
    @SerializedName("user_id")
    private String USER_ID;
    @SerializedName("note")
    private String NOTE;
    @SerializedName("mood_date")
    private String MOOD_DATE;
    @SerializedName("measurement")
    private String MEASUREMENT;
    private String MOOD_DATE_STRING;

    public Mood(String state, String ID, String ordering, String USER_ID, String NOTE,
                String MOOD_DATE, String MEASUREMENT) {
        this.State = state;
        this.ID = ID;
        this.Ordering = ordering;
        this.USER_ID = USER_ID;
        this.NOTE = NOTE;
        this.MOOD_DATE = MOOD_DATE;
        this.MEASUREMENT = MEASUREMENT;
    }

    public String getMOOD_DATE_STRING() {
        return MOOD_DATE_STRING;
    }

    public void setMOOD_DATE_STRING(String MOOD_DATE_STRING) {
        this.MOOD_DATE_STRING = MOOD_DATE_STRING;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getOrdering() {
        return Ordering;
    }

    public void setOrdering(String ordering) {
        Ordering = ordering;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getNOTE() {
        return NOTE;
    }

    public void setNOTE(String NOTE) {
        this.NOTE = NOTE;
    }

    public String getMOOD_DATE() {
        return MOOD_DATE;
    }

    public void setMOOD_DATE(String MOOD_DATE) {
        this.MOOD_DATE = MOOD_DATE;
    }

    public String getMEASUREMENT() {
        return MEASUREMENT;
    }

    public void setMEASUREMENT(String MEASUREMENT) {
        this.MEASUREMENT = MEASUREMENT;
    }


}
