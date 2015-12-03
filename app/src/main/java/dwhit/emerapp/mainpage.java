package dwhit.emerapp;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class mainpage extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    ListView lv;
    private static final String[]states = {"OH","AL","AK","AS","AZ","AR","CA","CO","CT","DE","DC","FL","GA","GU","HI","ID","IL","IN","IA","KS","KY","LA","ME","MD","MH","MA","MI","FM","MN","MS","MO","MT","NE","NV","NH","NJ","NM","NY","NC","ND","MP","OH","OK","OR","PW","PA","PR","RI","SC","SD","TN","TX","UT","VT","VA","VI","WA","WV","WI","WY"};

    Map<String, String> hashMapstates = new HashMap<String, String>();

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double mLat;
    private double mLon;
    private LocationRequest mLocationRequest;

    private String mCurrentLocation = null;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // get last location
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation == null) {
            // request location updates
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            mLat = mLastLocation.getLatitude();
            mLon = mLastLocation.getLongitude();
            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(mLat, mLon, 1);
                if (addresses.size() > 0) {
                    mCurrentLocation = hashMapstates.get(addresses.get(0).getAdminArea());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    private void handleNewLocation(Location location) {
        if (location == null) {
            mCurrentLocation = null;
        } else {
            mLat = location.getLatitude();
            mLon = location.getLongitude();
            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(mLat, mLon, 1);
                if (addresses.size() > 0) {
                    mCurrentLocation = hashMapstates.get(addresses.get(0).getAdminArea());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((mainpage) getActivity()).onDialogDismissed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private int findState(String location) {
        int j = 0;
        for (int i = 0; i < states.length; i++) {
            if (location.equals(states[i])) {
                j = i;
                break;
            }
        }
        return j;
    }

    protected void buildApp() {
        List<String> test_list = new ArrayList<String>();
        test_list.add("Amber");
        test_list.add("Weather");

        /*NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.triangle);
        mBuilder.setContentTitle("Alert Hub");
        mBuilder.setContentText("Check New Alerts!");
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());*/

        final Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, states);

        spinner.setAdapter(spinnerAdapter);

        if (mCurrentLocation != null) {
            spinner.setSelection(findState(mCurrentLocation));
        }

        final ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, test_list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent = new Intent(mainpage.this, openDetailActivity.class);
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    String message = "amber_alert";
                    intent.putExtra("timeline", message);
                    intent.putExtra("state",String.valueOf(spinner.getSelectedItem()));
                    startActivity(intent);
                } else if(position == 1){
                    String message = "SimpleWeather"+spinner.getSelectedItem().toString();
                    intent.putExtra("timeline", message);
                    intent.putExtra("state","");
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

        hashMapstates.put("Alabama","AL");
        hashMapstates.put("Alaska","AK");
        hashMapstates.put("Alberta","AB");
        hashMapstates.put("American Samoa","AS");
        hashMapstates.put("Arizona","AZ");
        hashMapstates.put("Arkansas","AR");
        hashMapstates.put("Armed Forces (AE)","AE");
        hashMapstates.put("Armed Forces Americas","AA");
        hashMapstates.put("Armed Forces Pacific","AP");
        hashMapstates.put("British Columbia","BC");
        hashMapstates.put("California","CA");
        hashMapstates.put("Colorado","CO");
        hashMapstates.put("Connecticut","CT");
        hashMapstates.put("Delaware","DE");
        hashMapstates.put("District Of Columbia","DC");
        hashMapstates.put("Florida","FL");
        hashMapstates.put("Georgia","GA");
        hashMapstates.put("Guam","GU");
        hashMapstates.put("Hawaii","HI");
        hashMapstates.put("Idaho","ID");
        hashMapstates.put("Illinois","IL");
        hashMapstates.put("Indiana","IN");
        hashMapstates.put("Iowa","IA");
        hashMapstates.put("Kansas","KS");
        hashMapstates.put("Kentucky","KY");
        hashMapstates.put("Louisiana","LA");
        hashMapstates.put("Maine","ME");
        hashMapstates.put("Manitoba","MB");
        hashMapstates.put("Maryland","MD");
        hashMapstates.put("Massachusetts","MA");
        hashMapstates.put("Michigan","MI");
        hashMapstates.put("Minnesota","MN");
        hashMapstates.put("Mississippi","MS");
        hashMapstates.put("Missouri","MO");
        hashMapstates.put("Montana","MT");
        hashMapstates.put("Nebraska","NE");
        hashMapstates.put("Nevada","NV");
        hashMapstates.put("New Brunswick","NB");
        hashMapstates.put("New Hampshire","NH");
        hashMapstates.put("New Jersey","NJ");
        hashMapstates.put("New Mexico","NM");
        hashMapstates.put("New York","NY");
        hashMapstates.put("Newfoundland","NF");
        hashMapstates.put("North Carolina","NC");
        hashMapstates.put("North Dakota","ND");
        hashMapstates.put("Northwest Territories","NT");
        hashMapstates.put("Nova Scotia","NS");
        hashMapstates.put("Nunavut","NU");
        hashMapstates.put("Ohio","OH");
        hashMapstates.put("Oklahoma","OK");
        hashMapstates.put("Ontario","ON");
        hashMapstates.put("Oregon","OR");
        hashMapstates.put("Pennsylvania","PA");
        hashMapstates.put("Prince Edward Island","PE");
        hashMapstates.put("Puerto Rico","PR");
        hashMapstates.put("Quebec","PQ");
        hashMapstates.put("Rhode Island","RI");
        hashMapstates.put("Saskatchewan","SK");
        hashMapstates.put("South Carolina","SC");
        hashMapstates.put("South Dakota","SD");
        hashMapstates.put("Tennessee","TN");
        hashMapstates.put("Texas","TX");
        hashMapstates.put("Utah","UT");
        hashMapstates.put("Vermont","VT");
        hashMapstates.put("Virgin Islands","VI");
        hashMapstates.put("Virginia","VA");
        hashMapstates.put("Washington","WA");
        hashMapstates.put("West Virginia","WV");
        hashMapstates.put("Wisconsin","WI");
        hashMapstates.put("Wyoming","WY");
        hashMapstates.put("Yukon Territory","YT");

            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                    .setInterval(10000)
                    .setFastestInterval(1000);

            setContentView(R.layout.activity_mainpage);
            lv = (ListView) findViewById(R.id.alertListView);

            buildGoogleApiClient();

            buildApp();

            new DHSAlert().execute();

        }


    /* Uses AsyncTask to run the HTTP get request on a background thread.
     *
     * Retrieves the XML file from the Department of Homeland Security alerts page and parses the
     * info from this file to be used when displaying alerts.
     */
    private class DHSAlert extends AsyncTask<Void, Void, String[]>{
        @Override
        protected String[] doInBackground(Void... params) {
            String[] info = new String[3];
            try
            {
                android.os.Debug.waitForDebugger();
                URL url = new URL("http://www.dhs.gov/ntas/1.0/sample-feed.xml");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(connection.getInputStream());
                parseXML(in, info);
                in.close();
            } catch (IOException e){
                e.printStackTrace();
            }
            return info;
        }

        /*@Override
        protected void onPostExecute(String[] info){
            String[] alert = {"The Department of Homeland Security has issued a national security alert."};
        }*/
    }

    /* Given an input stream to the url for the DHS security alerts web page, parses the XML
     * file to get information about the alert such as type, summary, and details.
     */
    public void parseXML(InputStream in, String[] info){
        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactoryObject.newPullParser();
            parser.setInput(in, null);

            int event = parser.getEventType();
            while(event != XmlPullParser.END_DOCUMENT){

                String name = parser.getName();
                switch(event) {
                    case XmlPullParser.START_TAG:
                        if (name.equals("alert")){
                            info[0] = parser.getAttributeValue(null, "type");
                        }else if(name.equals("summary")) {
                            try {
                                info[1] = parser.nextText();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if(name.equals("details")){
                            try {
                                info[2] = parser.nextText().replaceAll("</?p>","");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
                try {
                    event = parser.next();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }
}
