package percept.myplan.POJO;

import java.io.Serializable;

/**
 * Created by percept on 30/7/16.
 */

public class ContactDisplay implements Comparable<ContactDisplay>, Serializable {
    private String created_by = "";
    private String id = "";
    private String first_name = "";
    private String phone = "";
    private String email = "";
    private String helplist = "";
    private String state = "";
    private String last_name = "";
    private String ordering = "";
    private String note = "";
    private String con_image = "";
    private String skype = "";
    private String web_address = "";
    private String address = "";
    private String company_name = "";
    private String ringtone = "";
    private String con_image_thumb = "";
    private String emergency = "";
    private boolean isSelected = false;



    private boolean originalSelection;


    private String OrgName = "";
    public ContactDisplay() {
    }

//    public ContactDisplay(String created_by, String id, String first_name, String phone, String email,
//                          String helplist, String state, String last_name, String ordering,
//                          String note, String con_image, String skype) {
//        this.created_by = created_by;
//        this.id = id;
//        this.first_name = first_name;
//        this.phone = phone;
//        this.email = email;
//        this.helplist = helplist;
//        this.state = state;
//        this.last_name = last_name;
//        this.ordering = ordering;
//        this.note = note;
//        this.con_image = con_image;
//        this.skype = skype;
//    }
public boolean isOriginalSelection() {
    return originalSelection;
}

    public void setOriginalSelection(boolean originalSelection) {
        this.originalSelection = originalSelection;
    }
    public String getOrgName() {
        return OrgName;
    }

    public void setOrgName(String orgName) {
        OrgName = orgName;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getCon_image() {
        return con_image;
    }

    public void setCon_image(String con_image) {
        this.con_image = con_image;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOrdering() {
        return ordering;
    }

    public void setOrdering(String ordering) {
        this.ordering = ordering;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getHelplist() {
        return helplist;
    }

    public void setHelplist(String helplist) {
        this.helplist = helplist;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getWeb_address() {
        return web_address;
    }

    public void setWeb_address(String web_address) {
        this.web_address = web_address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    public String getCon_image_thumb() {
        return con_image_thumb;
    }

    public void setCon_image_thumb(String con_image_thumb) {
        this.con_image_thumb = con_image_thumb;
    }

    @Override
    public int compareTo(ContactDisplay contactDisplay) {
        return first_name.compareTo(contactDisplay.getFirst_name());
    }

    public String getEmergency() {
        return emergency;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
    }
}
