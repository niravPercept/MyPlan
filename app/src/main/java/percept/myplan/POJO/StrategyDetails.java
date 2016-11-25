package percept.myplan.POJO;

/**
 * Created by percept on 4/8/16.
 */


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by percept on 26/7/16.
 */

public class StrategyDetails {

    @SerializedName("created_by")
    private String CreatedBy;

    @SerializedName("link")
    private String Link;

    @SerializedName("videos")
    private String Videos;

    @SerializedName("state")
    private String State;

    @SerializedName("modified_by_name")
    private String ModifyByName;

    @SerializedName("image")
    private String Image;

    @SerializedName("checked_out_time")
    private String CheckedOutTime;

    @SerializedName("created_by_name")
    private String CreatedByName;

    @SerializedName("contacts")
    private String Contacts;

    @SerializedName("contact")
    private List<StrategyContact> Contact;

    @SerializedName("cat_id")
    private String CategoryId;

    @SerializedName("id")
    private String ID;

    @SerializedName("title")
    private String Title;

    @SerializedName("modified_by")
    private String ModifiedBy;

    @SerializedName("checked_out")
    private String CheckedOut;

    @SerializedName("description")
    private String Description;

    @SerializedName("ordering")
    private String Ordering;

    @SerializedName("user_id")
    private String User_Id;

    @SerializedName("internal_audio")
    private String InternalAudio;

    public String getInternalAudio() {
        return InternalAudio;
    }

    public void setInternalAudio(String internalAudio) {
        InternalAudio = internalAudio;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getOrdering() {
        return Ordering;
    }

    public void setOrdering(String ordering) {
        Ordering = ordering;
    }

    public List<StrategyContact> getContact() {
        return Contact;
    }

    public void setContact(List<StrategyContact> contacts) {
        Contact = contacts;
    }

    public String getContacts() {
        return Contacts;
    }

    public void setContacts(String contacts) {
        Contacts = contacts;
    }


    public String getUser_Id() {
        return User_Id;
    }

    public void setUser_Id(String user_Id) {
        User_Id = user_Id;
    }

    public String getVideos() {
        return Videos;
    }

    public void setVideos(String videos) {
        Videos = videos;
    }

    public String getModifyByName() {
        return ModifyByName;
    }

    public void setModifyByName(String modifyByName) {
        ModifyByName = modifyByName;
    }

    public String getCheckedOutTime() {
        return CheckedOutTime;
    }

    public void setCheckedOutTime(String checkedOutTime) {
        CheckedOutTime = checkedOutTime;
    }

    public String getCreatedByName() {
        return CreatedByName;
    }

    public void setCreatedByName(String createdByName) {
        CreatedByName = createdByName;
    }


    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getModifiedBy() {
        return ModifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        ModifiedBy = modifiedBy;
    }

    public String getCheckedOut() {
        return CheckedOut;
    }

    public void setCheckedOut(String checkedOut) {
        CheckedOut = checkedOut;
    }


    private boolean isSelected = false;

    public StrategyDetails(String created_by, String music, String id, String title, String contact_id, String description, String link, String state, String ordering, String image, boolean isSelected) {

        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
