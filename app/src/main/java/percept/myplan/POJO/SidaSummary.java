package percept.myplan.POJO;

import com.google.gson.annotations.SerializedName;

public class SidaSummary {
    @SerializedName("week_number")
    private String Week_Number;
    @SerializedName("avg_perc")
    private String avg_perc;
    @SerializedName("avg_score")
    private String Avg_Score;
    @SerializedName("month_number")
    private String monthNumber;
    @SerializedName("year")
    private String year;

    public SidaSummary(String week_Number, String avg_Score, String monthNumber, String year) {
        Week_Number = week_Number;
        Avg_Score = avg_Score;
        this.monthNumber = monthNumber;
        this.year = year;
    }

    public String getWeek_Number() {
        return Week_Number;
    }

    public void setWeek_Number(String week_Number) {
        Week_Number = week_Number;
    }

    public String getAvg_Score() {
        return Avg_Score;
    }

    public void setAvg_Score(String avg_Score) {
        Avg_Score = avg_Score;
    }

    public String getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(String monthNumber) {
        this.monthNumber = monthNumber;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getAvg_perc() {
        return avg_perc;
    }

    public void setAvg_perc(String avg_perc) {
        this.avg_perc = avg_perc;
    }
}
