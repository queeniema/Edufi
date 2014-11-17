package com.edufi;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class SetupLoginActivity extends Activity {
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the message from the intent
        Intent intent = getIntent();
        // Set user type globally
        MainActivity.userType = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        setContentView(R.layout.activity_setup_login);
    }

    /* Called when the user clicks the Continue button */
    public void continueOn(View view) {
        Intent intent = new Intent(this, SetupActivity.class);

        // Collect data from inputs
        EditText username = (EditText) findViewById(R.id.inputUsername);
        EditText password = (EditText) findViewById(R.id.inputPassword);

        // Convert to strings
        String usernameString = username.getText().toString();
        String passwordString = password.getText().toString();

        // Check if all fields have been filled out
//        TODO check format types like phone number has dashes
        if (usernameString.trim().equals("")) {
            username.setError("Username is required!");
        } else if (passwordString.trim().equals("")) {
            password.setError("Password is required!");
        } else {
            // Insert the data into the database
            new SummaryAsyncTask().execute(usernameString, passwordString, MainActivity.userType);

            // Mark that the user has logged in
            MainActivity.savedPreferences.edit().putBoolean(MainActivity.PREF_LOGGED_IN, false).commit();
            startActivity(intent);
        }
    }

    public void postData(String username, String password, String userType)
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://107.170.241.159/queenie/insert.php");

        try {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("type", "login"));
            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("password", password));
            nameValuePairs.add(new BasicNameValuePair("usertype", userType));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            BufferedReader in = new BufferedReader
                    (new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            while ((line = in.readLine()) != null) {
                sb.append(line);
                break;
            }
            in.close();
            String result = sb.toString();
            try{
                JSONObject object = new JSONObject(result);
                // Store the id of the user
                MainActivity.id = object.getString("id");
                Log.e("log_tag", "ID IN SETUPLOGIN:  " + MainActivity.id);
            }
            catch(JSONException e){
                Log.e("log_tag", "Error parsing data " + e.toString());
            }
        }
        catch(Exception e)
        {
            Log.e("log_tag", "Error:  " + e.toString());
        }
    }

    private class SummaryAsyncTask extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... params){
            postData(params[0], params[1], params[2]);
            return null;
        }
    }
}
