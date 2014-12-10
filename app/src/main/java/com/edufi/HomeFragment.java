package com.edufi;

//import android.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.Context;
import android.widget.Toast;
//import android.app.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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

public class HomeFragment extends Fragment implements LocationListener{
    static LatLng currentlocation;
    private GoogleMap map;
    private ArrayList<Marker> markerArray = new ArrayList<Marker>();
    private ArrayList<Integer> tidArray = new ArrayList<Integer>();

    private Context context = null;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    Location location;  // location
    double latitude;    // latitude
    double longitude;   // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    Button btnShowLocation;

    ListView reviewsListView = null;
    Spinner subjectSpinner = null;

	public HomeFragment(){
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        context = this.getActivity();
        getLocation();
        View rootView;

        String userType = MainActivity.savedPreferences.getString(MainActivity.USER_TYPE, "");
        if (userType.equals("student")) {
            rootView = inflater.inflate(R.layout.fragment_student_home, container, false);

            subjectSpinner = (Spinner) rootView.findViewById(R.id.spinner);

            subjectSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    Log.i("Selected Subject", parentView.getItemAtPosition(position).toString());
                    String subject = parentView.getItemAtPosition(position).toString();
                    new GetTutorsTask().execute(subject);
//                    FragmentManager fm = getChildFragmentManager();
//                    SupportMapFragment mf = (SupportMapFragment) fm.findFragmentById(R.id.map);
//                    // Initially have the map hidden
//                    FragmentTransaction ft = fm.beginTransaction();
//                    ft.show(mf);
//                    ft.commit();
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

            FragmentManager fm = getChildFragmentManager();
            SupportMapFragment mf = (SupportMapFragment) fm.findFragmentById(R.id.map);
            // Initially have the map hidden
            FragmentTransaction ft = fm.beginTransaction();
//            ft.hide(mf);
//            ft.commit();

            map = mf.getMap();

            // Setting a custom info window adapter for the google map
            map.setInfoWindowAdapter(new InfoWindowAdapter() {

                // Use default InfoWindow frame
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                // Defines the contents of the InfoWindow
                @Override
                public View getInfoContents(Marker arg0) {

                    // Getting view from the layout file info_window_layout
                    View v = getActivity().getLayoutInflater().inflate(R.layout.map_marker_info, null);
                    //Log.i("View", v.toString());
                    // Getting the tutor's name
                    String tutorName = arg0.getTitle();

                    // Getting the tutor's rating
                    Double tutorRating = Double.valueOf(arg0.getSnippet());

                    // Getting reference to the TextView to set tutor's name
                    TextView tName = (TextView) v.findViewById(R.id.tName);

                    // Getting reference to the RatingBar to set tutor's rating
                    RatingBar tRating = (RatingBar) v.findViewById(R.id.tRating);

                    // Setting the name
                    tName.setText(tutorName);

                    // Setting the rating
                    Log.i("Rating",String.valueOf(tutorRating.floatValue()));
                    tRating.setRating(tutorRating.floatValue());

                    // Returning the view containing InfoWindow contents
                    return v;

                }
            });

            map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(Marker marker) {
                    AsyncTask at = new VisitProfileTask().execute(tidArray.get(markerArray.indexOf(marker)).toString());
                }
            });

            // check if GPS enabled
            if(canGetLocation()){
                double latitude = getLatitude();
                double longitude = getLongitude();
                currentlocation = new LatLng(latitude, longitude);

                // \n is for new line
                Toast.makeText(context, "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                map.setMyLocationEnabled(true);

//                Marker you = map.addMarker(new MarkerOptions()
//                        .position(currentlocation)
//                        .title("You")
//                        .snippet("You are here")
//                        .icon(BitmapDescriptorFactory
//                                .fromResource(R.drawable.ic_launcher)));

                // Move the camera instantly to hamburg with a zoom of 15.
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentlocation, 14));

                // Zoom in, animating the camera.
                map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
            } else{
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                showSettingsAlert();
            }
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

    private class UpdateLocationTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... params){
            postData(params[0], params[1]);
            return null;
        }
    }

    private class GetTutorsTask extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            // Clear Map of Tutors
            for(int i = 0; i < markerArray.size(); i++) {
                markerArray.get(i).remove();
            }
            markerArray.clear();
            tidArray.clear();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String userId = MainActivity.savedPreferences.getString(MainActivity.USER_ID, "");
                String link = "http://107.170.241.159/wesley/fetch_tutors.php?uid="+userId+"&subject="+arg0[0];
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

                for (int i = 0; i < jArray.length(); i++) {
                    // Add Location Markers
                    JSONObject json_data = jArray.getJSONObject(i);
                    LatLng tutorlocation = new LatLng(json_data.getDouble("latitude"), json_data.getDouble("longitude"));
                    Marker tutor = map.addMarker(new MarkerOptions()
                            .position(tutorlocation)
                            .title(json_data.getString("name"))
                            .snippet(json_data.getString("rating"))
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_launcher)));
                    markerArray.add(tutor);
                    tidArray.add(new Integer(json_data.getInt("id")));
                }
            }
            catch(JSONException e){
                Log.e("log_tag", "Error parsing data " + e.toString());
            }
        }
    }

    private class VisitProfileTask extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String link = "http://107.170.241.159/queenie/get.php?id="
                        + arg0[0] + "&type=" + "tutor";
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
            String firstName = "";
            String lastName = "";
            String emailAddress = "";
            String phoneNumber = "";
            String levelOfEducation = "";
            String hourlyRate = "";
            try {
                JSONArray jArray = new JSONArray(result);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    firstName = json_data.getString("firstName");
                    lastName = json_data.getString("lastName");
                    emailAddress = json_data.getString("emailAddress");
                    phoneNumber = json_data.getString("phoneNumber");
                    levelOfEducation = json_data.getString("levelOfEducation");
                    hourlyRate = json_data.getString("hourlyRate");
                }
            } catch (JSONException e) {
                Log.e("log_tag", "Error parsing data " + e.toString());
            }

            Intent intent = new Intent(getActivity(), ViewTutorProfileActivity.class);

            // Send with the intent as a bundle
            Bundle extras = new Bundle();
            extras.putString("FIRST_NAME", firstName);
            extras.putString("LAST_NAME", lastName);
            extras.putString("EMAIL_ADDRESS", emailAddress);
            extras.putString("PHONE_NUMBER", phoneNumber);
            extras.putString("LEVEL_OF_EDUCATION", levelOfEducation);
            extras.putString("HOURLY_RATE", hourlyRate);
            intent.putExtras(extras);

            startActivity(intent);
        }
    }

    public void postData(String latitude, String longitude)
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://107.170.241.159/wesley/update_location.php");

        try{
            String userId = MainActivity.savedPreferences.getString(MainActivity.USER_ID, "");
            String userType = MainActivity.savedPreferences.getString(MainActivity.USER_TYPE, "");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("id", userId));
            nameValuePairs.add(new BasicNameValuePair("userType", userType));
            nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
            nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
        }
        catch(Exception e)
        {
            Log.e("log_tag", "Error:  " + e.toString());
        }
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.e("log_tag", "isGPSEnabled " + isGPSEnabled);
            Log.e("log_tag", "isNetworkEnabled " + isNetworkEnabled);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Log.e("log_tag", "no network!");
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            new UpdateLocationTask().execute(String.valueOf(latitude), String.valueOf(longitude));
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                new UpdateLocationTask().execute(String.valueOf(latitude), String.valueOf(longitude));
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check if best network provider
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("GPS Settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to Settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(this);
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
            ratingView.setRating(values[position].getRating());
            ratingView.setIsIndicator(true);
            commentView.setText(values[position].getComment());

            return rowView;
        }
    }

}
