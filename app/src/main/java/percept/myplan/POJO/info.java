package percept.myplan.POJO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by percept on 2/12/16.
 */

public class info {

    @SerializedName("infolink")
    private String infoLink;
    @SerializedName("infothumb")
    private String infoThumb;
    @SerializedName("infotitle")
    private String infoTitle;



    public info(String videoLink, String videoTitle) {
        this.infoLink = videoLink;
//        this.infoThumb = videoThumb;
        this.infoTitle = videoTitle;
    }

    public String getInfoLink() {
        return infoLink;
    }

    public String getInfoThumb() {
        return infoThumb;
    }

    public String getInfoTitle() {
        return infoTitle;
    }
}
