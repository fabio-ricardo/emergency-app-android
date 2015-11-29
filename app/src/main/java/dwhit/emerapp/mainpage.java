package dwhit.emerapp;

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

import java.util.ArrayList;
import java.util.List;


public class mainpage extends AppCompatActivity {

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_mainpage);
            lv = (ListView) findViewById(R.id.alertListView);
            // TODO: get actual info

            List<String> test_list = new ArrayList<String>();
            test_list.add("Amber Alerts");
            test_list.add("Weather Alerts");

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.drawable.triangle);
            mBuilder.setContentTitle("Alert Hub");
            mBuilder.setContentText("Check New Alerts!");
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());

            final ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, test_list);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                Intent intent = new Intent(mainpage.this, openDetailActivity.class);
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(position == 0) {
                         String message = "ohioamberalert";
                        intent.putExtra("timeline", message);
                        startActivity(intent);


                        } else if(position == 1){
                        String message = "swa_columbusoh";
                        intent.putExtra("timeline", message);
                        startActivity(intent);
                    }
                }
            });

        }



}
