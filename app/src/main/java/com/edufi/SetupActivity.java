package com.edufi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class SetupActivity
        extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
    }

    /* Called when the user clicks the Tutor button */
    public void setupTutor(View view) {
        // Redirect user to tutor setup
        Intent intent = new Intent(this, SetupTutorActivity.class);
        startActivity(intent);
    }

    /* Called when the user clicks the Student button */
    public void setupStudent(View view) {
        // Redirect user to student setup
        Intent intent = new Intent(this, SetupStudentActivity.class);
        startActivity(intent);
    }
}
