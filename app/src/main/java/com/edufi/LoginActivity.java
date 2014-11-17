package com.edufi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

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


public class LoginActivity extends Activity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /* Called when the user clicks the Log In button */
    public void login(View view) {
        intent = new Intent(this, MainActivity.class);
        // Collect data from inputs
        EditText username = (EditText) findViewById(R.id.inputUsername);
        EditText password = (EditText) findViewById(R.id.inputPassword);

        // Convert to strings
        String usernameString = username.getText().toString().trim();
        String passwordString = password.getText().toString().trim();

        // Check if all fields have been filled out
//        TODO check format types like phone number has dashes
        if (usernameString.trim().equals("")) {
            username.setError("Username is required!");
        } else if (passwordString.trim().equals("")) {
            password.setError("Password is required!");
        } else {
            // Insert the data into the database
            new SummaryAsyncTask().execute(usernameString, passwordString);


        }
    }

    /* Called when the user clicks the Sign Up button */
    public void signUpUser(View view) {
        // Redirect user to setup
        Intent intent = new Intent(this, SetupLoginActivity.class);
        startActivity(intent);
    }

    public void postData(String username, String password)
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://107.170.241.159/queenie/login.php");

        try {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("password", password));
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
                JSONArray jArray = new JSONArray(result);
                Log.e("log_result", "RESULT IS " + jArray);
                for(int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    // Store the id of the user
                    SharedPreferences.Editor editor = MainActivity.savedPreferences.edit();
                    editor.putString(MainActivity.USER_ID, json_data.getString("id"));
                    editor.putString(MainActivity.USER_TYPE, json_data.getString("type"));
                    // Mark that the user has logged in
                    editor.putBoolean(MainActivity.PREF_LOGGED_IN, false);
                    editor.commit();
//                    Log.e("log_tag", "ID IN LOGINACTIVITY:  " + json_data.getString("id"));
//                    Log.e("log_tag", "TYPE IN LOGINACTIVITY:  " + json_data.getString("type"));
                }

                startActivity(intent);
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
            postData(params[0], params[1]);
            return null;
        }
    }
}
