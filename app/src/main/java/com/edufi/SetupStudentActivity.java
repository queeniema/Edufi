package com.edufi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;



public class SetupStudentActivity extends Activity {
    public final static String EXTRA_MESSAGE = "com.edufi";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_student);

        // Set up year in school spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinnerYearInSchool);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.year_in_school_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
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

    /* Called when the user clicks the Submit button */
    public void submit(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        // Collect data from inputs
        EditText firstName = (EditText) findViewById(R.id.inputFirstName);
        EditText lastName = (EditText) findViewById(R.id.inputLastName);
        EditText emailAddress = (EditText) findViewById(R.id.inputEmailAddress);
        EditText phoneNumber = (EditText) findViewById(R.id.inputPhoneNumber);
        final Spinner yearInSchoolSpinner = (Spinner) findViewById(R.id.spinnerYearInSchool);

        // Convert to strings
        String firstNameString = firstName.getText().toString();
        String lastNameString = lastName.getText().toString();
        String emailAddressString = emailAddress.getText().toString();
        String phoneNumberString = phoneNumber.getText().toString();
        String yearInSchoolString = yearInSchoolSpinner.getSelectedItem().toString();

        // Check if all fields have been filled out
//        TODO check format types like phone number has dashes
        if (firstNameString.trim().equals("")) {
            firstName.setError("First name is required!");
        } else if (lastNameString.trim().equals("")) {
            lastName.setError("Last name is required!");
        } else if (emailAddressString.trim().equals("")) {
            emailAddress.setError("Email address is required!");
        } else if (phoneNumberString.trim().equals("")) {
            phoneNumber.setError("Phone number is required!");
        } else {
//            // Send with the intent as a bundle
//            Bundle extras = new Bundle();
//            extras.putString(EXTRA_MESSAGE + ".FIRST_NAME", firstNameString);
//            extras.putString(EXTRA_MESSAGE + ".LAST_NAME", lastNameString);
//            extras.putString(EXTRA_MESSAGE + ".EMAIL_ADDRESS", emailAddressString);
//            extras.putString(EXTRA_MESSAGE + ".PHONE_NUMBER", phoneNumberString);
//            extras.putString(EXTRA_MESSAGE + ".YEAR_IN_SCHOOL", yearInSchoolString);
//            intent.putExtras(extras);

            // Mark that the setup was completed
            SharedPreferences.Editor editor = MainActivity.savedPreferences.edit();
            editor.putString(MainActivity.USER_TYPE, "student");
            editor.putBoolean(MainActivity.PREF_SHOW_ON_APP_START, false);
            editor.commit();
            startActivity(intent);
        }
    }
}