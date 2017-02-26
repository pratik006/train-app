package com.prapps.app.activities;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;*/
import com.prapps.app.model.Greeting;
import com.prapps.app.model.KeyValue;
import com.prapps.app.model.Station;
import com.prapps.app.util.Command;
import com.prapps.app.util.NetBroadcastReveiver;
import com.prapps.app.util.RestCaller;
import com.prapps.app.util.SingleShotLocationProvider;
import com.prapps.app.util.StationAdapter;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity implements SearchUI{

    public static final String BASE_URL = "http://apps-pratiks.rhcloud.com/rest/rail/hyd-mmts";
    public static final String GET_STATIONS_URL = "/stations";
    public static final String GET_NEAREST_STATIONS_URL = "/findNearestStations";
    private Button btnFind;
    private int layoutId;
    private Map<String,String> stationMap = new HashMap<>();
    private BroadcastReceiver bReceiver;
    private ProgressDialog spinner;
    /*private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;*/

    private void init() {
        btnFind = (Button)findViewById(R.id.btnFind);
        layoutId = findViewById(R.id.btnFind).getId();
        final TextView view = (TextView) getMe().findViewById(R.id.txtFrom);
        Log.d(SearchActivity.class.getSimpleName(), findViewById(R.id.btnFind).getId()+"");
        this.bReceiver = new NetBroadcastReveiver(this);
        spinner = new ProgressDialog(this);
        spinner.setTitle("Loading");
        spinner.setMessage("Wait while loading...");
        spinner.setCancelable(false); // disable dismiss by tapping outside of the dialog
        spinner.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(bReceiver, new IntentFilter());
        //mGoogleApiClient.connect();
    }

    /*@Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }*/

    public void btnFindOnClick(View view) {
        String from = String.valueOf(((TextView)findViewById(R.id.txtFrom)).getText());
        String to = String.valueOf(((TextView)findViewById(R.id.txtTo)).getText());
        Log.d(SearchActivity.class.getSimpleName(),"clicked "+from+" "+to);

        Intent intent = new Intent(getMe(), SearchResultActivity.class);
        intent.putExtra("from", stationMap.get(from));
        intent.putExtra("to", stationMap.get(to));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        init();
    }

    public SearchActivity getMe() {
        return this;
    }

    @Override
    protected void onPause() {
        unregisterReceiver(bReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver(
                this.bReceiver,
                new IntentFilter(
                        ConnectivityManager.CONNECTIVITY_ACTION));
        super.onResume();
    }

    @Override
    public void loadStations() {
        if (!stationMap.isEmpty()) {
            //no need to reload stations when app is resumed
            return;
        }

        RestCaller<Station[]> rClient = new RestCaller(RestCaller.GET_STATIONS_URL, new Command<Station[]>() {
            @Override
            public void execute(Station[] stations) {
                final String[] names = new String[stations.length];
                int ctr = 0;
                for (Station stn : stations) {
                    names[ctr++] = stn.getName();
                    stationMap.put(stn.getName(), stn.getCode());
                }
                Log.d(RestCaller.class.getSimpleName(), Arrays.toString(stations));
                StationAdapter adapter = new StationAdapter(getMe(),
                        android.R.layout.select_dialog_item, stations);

                ArrayAdapter aAdapter = new ArrayAdapter<String>(getMe(),
                        android.R.layout.select_dialog_item, names);
                AutoCompleteTextView textViewFrom = (AutoCompleteTextView)
                        findViewById(R.id.txtFrom);
                textViewFrom.setAdapter(aAdapter);
                AutoCompleteTextView textView = (AutoCompleteTextView)
                        findViewById(R.id.txtTo);
                textView.setAdapter(aAdapter);
                spinner.dismiss();
            }
        },Station[].class);
        rClient.execute();
    }

    @Override
    public void setNearestLoc() {
        final AutoCompleteTextView textViewFrom = (AutoCompleteTextView)
                findViewById(R.id.txtFrom);
        if (textViewFrom.getText() != null && textViewFrom.getText().length()>0) {
            //already user has set some value and this will be invoked when app is resumed
            return;
        }


        SingleShotLocationProvider.requestSingleUpdate(getMe(), new SingleShotLocationProvider.LocationCallback() {
            @Override
            public void onNewLocationAvailable(final SingleShotLocationProvider.GPSCoordinates location) {
                Log.d(SearchActivity.class.getSimpleName(), location.latitude+","+location.longitude);
                RestCaller<Station[]> rClient = new RestCaller(RestCaller.GET_NEAREST_STATIONS_URL, new Command<Station[]>() {
                    @Override
                    public void execute(Station[] stations) {
                        if(stations != null && stations.length > 0) {
                            textViewFrom.setText(stations[0].getName());
                            AutoCompleteTextView textViewTo = (AutoCompleteTextView)
                                    findViewById(R.id.txtTo);
                            textViewTo.requestFocus();
                        }
                    }
                },Station[].class);
                rClient.execute(new KeyValue("lat", location.latitude + ""), new KeyValue("lon", location.longitude + ""));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(SearchActivity.class.getName(), "provider "+provider+" status changed");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(SearchActivity.class.getName(), "provider "+provider+" status disabled");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(SearchActivity.class.getName(), "provider "+provider+" status enabled");
            }
        });
    }
}
