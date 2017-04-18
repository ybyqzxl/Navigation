package panda.li.navigation.main;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import panda.li.navigation.R;

/**
 * Created by Aleck_ on 2016/8/4.
 */
public class StationListActivity extends Activity {

    private String StationName;
    private String StationLine;
    private TextView txt_StationName;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stationlist);

        bundle = this.getIntent().getExtras();
        StationName = bundle.getString("StationName");
        StationLine = bundle.getString("StationLine");

        txt_StationName = (TextView) findViewById(R.id.txt_StationName);
        txt_StationName.setText(StationName + ":" + StationLine);

    }
}
