package percept.myplan.POJO;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by percept on 20/7/16.
 */

public class QuickMessage implements Serializable {

    @SerializedName("msg")
    private String message;
    @SerializedName("phone")
    private String contactN0;
    @SerializedName("n_msg")
    private String notificationMessage;
    @SerializedName("id")
    private String contactID;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;

    public QuickMessage(String contactID, String firstName, String lastName, String contactN0, String notificationMessage, String message) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.message = message;
        this.contactN0 = contactN0;
        this.contactID = contactID;
        this.notificationMessage = notificationMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getContactN0() {
        return contactN0;
    }

    public void setContactN0(String contactN0) {
        this.contactN0 = contactN0;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getContactID() {
        return contactID;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
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
}
