package dwhit.emerapp;

import android.os.AsyncTask;
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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.net.HttpURLConnection;
import java.util.List;


public class mainpage extends AppCompatActivity {

    ListView lv;
    String type = "", summary = "", details = "";
    public final static String TYPE = "type", SUMMARY = "summary", DETAILS = "details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        lv = (ListView) findViewById(R.id.alertListView);

        new DHSAlert().execute();

        List<String> test_list = new ArrayList<String>();
        test_list.add("Amber Alerts");
        test_list.add("Weather Alerts");
        test_list.add("Presidential Alerts");

        final ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, test_list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent = new Intent(mainpage.this, openDetailActivity.class);
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    String message = "ohioamberalert";
                    intent.putExtra("timeline", message);
                    startActivity(intent);
                } else if (position == 1) {
                    String message = "swa_columbusoh";
                    intent.putExtra("timeline", message);
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent2 = new Intent(mainpage.this, dhsActivity.class);
                    intent2.putExtra(TYPE, type);
                    intent2.putExtra(SUMMARY, summary);
                    intent2.putExtra(DETAILS, details);
                    startActivity(intent2);
                }
            }
        });
    }

    /* Uses AsyncTask to run the HTTP get request on a background thread.
     *
     * Retrieves the XML file from the Department of Homeland Security alerts page and parses the
     * info from this file to be used when displaying alerts.
     */
    private class DHSAlert extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                URL url = new URL("http://www.dhs.gov/ntas/1.0/sample-feed.xml");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(connection.getInputStream());
                parseXML(in);
                in.close();
            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    /* Given an input stream to the url for the DHS security alerts web page, parses the XML
     * file to get information about the alert such as type, summary, and details.
     */
    public void parseXML(InputStream in){
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
                            type = parser.getAttributeValue(null, "type").toLowerCase();
                        }else if(name.equals("summary")) {
                            try {
                                summary = parser.nextText();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if(name.equals("details")){
                            try {
                                details = parser.nextText().replaceAll("</?p>","");
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
