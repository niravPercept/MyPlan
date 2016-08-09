package percept.myplan.Global;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import percept.myplan.Activities.LoginActivity;
import percept.myplan.Activities.LoginActivity_1;
import percept.myplan.AppController;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.Contact;
import percept.myplan.R;

/**
 * Created by percept on 25/7/16.
 */

public class General {

    private String getServiceName(PHPServices serviceName) {
        if (serviceName == PHPServices.LOGIN) {
            return ".login";
        } else if (serviceName == PHPServices.REGISTER) {
            return ".register";
        } else if (serviceName == PHPServices.GET_SYMPTOMS) {
            return ".getsymptoms";
        } else if (serviceName == PHPServices.GET_SYMPTOM) {
            return ".getsymptom";
        } else if (serviceName == PHPServices.GET_STRATEGIES) {
            return ".getStrategies";
        } else if (serviceName == PHPServices.GET_CONTACTS) {
            return ".getContacts";
        } else if (serviceName == PHPServices.GET_CONTACT) {
            return ".getContact";
        } else if (serviceName == PHPServices.GET_SIDASTEST) {
            return "";
        } else if (serviceName == PHPServices.GET_SIDASCALENDER) {
            return "";
        } else if (serviceName == PHPServices.GET_MOODCALENDER) {
            return "";
        } else if (serviceName == PHPServices.GET_HOPEBOXES) {
            return ".getHopeboxes";
        } else if (serviceName == PHPServices.GET_HOPEBOX) {
            return ".getHopeboxe";
        } else if (serviceName == PHPServices.SAVE_SYMPTOM) {
            return ".saveSymptom";
        } else if (serviceName == PHPServices.DELETE_CONTACT) {
            return ".deleteContact";
        } else if (serviceName == PHPServices.GET_CATEGORIES) {
            return ".getCategories";
        } else if (serviceName == PHPServices.GET_CATEGORY_INSPIRATIONS) {
            return ".getCategoryInspirations";
        } else if (serviceName == PHPServices.GET_STRATEGY) {
            return ".getStrategy";
        } else if (serviceName == PHPServices.ADD_MYSTRATEGY) {
            return ".addMystrategy";
        } else if (serviceName == PHPServices.ADD_HOPEBOX) {
            return ".saveHopebox";
        } else if(serviceName==PHPServices.CHECK_LOGIN){
            return ".checkLoginSession";
        }else if(serviceName==PHPServices.SAVE_PROFILE){
            return ".saveProfile";
        }


        return "";
    }


    public String getJSONContentFromInternetService(final Context context, PHPServices servicesName,
                                                    Map<String, String> params,
                                                    Boolean checkInternetConnectivity,
                                                    Boolean encryptedDataTransfer, Boolean forceNetwork,
                                                    final VolleyResponseListener volleyResponseListener)
            throws Exception {


        // CHeck internet connection
        if (checkInternetConnectivity == true && !checkInternetConnection(context)) {
            throw new Exception(context.getResources().getString(R.string.err_network_not_available));
        }
        String str = context.getResources().getString(R.string.server_url);
        String _str = str + getServiceName(servicesName);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                _str, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(":::::::::::: ", response.toString());

                        try {
                            if (response.getJSONObject("data").has("message")) {
                                if (response.getJSONObject("data").get("message").equals("Your session is expired.")) {
                                    //Uncomment call for Session code.
                                    Intent intent = new Intent(context.getApplicationContext(), LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                    Constant.CURRENT_FRAGMENT = 0;
                                    Utils _utils = new Utils(context);
                                    _utils.setPreference(Constant.PREF_LOGGEDIN, "false");
                                    _utils.setPreference(Constant.PREF_SID, "");
                                    _utils.setPreference(Constant.PREF_SNAME, "");
                                    _utils.setPreference(Constant.PREF_PROFILE_IMG_LINK, "");
                                    _utils.setPreference(Constant.PREF_PROFILE_USER_NAME, "");
                                    _utils.setPreference(Constant.PREF_PROFILE_EMAIL, "");
                                    _utils.setPreference(Constant.PREF_PROFILE_NAME, "");
                                    ((AppCompatActivity) context).finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        volleyResponseListener.onResponse(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("::::::::::::: ", "Error: " + error.getMessage());
                volleyResponseListener.onError(error);

            }

        });

        forceNetwork = true;
        if (forceNetwork) {
            AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
        } else {
            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(_str);
            if (entry != null) {
                try {
                    String data = new String(entry.data, "UTF-8");
                    // handle data, like converting it to xml, json, bitmap etc.,
                    JSONObject _obj = new JSONObject(data);
                    Log.d("::: FROM ", "CACHE");
                    volleyResponseListener.onResponse(_obj);
                    AppController.getInstance().getRequestQueue().getCache().invalidate(_str, true);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                // Cached response doesn't exists. Make network call here
                Log.d("::: CALLED JSON", "NETWORK");
                AppController.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
            }
        }
//        AppController.getInstance().getRequestQueue().getCache().invalidate(_str, true);

        return "";
    }


    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager _connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean _isConnected = false;
        NetworkInfo _activeNetwork = _connManager.getActiveNetworkInfo();
        if (_activeNetwork != null) {
            _isConnected = _activeNetwork.isConnectedOrConnecting();
        }

        return _isConnected;
    }


    public enum PHPServices {
        LOGIN,
        REGISTER,
        GET_SYMPTOMS,
        GET_SYMPTOM,
        GET_STRATEGIES,
        GET_CONTACTS,
        GET_CONTACT,
        GET_SIDASTEST,
        GET_SIDASCALENDER,
        GET_MOODCALENDER,
        GET_HOPEBOXES,
        GET_HOPEBOX,
        SAVE_SYMPTOM,
        DELETE_CONTACT,
        GET_CATEGORIES,
        GET_CATEGORY_INSPIRATIONS,
        GET_STRATEGY,
        ADD_MYSTRATEGY,
        ADD_HOPEBOX,
        CHECK_LOGIN,
        SAVE_PROFILE
    }
}
