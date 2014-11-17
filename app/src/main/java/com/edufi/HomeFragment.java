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
import android.content.Context;

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
    View reviewView = null;
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

        }
        else if (userType.equals("tutor")) {
            rootView = inflater.inflate(R.layout.fragment_tutor_home, container, false);

            reviewsListView = (ListView) rootView.findViewById(R.id.tutorReviews);

//            Review r1 = new Review("Wesley Situ", 5, "Very Good");
//            Review r2 = new Review("Queenie Ma", 4, "Good");
//            Review[] reviews = new Review[2];
//            reviews[0] = r1;
//            reviews[1] = r2;
//
//            Log.i("Context", context.toString());
//            ReviewArrayAdapter adapter = new ReviewArrayAdapter(context, reviews);
//            reviewsListView.setAdapter(adapter);

            // Fill in the Reviews List
            AsyncTask at = new LongOperation().execute("");
        }
        else {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        }
         
        return rootView;
    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String link = "http://107.170.241.159/wesley/fetch_reviews.php?tutor_id="
                        + "1";
                Log.i("Fetch Link", link);
                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));

                HttpResponse response = client.execute(request);
                Log.i("Response Status", response.getStatusLine().toString());

                BufferedReader in = new BufferedReader
                        (new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();
                Log.i("Complete Response", sb.toString());
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                JSONArray jArray = new JSONArray(result);
                Review[] reviews = new Review[jArray.length()];

                for(int i = 0; i < jArray.length(); i++) {
                    // Set review data
                    JSONObject json_data = jArray.getJSONObject(i);
                    String name = json_data.getString("name");
                    int rating = json_data.getInt("rating");
                    String comment = json_data.getString("comment");
                    Review review = new Review(name, rating, comment);

                    // Insert review into array
                    reviews[i] = review;
                }

                ReviewArrayAdapter adapter = new ReviewArrayAdapter(context, reviews);
                reviewsListView.setAdapter(adapter);
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
