package com.edufi;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.Context;
import android.app.*;

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
import java.util.ArrayList;

public class HomeFragment extends Fragment {
    ListView reviewsListView = null;
    Spinner subjectSpinner = null;
    Context context = null;

	public HomeFragment(){
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        context = this.getActivity();
        View rootView;

        String userType = MainActivity.savedPreferences.getString(MainActivity.USER_TYPE, "");
        if (userType.equals("student")) {
            rootView = inflater.inflate(R.layout.fragment_student_home, container, false);

            subjectSpinner = (Spinner) rootView.findViewById(R.id.spinner);

            subjectSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    Log.i("Selected Subject", parentView.getItemAtPosition(position).toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                    Log.i("Nothing Selected", "wut");
                }

            });

            Log.i("Debug", "Event listener attached?");

            // Add Subjects to spinner
            AsyncTask at = new GetSubjectsTask().execute("");

        }
        else if (userType.equals("tutor")) {
            rootView = inflater.inflate(R.layout.fragment_tutor_home, container, false);

            reviewsListView = (ListView) rootView.findViewById(R.id.tutorReviews);

            // Fill in the Reviews List
            AsyncTask at = new GetReviewsTask().execute("");
        }
        else {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        }
         
        return rootView;
    }

    private class GetReviewsTask extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String userId = MainActivity.savedPreferences.getString(MainActivity.USER_ID, "");
                String link = "http://107.170.241.159/wesley/fetch_reviews.php?tutor_id="
                        + userId;
                // Log.i("Fetch Link", link);
                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));

                HttpResponse response = client.execute(request);
                // Log.i("Response Status", response.getStatusLine().toString());

                BufferedReader in = new BufferedReader
                        (new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();
                // Log.i("Complete Response", sb.toString());
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                JSONArray jArray = new JSONArray(result);
                Review[] reviews;
                if (jArray.length() > 0) {
                    reviews = new Review[jArray.length()];

                    for (int i = 0; i < jArray.length(); i++) {
                        // Set review data
                        JSONObject json_data = jArray.getJSONObject(i);
                        String name = json_data.getString("name");
                        int rating = json_data.getInt("rating");
                        String comment = json_data.getString("comment");
                        Review review = new Review(name, rating, comment);

                        // Insert review into array
                        reviews[i] = review;
                    }
                }
                else {
                    reviews = new Review[1];
                    reviews[0] = new Review("No Reviews Available", 0, "");
                }

                ReviewArrayAdapter adapter = new ReviewArrayAdapter(context, reviews);
                reviewsListView.setAdapter(adapter);
            }
            catch(JSONException e){
                Log.e("log_tag", "Error parsing data " + e.toString());
            }
        }
    }

    private class GetSubjectsTask extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String userId = MainActivity.savedPreferences.getString(MainActivity.USER_ID, "");
                String link = "http://107.170.241.159/wesley/fetch_subjects.php";
                Log.i("Link", link);
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
            try{
                JSONArray jArray = new JSONArray(result);
                ArrayList<String> subjectArray = new ArrayList<String>();

                for (int i = 0; i < jArray.length(); i++) {
                    // Set review data
                    JSONObject json_data = jArray.getJSONObject(i);
                    subjectArray.add(json_data.getString("subject"));
                }

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, subjectArray); //selected item will look like a spinner set from XML
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subjectSpinner.setAdapter(spinnerArrayAdapter);
            }
            catch(JSONException e){
                Log.e("log_tag", "Error parsing data " + e.toString());
            }
        }
    }

    private class Review {
        private String name;
        private int rating;
        private String comment;

        public Review (String name, int rating, String comment) {
            this.name = name;
            this.rating = rating;
            this.comment = comment;
        }

        public String getName() {
            return name;
        }

        public int getRating() {
            return rating;
        }

        public String getComment() {
            return comment;
        }
    }

    private class ReviewArrayAdapter extends ArrayAdapter<Review> {
        private final Context context;
        private final Review[] values;

        public ReviewArrayAdapter(Context context, Review[] values) {
            super(context, R.layout.review_list_item, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.review_list_item, parent, false);
            TextView raterNameView = (TextView) rowView.findViewById(R.id.raterName);
            RatingBar ratingView = (RatingBar) rowView.findViewById(R.id.ratingBar);
            TextView commentView = (TextView) rowView.findViewById(R.id.comment);

            raterNameView.setText(values[position].getName());
            ratingView.setNumStars(5);
            ratingView.setRating(values[position].getRating());
            commentView.setText(values[position].getComment());

            return rowView;
        }
    }

}
