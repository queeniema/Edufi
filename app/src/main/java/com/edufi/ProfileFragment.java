package com.edufi;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ProfileFragment extends Fragment {
    private int byGetOrPost = 0;
    String firstName;
    String lastName;
    String emailAddress;
    String phoneNumber;
    String levelOfEducation;
    String hourlyRate;
    TextView nameTextView = null;
    TextView emailAddressTextView = null;
    TextView phoneNumberTextView = null;
    TextView levelOfEducationTextView = null;
    TextView hourlyRateTextView = null;
    AsyncTask at = new LongOperation().execute("");
    public ProfileFragment(){}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.fragment_profile,container,false);

        nameTextView = (TextView) rootView.findViewById(R.id.name);
//        newTextView.setText("FIRST"+" "+"LAST");
        emailAddressTextView = (TextView) rootView.findViewById(R.id.emailAddress);
//        newTextView.setText("TEST@GMAIL.COM");
        phoneNumberTextView = (TextView) rootView.findViewById(R.id.phoneNumber);
//        newTextView.setText("123-111-333");
        levelOfEducationTextView = (TextView) rootView.findViewById(R.id.levelOfEducation);
//        newTextView.setText(getString(R.string.level_of_education)+"\n"+"High School");
        hourlyRateTextView = (TextView) rootView.findViewById(R.id.hourlyRate);
//        newTextView.setText("$"+"20.00"+"/hour");
        AsyncTask at = new LongOperation().execute("");
        return rootView;
    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg0) {
            if (byGetOrPost == 0) { //means by Get Method
                try {
                    String link = "http://107.170.241.159/sql.php?firstname="
                            + "Barack" + "&lastname=" + "Obama";
                    URL url = new URL(link);
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(link));
                    HttpResponse response = client.execute(request);
                    BufferedReader in = new BufferedReader
                            (new InputStreamReader(response.getEntity().getContent()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            } else {
                try {
                    String username = (String) arg0[0];
                    String password = (String) arg0[1];
                    String link = "http://myphpmysqlweb.hostei.com/loginpost.php";
                    String data = URLEncoder.encode("username", "UTF-8")
                            + "=" + URLEncoder.encode(username, "UTF-8");
                    data += "&" + URLEncoder.encode("password", "UTF-8")
                            + "=" + URLEncoder.encode(password, "UTF-8");
                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter
                            (conn.getOutputStream());
                    wr.write(data);
                    wr.flush();
                    BufferedReader reader = new BufferedReader
                            (new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                JSONArray jArray = new JSONArray(result);
                for(int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    firstName = json_data.getString("firstName");
                    lastName = json_data.getString("lastName");
                    emailAddress = json_data.getString("emailAddress");
                    phoneNumber = json_data.getString("phoneNumber");
                    levelOfEducation = json_data.getString("levelOfEducation");
                    hourlyRate = json_data.getString("hourlyRate");
                }
            }
            catch(JSONException e){
                Log.e("log_tag", "Error parsing data " + e.toString());
            }
            nameTextView.setText(firstName+" "+lastName);
            emailAddressTextView.setText(emailAddress);
            phoneNumberTextView.setText(phoneNumber);
            levelOfEducationTextView.setText(levelOfEducation);
            hourlyRateTextView.setText(hourlyRate);
        }
    }

}