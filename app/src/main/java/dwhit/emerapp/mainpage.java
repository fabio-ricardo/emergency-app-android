package dwhit.emerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
            //String[] test_list = {"Amber Alerts","a","b","c","d","e","f","g","h","k"};
            List<String> test_list = new ArrayList<String>();
            test_list.add("Amber Alerts");
            final ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, test_list);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(position == 0) {
                        Intent intent = new Intent(mainpage.this, openDetailActivity.class);
                        String message = "abc";
                        intent.putExtra("message", message);
                        startActivity(intent);

                        }
                }
            });

        }



}
