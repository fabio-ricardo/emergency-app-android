package dwhit.emerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class dhsActivity extends AppCompatActivity {

    ListView lv;
    public final static String TYPE = "type", SUMMARY = "summary", DETAILS = "details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dhs);
        lv = (ListView) findViewById(R.id.listView);

        ArrayList<String> alert = new ArrayList<String>();
        final String type = getIntent().getStringExtra(mainpage.TYPE);
        final String summary = getIntent().getStringExtra(mainpage.SUMMARY);
        final String details = getIntent().getStringExtra(mainpage.DETAILS);

        if(!type.equals("")) {
            alert.add("New DHS alert");
            ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alert);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(dhsActivity.this, openDHSDetailActivity.class);
                    intent.putExtra(TYPE, type);
                    intent.putExtra(SUMMARY, summary);
                    intent.putExtra(DETAILS, details);
                    startActivity(intent);
                }
            });
        }
    }
}
