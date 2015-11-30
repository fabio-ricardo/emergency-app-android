package dwhit.emerapp;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

public class openDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_open_detail);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText("Twitter Source: @"+getIntent().getExtras().getString("timeline"));
         setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            ListView lv = (ListView) findViewById(R.id.listView);
            List<String> tweets = new ArrayList<String>();
            TwitterTimeline getTweets = new TwitterTimeline(getIntent().getExtras().getString("timeline"),getIntent().getExtras().getString("state") );
            tweets = getTweets.GetTimeline();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1,tweets);
            lv.setAdapter(adapter);



        //setContentView(lv);

    }


}
