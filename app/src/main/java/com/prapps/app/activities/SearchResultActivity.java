package com.prapps.app.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.prapps.app.model.KeyValue;
import com.prapps.app.model.RunDayType;
import com.prapps.app.model.Train;
import com.prapps.app.util.Command;
import com.prapps.app.util.RestCaller;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pratik on 18/2/17.
 */

public class SearchResultActivity extends AppCompatActivity {

    public static final String BASE_URL = "http://apps-pratiks.rhcloud.com/rest/rail/hyd-mmts";

    List<Train> trains = new ArrayList<>();
    String from;
    String to;
    ArrayAdapter<Train> adapter;
    SearchResultActivity getMe() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_activity);
        setTitle("Trains");
        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        to = intent.getStringExtra("to");

        //ListView listView = (ListView) findViewById(R.id.listView);

        //((TextView)findViewById(R.id.empty)).setText("No Trains Found");
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        RestCaller<Train[]> rClient = new RestCaller(RestCaller.FIND_TRAINS_URL, new Command<Train[]>() {
            @Override
            public void execute(Train[] trainObjects) {
                progress.dismiss();
                ListView listView = (ListView) findViewById(R.id.listView);
                if (trainObjects ==  null || trainObjects.length == 0) {
                    //listView.setEmptyView(findViewById(R.id.empty));.
                    final LinearLayout lm = (LinearLayout) findViewById(R.id.mainLayout);
                    TextView noTrainView = new TextView(getMe());
                    noTrainView.setText(" No Trains found");
                    noTrainView.setGravity(Gravity.CENTER);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                    params.height = 3;
                    View line = new View(getMe());
                    line.setLayoutParams(params);
                    line.setBackgroundColor(Color.parseColor("#a9a9a9a9"));
                    params.setMargins(0,0,0,30);

                    LinearLayout ll = new LinearLayout(getMe());
                    ll.setOrientation(LinearLayout.VERTICAL);
                    ll.setGravity(Gravity.CENTER);
                    ll.addView(line);
                    ll.addView(noTrainView);
                    lm.addView(ll);

                    return;
                }

                adapter = new StableArrayAdapter(getMe(), android.R.layout.simple_list_item_1);
                listView.setAdapter(adapter);
                final String[] names = new String[trainObjects.length];
                int ctr = 0;
                adapter.clear();
                for (Train train : trainObjects) {
                    Log.d(SearchActivity.class.getSimpleName(), train.getId()+"");
                    trains.add(train);
                    adapter.add(train);
                }
            }
        }, Train[].class);
        rClient.execute(new KeyValue<String>("from", from), new KeyValue<String>("to", to));
    }

    private class StableArrayAdapter extends ArrayAdapter<Train> {

        HashMap<Train, Integer> mIdMap = new HashMap<Train, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.search_result_activity, parent, false);
            }

            Train train = trains.get(position);

            TextView departure = (TextView) itemView.findViewById(R.id.departureText);
            departure.setText(train.getRoutes().get(0).getDeparture());
            departure.setGravity(Gravity.RIGHT);

            TextView id = (TextView) itemView.findViewById(R.id.trainNoView);
            id.setText(train.getId()+"");
            id.setGravity(Gravity.LEFT);

            TextView name = (TextView) itemView.findViewById(R.id.trainName);
            name.setGravity(Gravity.RIGHT);
            name.setText(train.getName());


           /* TextView txtRunday = (TextView) itemView.findViewById(R.id.txtRunday);
            String runday = "";
            for (Integer type : train.getRunDayType())  {
                runday += RunDayType.getByDayOfWeek(type).getRunDay() + " ";
            }
            txtRunday.setText(runday);*/
            return itemView;
        }

        @Override
        public long getItemId(int position) {
            Train item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public void add(Train object) {
            super.add(object);
            mIdMap.put(object, mIdMap.size());
        }
    }
}
