package percept.myplan.Global;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import percept.myplan.Activities.AddVideoActivity;
import percept.myplan.Interfaces.AsyncTaskCompletedListener;
import percept.myplan.R;

import static android.R.id.progress;

/**
 * Created by percept on 16/8/16.
 */
public class MultiPartParsing {

    private Context context;
    private HashMap<String, String> map;
    private AsyncTaskCompletedListener completedListener;
    private General.PHPServices servicesName;
//    private AddVideoActivity.MPROGRESSDIALOG mprogressdialog;

    public MultiPartParsing(Context _context, HashMap<String, String> _map,
                            General.PHPServices servicesName, AsyncTaskCompletedListener _completedListener) {
        this.context = _context;
        this.map = _map;
        this.completedListener = _completedListener;
        this.servicesName = servicesName;
        new MultiPartAsyncTask().execute();
    }

    class MultiPartAsyncTask extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//        AddVideoActivity.MPROGRESSDIALOG.setMessage("file upload ........chga");
        }

        @Override
        protected String doInBackground(Void... voids) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

            DefaultHttpClient client = new DefaultHttpClient();

            SchemeRegistry registry = new SchemeRegistry();
            SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
            socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
            registry.register(new Scheme("https", socketFactory, 443));
            SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
            HttpClient httpclient = new DefaultHttpClient(mgr, client.getParams());

//            HttpClient httpclient = new DefaultHttpClient();
            String _strURL = context.getResources().getString(R.string.server_url) + new General().getServiceName(servicesName);
            HttpPost httppost = new HttpPost(_strURL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
//                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

//                if (!FILE_PATH.equals("")) {
//                    File sourceFile = new File(FILE_PATH);

                // Adding file data to http body
                Log.d("::::::Params ", new JSONObject(map).toString());
                for (String key : map.keySet()) {

                    if (key.contains("image") || key.contains("video")|| key.contains("cover") || key.contains("media")) {
                        if (key.equals("media_title")) {
                            entity.addPart(key, new StringBody(map.get(key)));
                        } else if (Pattern.matches("[0-9]+", map.get(key)))
                            entity.addPart(key, new StringBody(map.get(key)));
                        else if (!TextUtils.isEmpty(map.get(key))) {
                            try {
                                File _f = new File(map.get(key));
                                if (_f.exists()) {
                                    entity.addPart(key, new FileBody(_f));

                                   /* byte[] bytes = new byte[(int) _f.length()];
                                    int bufferLength = 1024;
                                    for (int i = 0; i < bytes.length; i += bufferLength) {
                                        int progress = (int) ((i / (float) bytes.length) * 100);
                                        if (AddVideoActivity.MPROGRESSDIALOG!= null) {
                                            AddVideoActivity.MPROGRESSDIALOG.setProgress(progress);
                                        }
                                    }*/
                                } else
                                    entity.addPart(key, new StringBody(map.get(key)));
                            } catch (Exception e) {
                                entity.addPart(key, new StringBody(map.get(key)));
                            }
                        }
                    } else

                        entity.addPart(key, new StringBody(map.get(key), Charset.forName("UTF-8")));
                    entity.getContentEncoding();
                }


//                totalSize = entity.getContentLength();
                httppost.setEntity(entity);
                long totalLength = entity.getContentLength();
                System.out.println("TotalLength : " + totalLength);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);

                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }




        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.d(":::::::::", response);
            if (completedListener != null)
                completedListener.onTaskCompleted(response);
        }
    }
}
