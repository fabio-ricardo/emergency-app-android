package dwhit.emerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

public class openDHSDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_dhs_detail);

        Intent intent = getIntent();
        String type = intent.getStringExtra(dhsActivity.TYPE);
        String summary = intent.getStringExtra(dhsActivity.SUMMARY);
        String details = intent.getStringExtra(dhsActivity.DETAILS);

        String message = "The Department of Homeland Security has issued a national emergency " +
                "with an "+ type + " to national security.";

        TextView tv1 = (TextView) findViewById(R.id.textView1);
        tv1.setText(message);

        TextView tv2 = (TextView) findViewById(R.id.textView2);
        tv2.setText(summary);

        TextView tv3 = (TextView) findViewById(R.id.textView3);
        tv3.setText(details);
    }
}
