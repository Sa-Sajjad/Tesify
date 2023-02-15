package kaerushi.weeabooify.uwuify;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class Weeabooify extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static Context getAppContext() {
        return Weeabooify.context;
    }

    public void onCreate() {
        super.onCreate();
        Weeabooify.context = getApplicationContext();
    }
}