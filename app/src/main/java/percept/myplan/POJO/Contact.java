package percept.myplan.POJO;

import org.apache.http.util.TextUtils;

/**
 * Created by percept on 11/7/16.
 */

public class Contact implements Comparable<Contact> {

    private String firstName = "";
    private String lastName = "";
    private String phoneNo = "";
    private String email = "";
    private String webURL = "";
    private String contactImgURI = "";
    private String ringtoneURI = "";
    private String note = "";
    private String skypeName = "";
    private String address = "";
    private String orgName = "";
    private String contactID = "";
    private String WEB_ID = "";
    private boolean isSelected;



    private String helplist = "";

    private String emergency = "";
    private String id = "";
    private boolean originalSelection;
    public Contact() {
    }

   /* public Contact(String firstName, String phoneNo, String contactID, boolean isSelected, boolean originalSelection, String web_id) {
        this.firstName = firstName;
        this.phoneNo = phoneNo;
        ContactID = contactID;
        this.isSelected = isSelected;
        this.OriginalSelection = originalSelection;
        this.WEB_ID = web_id;
    }*/

    public Contact(String firstName, String lastName, String phoneNo, String email, String webURL, String contactImgURI, String ringtoneURI, String note, String skypeName, String address, String orgName, String contactID, String WEB_ID, boolean isSelected, boolean originalSelection) {
        if (!TextUtils.isEmpty(firstName))
            this.firstName = firstName;
        if (!TextUtils.isEmpty(lastName))
            this.lastName = lastName;
        if (!TextUtils.isEmpty(phoneNo))
            this.phoneNo = phoneNo;
        if (!TextUtils.isEmpty(email))
            this.email = email;
        if (!TextUtils.isEmpty(webURL))
            this.webURL = webURL;
        if (!TextUtils.isEmpty(contactImgURI))
            this.contactImgURI = contactImgURI;
        if (!TextUtils.isEmpty(ringtoneURI))
            this.ringtoneURI = ringtoneURI;
        if (!TextUtils.isEmpty(note))
            this.note = note;
        if (!TextUtils.isEmpty(skypeName))
            this.skypeName = skypeName;
        if (!TextUtils.isEmpty(address))
            this.address = address;
        if (!TextUtils.isEmpty(orgName))
            this.orgName = orgName;
        if (!TextUtils.isEmpty(WEB_ID))
            this.WEB_ID = WEB_ID;
        this.isSelected = isSelected;
        this.contactID = contactID;
        this.originalSelection = originalSelection;
    }
    public String getHelplist() {
        return helplist;
    }

    public void setHelplist(String helplist) {
        this.helplist = helplist;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmergency() {
        return emergency;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebURL() {
        return webURL;
    }

    public void setWebURL(String webURL) {
        this.webURL = webURL;
    }

    public String getContactImgURI() {
        return contactImgURI;
    }

    public void setContactImgURI(String contactImgURI) {
        this.contactImgURI = contactImgURI;
    }

    public String getRingtoneURI() {
        return ringtoneURI;
    }

    public void setRingtoneURI(String ringtoneURI) {
        this.ringtoneURI = ringtoneURI;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSkypeName() {
        return skypeName;
    }

    public void setSkypeName(String skypeName) {
        this.skypeName = skypeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getContactID() {
        return contactID;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    public boolean isOriginalSelection() {
        return originalSelection;
    }

    public void setOriginalSelection(boolean originalSelection) {
        this.originalSelection = originalSelection;
    }

    public String getWEB_ID() {
        return WEB_ID;
    }

    public void setWEB_ID(String WEB_ID) {
        this.WEB_ID = WEB_ID;
    }

    @Override
    public int compareTo(Contact contact) {
        return firstName.compareTo(contact.getFirstName());
    }
}
