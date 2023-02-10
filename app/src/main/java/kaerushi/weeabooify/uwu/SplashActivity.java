package kaerushi.weeabooify.uwu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import kaerushi.weeabooify.uwu.config.PrefConfig;
import kaerushi.weeabooify.uwu.ui.HomePage;
import kaerushi.weeabooify.uwu.ui.WelcomePage;
import kaerushi.weeabooify.uwu.utils.ModuleUtil;
import kaerushi.weeabooify.uwu.utils.OverlayUtils;
import kaerushi.weeabooify.uwu.utils.RootUtil;
import com.topjohnwu.superuser.Shell;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static SplashActivity mContext;

    static {
        Shell.enableVerboseLogging = BuildConfig.DEBUG;
        if (Shell.getCachedShell() == null)
            Shell.setDefaultBuilder(Shell.Builder.create().setFlags(Shell.FLAG_REDIRECT_STDERR).setTimeout(20));
    }

    private boolean keepShowing = true;

    private final int versionCode = BuildConfig.VERSION_CODE;

    public static SplashActivity getContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shell.getShell(shell -> {
            mContext = this;

            Intent intent;

            if (RootUtil.isDeviceRooted() && RootUtil.isMagiskInstalled() && ModuleUtil.moduleExists() && OverlayUtils.overlayExists() && (versionCode == PrefConfig.loadPrefInt(this, "versionCode"))) {
                keepShowing = false;
                intent = new Intent(SplashActivity.this, HomePage.class);
            } else {
                keepShowing = false;
                intent = new Intent(SplashActivity.this, WelcomePage.class);
            }

            startActivity(intent);
            finish();
        });
    }
}