package percept.myplan.POJO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by percept on 20/7/16.
 */

public class InspirationWiseStrategy {

    @SerializedName("id")
    private String StrategyId;
    @SerializedName("title")
    private String StrategyName;
    @SerializedName("description")
    private String StrategyDesc;

    public InspirationWiseStrategy(String strategyId, String strategyName, String strategyDesc) {
        StrategyId = strategyId;
        StrategyName = strategyName;
        StrategyDesc = strategyDesc;
    }

    public String getStrategyDesc() {
        return StrategyDesc;
    }

    public void setStrategyDesc(String strategyDesc) {
        StrategyDesc = strategyDesc;
    }

    public String getStrategyName() {
        return StrategyName;
    }

    public void setStrategyName(String strategyName) {
        StrategyName = strategyName;
    }

    public String getStrategyId() {
        return StrategyId;
    }

    public void setStrategyId(String strategyId) {
        StrategyId = strategyId;
    }


}
