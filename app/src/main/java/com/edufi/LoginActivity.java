package com.edufi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /* Called when the user clicks the Log In button */
    public void login(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        // Mark that the user has logged in
        MainActivity.savedPreferences.edit().putBoolean(MainActivity.PREF_LOGGED_IN, false).commit();
        startActivity(intent);
    }

    /* Called when the user clicks the Sign Up button */
    public void signUpUser(View view) {
        // Redirect user to setup
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
    }
}
