package percept.myplan.POJO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by percept on 16/8/16.
 */

public class SidaQuestion implements Comparable<SidaQuestion> {
    @SerializedName("id")
    private String ID;
    @SerializedName("question")
    private String Question;
    @SerializedName("ordering")
    private String Ordering;
    @SerializedName("state")
    private String State;
    private String SIDA_ANSWER;
    @SerializedName("answer1")
    private String labelLeft;
    @SerializedName("answer11")
    private String labelRight;

    public SidaQuestion(String ID, String labelLeft, String labelRight, String ordering, String question, String SIDA_ANSWER, String state) {
        this.ID = ID;
        this.labelLeft = labelLeft;
        this.labelRight = labelRight;
        Ordering = ordering;
        Question = question;
        this.SIDA_ANSWER = SIDA_ANSWER;
        State = state;
    }

    public String getSIDA_ANSWER() {
        return SIDA_ANSWER;
    }

    public void setSIDA_ANSWER(String SIDA_ANSWER) {
        this.SIDA_ANSWER = SIDA_ANSWER;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getOrdering() {
        return Ordering;
    }

    public void setOrdering(String ordering) {
        Ordering = ordering;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getLabelLeft() {
        return labelLeft;
    }

    public void setLabelLeft(String labelLeft) {
        this.labelLeft = labelLeft;
    }

    public String getLabelRight() {
        return labelRight;
    }

    public void setLabelRight(String labelRight) {
        this.labelRight = labelRight;
    }

    @Override
    public int compareTo(SidaQuestion sidaQuestion) {
        return ID.compareTo(sidaQuestion.getID());
    }
}
