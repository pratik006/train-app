package com.prapps.app.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.prapps.app.activities.SearchUI;

/**
 * Created by pratik on 24/2/17.
 */

public class NetBroadcastReveiver extends BroadcastReceiver {

    private SearchUI searchUI;
    public NetBroadcastReveiver(SearchUI searchUI) {
        this.searchUI = searchUI;
    }

    @Override
    public void onReceive(Context context, Intent intent )
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        if (connectivityManager.getActiveNetworkInfo() != null) {
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE );
            if ( activeNetInfo != null )
            {
                searchUI.loadStations();
                searchUI.setNearestLoc();
                Toast.makeText( context, "Active Network Type : " + activeNetInfo.getTypeName(), Toast.LENGTH_SHORT ).show();
            }
            if( mobNetInfo != null )
            {
                Toast.makeText( context, "Mobile Network Type : " + mobNetInfo.getTypeName(), Toast.LENGTH_SHORT ).show();
            }
        }

    }
}
