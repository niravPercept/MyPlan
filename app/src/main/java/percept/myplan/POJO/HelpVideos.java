package percept.myplan.POJO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by percept on 31/8/16.
 */

public class HelpVideos {
    @SerializedName("link")
    private String videoLink;
    @SerializedName("thumb")
    private String videoThumb;
    @SerializedName("title")
    private String videoTitle;

    public HelpVideos(String videoLink, String videoThumb, String videoTitle) {
        this.videoLink = videoLink;
        this.videoThumb = videoThumb;
        this.videoTitle = videoTitle;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public String getVideoThumb() {
        return videoThumb;
    }

    public String getVideoTitle() {
        return videoTitle;
    }
}
