package com.edufi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

public class TutorMeFragment extends Fragment implements View.OnClickListener {
    String firstName;
    String lastName;
    String emailAddress;
    String phoneNumber;
    String levelOfEducation;
    String hourlyRate;

    String tutorId;

    public TutorMeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;

        rootView = inflater.inflate(R.layout.fragment_tutor_me, container, false);

        Button button_queenie = (Button) rootView.findViewById(R.id.queenie);
        button_queenie.setOnClickListener(this);
        Button button_wesley = (Button) rootView.findViewById(R.id.wesley);
        button_wesley.setOnClickListener(this);
        Button button_jane = (Button) rootView.findViewById(R.id.jane);
        button_jane.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.queenie:
                tutorId = "7";     // hard-coded for now
                break;
            case R.id.wesley:
                tutorId = "9";     // hard-coded for now
                break;
            case R.id.jane:
                tutorId = "36";     // hard-coded for now
                break;
        }

        // Retrieve the profile info
        AsyncTask at = new LongOperation().execute("");
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String link = "http://107.170.241.159/queenie/get.php?id="
                        + tutorId + "&type=" + "tutor";
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
                JSONArray jArray = new JSONArray(result);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    firstName = json_data.getString("firstName");
                    lastName = json_data.getString("lastName");
                    emailAddress = json_data.getString("emailAddress");
                    phoneNumber = json_data.getString("phoneNumber");
                    levelOfEducation = json_data.getString("levelOfEducation");
                    hourlyRate = json_data.getString("hourlyRate");
                }
            } catch (JSONException e) {
                Log.e("log_tag", "Error parsing data " + e.toString());
            }

            Intent intent = new Intent(getActivity(), ViewTutorProfileActivity.class);

            // Send with the intent as a bundle
            Bundle extras = new Bundle();
            extras.putString("FIRST_NAME", firstName);
            extras.putString("LAST_NAME", lastName);
            extras.putString("EMAIL_ADDRESS", emailAddress);
            extras.putString("PHONE_NUMBER", phoneNumber);
            extras.putString("LEVEL_OF_EDUCATION", levelOfEducation);
            extras.putString("HOURLY_RATE", hourlyRate);
            intent.putExtras(extras);

            startActivity(intent);
        }
    }
}