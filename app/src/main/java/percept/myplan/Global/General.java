package percept.myplan.Global;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;

import percept.myplan.Activities.LoginActivity;
import percept.myplan.AppController;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.R;

import static percept.myplan.Global.Constant.removeAlarms;

/**
 * Created by percept on 25/7/16.
 */

public class General {

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager _connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean _isConnected = false;
        NetworkInfo _activeNetwork = _connManager.getActiveNetworkInfo();
        if (_activeNetwork != null) {
            _isConnected = _activeNetwork.isConnectedOrConnecting();
        }

        return _isConnected;
    }

    public String getServiceName(PHPServices serviceName) {
        switch (serviceName) {
            case LOGIN:
                return ".login";
            case REGISTER:
                return ".register";
            case GET_SYMPTOMS:
                return ".getsymptoms";
            case GET_SYMPTOM:
                return ".getsymptom";
            case GET_STRATEGIES:
                return ".getStrategies";
            case GET_CONTACTS:
                return ".getContacts";
            case GET_CONTACT:
                return ".getContact";
            case GET_MOODCALENDER:
                return ".getMoodCalendar";
            case GET_HOPEBOXES:
                return ".getHopeboxes";
            case GET_HOPEBOX:
                return ".getHopeboxe";
            case SAVE_SYMPTOM:
                return ".saveSymptom";
            case DELETE_CONTACT:
                return ".deleteContact";
            case GET_CATEGORIES:
                return ".getCategories";
            case GET_CATEGORY_INSPIRATIONS:
                return ".getCategoryInspirations";
            case GET_STRATEGY:
                return ".getStrategy";
            case ADD_MYSTRATEGY:
                return ".addMystrategy";
            case ADD_HOPEBOX:
                return ".saveHopebox";
            case CHECK_LOGIN:
                return ".checkLoginSession";
            case SAVE_PROFILE:
                return ".saveProfile";
            case ADD_MOOD:
                return ".submitMoodTest";
            case GET_SIDATEST:
                return ".getSIDASTest";
            case GET_SIDACALENDER:
                return ".getSIDASCalendar";
            case SUBMIT_SIDATEST:
                return ".submitSIDASTest";
            case SAVE_HOPE_MEDIA:
                return ".saveHopemedia";
            case SHARE_STRATEGIES:
                return ".shareStrategies";
            case SAVE_CONTACT:
                return ".saveContact";
            case SAVE_STRATEGY:
                return ".saveStrategy";
            case SAVE_CONTACTS:
                return ".saveContacts";
            case GET_USER_STRATEGY:
                return ".getUserstrategy";
            case PROFILE:
                return ".profile";
            case DELETE_HOPE_BOX:
                return ".deleteHopebox";
            case DELETE_HOPE_MEDIA:
                return ".deleteHopemedia";
            case FORGOT_PASSWORD:
                return ".forgotPassword";
            case GET_SETTINGS:
                return ".getSettings";
            case SAVE_SETTINGS:
                return ".SaveSettings";
            case GET_MESSAGE:
                return ".getMessage";
            case SAVE_MESSAGE:
                return ".SaveMessage";
            case GET_HELP_INFO:
                return ".getHelpinfo";
            case GIVE_FEEDBACK:
                return ".giveFeedback";
            case GET_EMERGENCY_ROOMS:
                return ".getEmergencyrooms";
            case SAVE_EMERGENCY_ROOM:
                return ".saveEmergencyroom";
            case DELETE_STRATEGY_IMAGES:
                return ".DeleteStrategyimages";
            case DELETE_SYMPTOM:
                return ".DeleteSymptom";
            case GET_EXPORT_PDF:
                return ".getExportpdf";
            case GET_PROFILE:
                return ".getprofile";
            case DELETE_PROFILEMAGE:
                return ".deleteProfilemage";
            case DELETE_STRATEGY_AUDIO:
                return ".DeleteStrategyaudio";
            case GETHELPINFOSYMPTOMS:
                return ".getHelpinfosymptoms";
            default:
                return "";
        }
    }

    public String getJSONContentFromInternetService(final Context context, final PHPServices servicesName,
                                                    Map<String, String> params,
                                                    Boolean checkInternetConnectivity,
                                                    Boolean encryptedDataTransfer, Boolean forceNetwork,
                                                    final VolleyResponseListener volleyResponseListener, final String storeId)
            throws Exception {


        // CHeck internet connection
        if (checkInternetConnectivity == true && !checkInternetConnection(context)) {
            Utils utils = new Utils(context);
            String offlineData = utils.getPreference(servicesName.name() + storeId);
            if (!offlineData.trim().equals("")) {
                JSONObject jsonObject = new JSONObject(offlineData);
                volleyResponseListener.onResponse(jsonObject);
            } else
                throw new Exception(context.getResources().getString(R.string.err_network_not_available));
        }
        String str = context.getResources().getString(R.string.server_url);
        String _str = str + getServiceName(servicesName);


        if (getServiceName(servicesName).equalsIgnoreCase(".getSIDASTest")) {
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("da")) {
                _str = _str + "&lang=da";
            }
        }
        if (getServiceName(servicesName).equalsIgnoreCase(".getUserstrategy")) {
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("da")) {
                _str = _str + "&lang=da";
            }
        }
        if (getServiceName(servicesName).equalsIgnoreCase(".getCategories")) {
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("da")) {
                _str = _str + "&lang=da";
            }
        }
        if (getServiceName(servicesName).equalsIgnoreCase(".getCategoryInspirations")) {
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("da")) {
                _str = _str + "&lang=da";
            }
        }
        if (getServiceName(servicesName).equalsIgnoreCase(".getEmergencyrooms")) {
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("da")) {
                _str = _str + "&lang=da";
            }
        }

        if (getServiceName(servicesName).equalsIgnoreCase(".getHelpinfo")) {
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("da")) {
                _str = _str + "&lang=da";
            }
        }
        if (getServiceName(servicesName).equalsIgnoreCase(".getHelpinfosymptoms")) {
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("da")) {
                _str = _str + "&lang=da";
            }
        }



        JSONObject parameters = new JSONObject(params);

        Log.d("::::::Params ", parameters.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                _str, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(":::::::::::: ", response.toString());
                        Utils utils = new Utils(context);
                        String str = servicesName.name();
                        utils.setPreference(servicesName.name() + storeId, response.toString());
                        try {
                            if (response.get("data") instanceof JSONObject && response.getJSONObject("data").has("message")) {
                                if (response.getJSONObject("data").get("message").equals("Your session is expired.")) {
                                    //Uncomment call for Session code.
                                    Intent removeAlarmIntent = new Intent("MyPlan.Remove.Alarm");
                                    context.sendBroadcast(removeAlarmIntent);
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
                                    _utils.setPreference(Constant.PREF_PROFILE_FNAME, "");
                                    _utils.setPreference(Constant.PREF_PROFILE_LNAME, "");
                                    _utils.setPreference(Constant.PREF_USER_ID, "");
                                    removeAlarms(context);

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

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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


    public enum PHPServices {
        LOGIN, REGISTER, GET_SYMPTOMS, GET_SYMPTOM, GET_STRATEGIES, GET_CONTACTS, GET_CONTACT, GET_SIDASCALENDER,
        GET_MOODCALENDER, GET_HOPEBOXES, GET_HOPEBOX, SAVE_SYMPTOM, DELETE_CONTACT, GET_CATEGORIES,
        GET_CATEGORY_INSPIRATIONS, GET_STRATEGY, ADD_MYSTRATEGY,
        ADD_HOPEBOX, CHECK_LOGIN, SAVE_PROFILE, ADD_MOOD, GET_SIDATEST,
        GET_SIDACALENDER, SUBMIT_SIDATEST, SHARE_STRATEGIES,
        SAVE_HOPE_MEDIA, SAVE_STRATEGY, SAVE_CONTACT, SAVE_CONTACTS,
        GET_USER_STRATEGY, PROFILE, DELETE_HOPE_BOX, FORGOT_PASSWORD,
        GET_SETTINGS, SAVE_SETTINGS, GET_MESSAGE, SAVE_MESSAGE, GET_HELP_INFO, GIVE_FEEDBACK,
        GET_EMERGENCY_ROOMS, SAVE_EMERGENCY_ROOM, DELETE_STRATEGY_IMAGES, DELETE_SYMPTOM, DELETE_HOPE_MEDIA,
        GET_EXPORT_PDF, GET_PROFILE, DELETE_PROFILEMAGE,DELETE_STRATEGY_AUDIO,GETHELPINFOSYMPTOMS,
    }
}
