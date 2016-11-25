package percept.myplan.POJO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by percept on 22/7/16.
 */

public class Hope {

    @SerializedName("state")
    private String State;
    @SerializedName("created_by")
    private String Created_By;
    @SerializedName("id")
    private String ID;
    @SerializedName("ordering")
    private String Ordering;
    @SerializedName("cover")
    private String IMG_COVER;
    @SerializedName("title")
    private String TITLE;
    @SerializedName("cover_thumb")
    private String THUMB_COVER;

    public Hope(String state, String created_By, String ID, String ordering, String IMG_COVER, String TITLE,String thumb) {
        State = state;
        Created_By = created_By;
        this.ID = ID;
        Ordering = ordering;
        this.IMG_COVER = IMG_COVER;
        this.TITLE = TITLE;
        this.THUMB_COVER=thumb;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCreated_By() {
        return Created_By;
    }

    public void setCreated_By(String created_By) {
        Created_By = created_By;
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

    public String getIMG_COVER() {
        return IMG_COVER;
    }

    public void setIMG_COVER(String IMG_COVER) {
        this.IMG_COVER = IMG_COVER;
    }

    public String getTHUMB_COVER() {
        return THUMB_COVER;
    }

    public void setTHUMB_COVER(String THUMB_COVER) {
        this.THUMB_COVER = THUMB_COVER;
    }
}
