package com.kaisebhi.kaisebhi.Utility;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.kaisebhi.kaisebhi.R;

public class Utility {
    private static String TAG = "Utility.java";
    private static AlertDialog alertDialog;
    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**Below method is to return network connectivity of our application on device.
     * @param ctx is context using which we can get system service of Connectivity Service. */
    public static boolean isNetworkAvailable(Context ctx) {
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ConnectivityManager connectivityManager = ctx.getSystemService(ConnectivityManager.class);
                Network network = connectivityManager.getActiveNetwork();
                NetworkCapabilities netCap = connectivityManager.getNetworkCapabilities(network);
                if(netCap == null)
                    return false;
                return netCap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || netCap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
            } else {
                ConnectivityManager conMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
                return conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()
                        == NetworkInfo.State.CONNECTED || conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED;
            }
        } catch (Exception e) {
            Log.d(TAG, "isNetworkAvailable: " + e);
        }
        return false;
    }

    /**Below is AlertDialog which is prompt window layout to display an alert dialog which is not gonna dismiss until
     * the network is not connected. */
    public static void noNetworkDialog(Context ctx) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setCancelable(false);
            builder.setMessage("Internet not Available");
            /**Below lambda have DialogInterface instance and which int type. */
            builder.setPositiveButton("Okay", (dialog, which) -> {
                Log.d(TAG, "noNetworkDialog: " + isNetworkAvailable(ctx));
                if(isNetworkAvailable(ctx)) {
                    toast(ctx, "Please refresh to see result");
                    alertDialog.dismiss();
                } else {
                    alertDialog.dismiss();
                    alertDialog = builder.create();
                    alertDialog.show();
                }
            });
            alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            Log.d(TAG, "noNetworkDialog: " + e);
        }
    }



    public static AlertDialog createAlertDialog(Context context) {

        AlertDialog pDialog = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        return pDialog;
    }
}
