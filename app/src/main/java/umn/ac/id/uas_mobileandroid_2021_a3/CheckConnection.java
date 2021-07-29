package umn.ac.id.uas_mobileandroid_2021_a3;

import android.content.Context;
import android.net.ConnectivityManager;

import java.net.InetAddress;

public class CheckConnection {
    public Boolean checkNetwork(Context context){
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public Boolean checkInternet(){
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            return (returnVal==0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
