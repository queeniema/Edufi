package com.edufi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ViewTutorProfileActivity extends Activity {
    private List<String> subjects = new ArrayList<String>();

    TextView nameTextView = null;
    TextView emailAddressTextView = null;
    TextView phoneNumberTextView = null;
    TextView levelOfEducationTextView = null;
    TextView hourlyRateTextView = null;

    String firstName;
    String lastName;
    String emailAddress;
    String phoneNumber;
    String levelOfEducation;
    String hourlyRate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_tutor_profile);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            firstName = extras.getString("FIRST_NAME");
            lastName = extras.getString("LAST_NAME");
            emailAddress = extras.getString("EMAIL_ADDRESS");
            phoneNumber = extras.getString("PHONE_NUMBER");
            levelOfEducation = extras.getString("LEVEL_OF_EDUCATION");
            hourlyRate = extras.getString("HOURLY_RATE");
        }

        nameTextView = (TextView) findViewById(R.id.name);
        emailAddressTextView = (TextView) findViewById(R.id.emailAddress);
        phoneNumberTextView = (TextView) findViewById(R.id.phoneNumber);
        levelOfEducationTextView = (TextView) findViewById(R.id.levelOfEducation);
        hourlyRateTextView = (TextView) findViewById(R.id.hourlyRate);

        nameTextView.setText(firstName+" "+lastName);
        emailAddressTextView.setText(emailAddress);
        phoneNumberTextView.setText(phoneNumber);
        levelOfEducationTextView.setText(levelOfEducation);
        hourlyRateTextView.setText("$"+hourlyRate+"/hour");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendNotification(View v) {


        // Display toast notifying student that their tutoring request has been sent to the tutor
        Toast toast;
        toast = Toast.makeText(getApplicationContext(), "Your request has been sent!", Toast.LENGTH_SHORT);
        if(toast != null) {
            toast.show();
        }
    }
}