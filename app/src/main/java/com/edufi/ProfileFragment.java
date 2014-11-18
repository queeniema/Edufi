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
    String firstName;
    String lastName;
    String emailAddress;
    String phoneNumber;
    // for tutors
    String levelOfEducation;
    String hourlyRate;
    // for students
    String yearInSchool;

    TextView nameTextView = null;
    TextView emailAddressTextView = null;
    TextView phoneNumberTextView = null;
    // for tutors
    TextView levelOfEducationTextView = null;
    TextView hourlyRateTextView = null;
    // for students
    TextView yearInSchoolTextView = null;

    AsyncTask at = new LongOperation().execute("");
    public ProfileFragment(){}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView;

        String userType = MainActivity.savedPreferences.getString(MainActivity.USER_TYPE, "");

        if (userType.equals("tutor")) {
            rootView = inflater.inflate (R.layout.activity_tutor_profile,container,false);
            nameTextView = (TextView) rootView.findViewById(R.id.name);
            emailAddressTextView = (TextView) rootView.findViewById(R.id.emailAddress);
            phoneNumberTextView = (TextView) rootView.findViewById(R.id.phoneNumber);
            levelOfEducationTextView = (TextView) rootView.findViewById(R.id.levelOfEducation);
            hourlyRateTextView = (TextView) rootView.findViewById(R.id.hourlyRate);
        }
      else {
            rootView = inflater.inflate (R.layout.activity_student_profile,container,false);
            nameTextView = (TextView) rootView.findViewById(R.id.name);
            emailAddressTextView = (TextView) rootView.findViewById(R.id.emailAddress);
            phoneNumberTextView = (TextView) rootView.findViewById(R.id.phoneNumber);
            yearInSchoolTextView = (TextView) rootView.findViewById(R.id.yearInSchool);
        }

        AsyncTask at = new LongOperation().execute("");
        return rootView;
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        String userId;
        String userType;
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                userId = MainActivity.savedPreferences.getString(MainActivity.USER_ID, "");
                userType = MainActivity.savedPreferences.getString(MainActivity.USER_TYPE, "");

//                Log.e("log_result", "ID IN PROFILE IS " + userId);
                String link = "http://107.170.241.159/queenie/get.php?id="
                        + userId + "&type=" + userType;
//                Log.e("log_result", "USERTYPE IN PROFILE IS " + userType);
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
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                userId = MainActivity.savedPreferences.getString(MainActivity.USER_ID, "");
                userType = MainActivity.savedPreferences.getString(MainActivity.USER_TYPE, "");
                if (userType.equals("tutor")) {
                    JSONArray jArray = new JSONArray(result);
//                    Log.e("log_result", "RESULT IS " + jArray);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        firstName = json_data.getString("firstName");
                        lastName = json_data.getString("lastName");
                        emailAddress = json_data.getString("emailAddress");
                        phoneNumber = json_data.getString("phoneNumber");
//                        Log.e("log_result", "USERTYPE(!!!!) IN PROFILE IS " + userType);
                        levelOfEducation = json_data.getString("levelOfEducation");
                        hourlyRate = json_data.getString("hourlyRate");
                    }
                } else {
                    JSONArray jArray = new JSONArray(result);
                    Log.e("log_result", "RESULT IS " + jArray);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        firstName = json_data.getString("firstName");
                        lastName = json_data.getString("lastName");
                        emailAddress = json_data.getString("emailAddress");
                        phoneNumber = json_data.getString("phoneNumber");
//                        Log.e("log_result", "USERTYPE2(!!!!) IN PROFILE IS " + userType);
                        yearInSchool = json_data.getString("yearInSchool");
                    }
                }
            }
            catch(JSONException e){
                Log.e("log_tag", "Error parsing data " + e.toString());
            }
            nameTextView.setText(firstName+" "+lastName);
            emailAddressTextView.setText(emailAddress);
            phoneNumberTextView.setText(phoneNumber);
//            Log.e("log_result", "LEVEL IN PROFILE IS " + levelOfEducation);
//            Log.e("log_result", "HOURLY RATE IN PROFILE IS " + hourlyRate);
//            Log.e("log_result", "YEAR IN SCHOOL IN PROFILE IS " + yearInSchool);
            if (userType.equals("tutor")) {
                levelOfEducationTextView.setText(levelOfEducation);
                hourlyRateTextView.setText("$"+hourlyRate+"/hour");
            } else {
                yearInSchoolTextView.setText(yearInSchool);
            }

        }
    }

}