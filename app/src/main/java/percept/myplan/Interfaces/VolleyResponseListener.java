package percept.myplan.Interfaces;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by percept on 25/7/16.
 */

public interface VolleyResponseListener {
    void onError(VolleyError message);

    void onResponse(JSONObject response);
}
