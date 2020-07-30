package tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChecker {
    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager;
        NetworkInfo activeNetworkInfo;
        try
        {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        catch(Exception ex)
        {
            activeNetworkInfo = null;
            ex.printStackTrace();
        }

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
