package com.edufi;

import android.app.Activity;
import android.content.Intent;
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

import java.util.ArrayList;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /* Called when the user clicks the Log In button */
    public void login(View view) {
        Intent intent = new Intent(this, MainActivity.class);
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

            // Mark that the user has logged in
            MainActivity.savedPreferences.edit().putBoolean(MainActivity.PREF_LOGGED_IN, false).commit();
            startActivity(intent);
        }
    }

    /* Called when the user clicks the Sign Up button */
    public void signUpUser(View view) {
        // Redirect user to setup
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
    }

    public void postData(String username, String password)
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://107.170.241.159/queenie/insert.php");

        try {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("type", "login"));
            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("password", password));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
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
