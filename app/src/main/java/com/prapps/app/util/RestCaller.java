package com.prapps.app.util;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.AutoCompleteTextView;

import com.prapps.app.model.KeyValue;
import com.prapps.app.model.Station;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by pratik on 24/2/17.
 */

public class RestCaller<T> extends AsyncTask<KeyValue<String>, Void, T> {
    public static final String BASE_URL = "http://apps-pratiks.rhcloud.com/rest/rail/hyd-mmts";
    public static final String GET_STATIONS_URL = "/stations";
    public static final String GET_NEAREST_STATIONS_URL = "/findNearestStations";
    public static final String FIND_TRAINS_URL = "/findTrains";

    private Command cmd;
    private String url;
    private Class<T> clazz;

    public RestCaller(String url, Command command, Class<T> clazz) {
        Log.d(RestCaller.class.getName(), "RestUrl: "+url);
        this.cmd = command;
        this.url = url;
        this.clazz = clazz;
    }

    @Override
    protected T doInBackground(KeyValue<String>... params) {
        T t = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL+url);
            if (params != null) {
                for (KeyValue param : params) {
                    builder.queryParam(param.getKey(), param.getValue());
                }
            }
            URI targetUrl = builder.build().toUri();
            Log.d(RestCaller.class.getName(), "RestUrl: "+targetUrl);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            t = restTemplate.getForObject(targetUrl, clazz);
            Log.d(RestCaller.class.getSimpleName(), t+"");
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }

        return t;
    }

    @Override
    protected void onPostExecute(T t) {
        cmd.execute(t);
    }
}
