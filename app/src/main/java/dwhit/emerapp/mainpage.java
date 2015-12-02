package dwhit.emerapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

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
import java.util.Map;


public class mainpage extends AppCompatActivity{

    ListView lv;
    private static final String[]states = {"AL","AK","AS","AZ","AR","CA","CO","CT","DE","DC","FL","GA","GU","HI","ID","IL","IN","IA","KS","KY","LA","ME","MD","MH","MA","MI","FM","MN","MS","MO","MT","NE","NV","NH","NJ","NM","NY","NC","ND","MP","OH","OK","OR","PW","PA","PR","RI","SC","SD","TN","TX","UT","VT","VA","VI","WA","WV","WI","WY"};

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
                    intent.putExtra("state", " ");
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_mainpage);
            lv = (ListView) findViewById(R.id.alertListView);

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
