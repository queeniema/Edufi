package com.edufi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewTutorProfileActivity extends Activity {
    final Context context = this;

    private List<String> subjects = new ArrayList<String>();

    TextView nameTextView = null;
    TextView emailAddressTextView = null;
    TextView phoneNumberTextView = null;
    TextView levelOfEducationTextView = null;
    TextView hourlyRateTextView = null;

    String tutorId;

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
            tutorId = extras.getString("TUTOR_ID");
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

    /* Called when a student clicks the "Tutor me!" button */
    public void sendNotification(View v) {
        // Display toast notifying student that their tutoring request has been sent to the tutor
        Toast toast;
        toast = Toast.makeText(getApplicationContext(), "Your request has been sent!", Toast.LENGTH_SHORT);
        if(toast != null) {
            toast.show();
        }
    }

    /* Called when a student clicks the "Leave a review" button */
    public void openReviewPopup(View v) {
//        FireMissilesDialogFragment fire_frag = new FireMissilesDialogFragment();
//        fire_frag.show(getFragmentManager(), );

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        final View dialogView = layoutInflater.inflate(R.layout.dialog_submit_review, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set dialog_submit_review.xml to be the layout file of the alertdialog builder
        alertDialogBuilder.setView(dialogView);

//        final EditText input = (EditText) promptView.findViewById(R.id.userInput);

        final RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.rating_bar);
        final EditText comment = (EditText) dialogView.findViewById(R.id.review_comment);


        // setup a dialog window
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get rating and comment
                        int reviewRating = (int) ratingBar.getRating();
                        String reviewComment = comment.getText().toString();
//                        Log.e("DEBUG_REVIEW", Integer.toString(reviewRating));
//                        Log.e("DEBUG_REVIEW", comment.getText().toString());

                        // insert the data into the database
                        String sid = MainActivity.savedPreferences.getString(MainActivity.USER_ID, "");
                        new SummaryAsyncTask().execute(sid, tutorId, Integer.toString(reviewRating), reviewComment);

                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,	int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.setTitle("Leave a review");
        alertD.show();
    }

    public void postData(String sid, String tid, String rating, String comment)
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://107.170.241.159/queenie/insert.php");

        try{
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("type", "review"));
            nameValuePairs.add(new BasicNameValuePair("sid", sid));
            nameValuePairs.add(new BasicNameValuePair("tid", tid));
            nameValuePairs.add(new BasicNameValuePair("rating", rating));
            nameValuePairs.add(new BasicNameValuePair("comment", comment));
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
            postData(params[0], params[1], params[2], params[3]);
            return null;
        }
    }
}

