package kaerushi.weeabooify.uwuify.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Objects;

import kaerushi.weeabooify.uwuify.BuildConfig;
import kaerushi.weeabooify.uwuify.R;
import kaerushi.weeabooify.uwuify.Weeabooify;
import kaerushi.weeabooify.uwuify.config.PrefConfig;
import kaerushi.weeabooify.uwuify.utils.ModuleUtil;
import kaerushi.weeabooify.uwuify.utils.OverlayUtils;
import kaerushi.weeabooify.uwuify.utils.RootUtil;

public class WelcomePage extends AppCompatActivity {

    private final int versionCode = BuildConfig.VERSION_CODE;
    private final String versionName = BuildConfig.VERSION_NAME;
    private LinearLayout spinner;

    @SuppressLint({"SetTextI18n", "NonConstantResourceId", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        // Progressbar while installing module
        spinner = findViewById(R.id.progressBar_installingModule);

        // Continue button
        Button checkRoot = findViewById(R.id.checkRoot);

        // Dialog to show if root not found
        LinearLayout warn = findViewById(R.id.warn);
        TextView warning = findViewById(R.id.warning);

        // Rom variant
        LinearLayout nusa_variant = findViewById(R.id.nusa_variant);
        LinearLayout rr_variant = findViewById(R.id.rr_variant);
        LinearLayout havoc_variant = findViewById(R.id.havoc_variant);
        LinearLayout los_variant = findViewById(R.id.los_variant);

        nusa_variant.setOnClickListener(v -> {
            warn.setVisibility(View.INVISIBLE);

            PrefConfig.savePrefSettings(Weeabooify.getAppContext(), "selectedRomVariant", "Nusan");
            Toast.makeText(Weeabooify.getAppContext(), "Selected Nusantara", Toast.LENGTH_SHORT).show();
            nusa_variant.setBackground(getResources().getDrawable(R.drawable.container_selected));
            rr_variant.setBackground(getResources().getDrawable(R.drawable.container));

            Transition transition = new Fade();
            transition.setDuration(1200);
            transition.addTarget(R.id.checkRoot);
            TransitionManager.beginDelayedTransition(nusa_variant, transition);
            checkRoot.setVisibility(View.VISIBLE);
        });

        rr_variant.setOnClickListener(v -> {
            warn.setVisibility(View.INVISIBLE);

            PrefConfig.savePrefSettings(Weeabooify.getAppContext(), "selectedRomVariant", "RR");
            Toast.makeText(Weeabooify.getAppContext(), "Selected Resurrection Remix", Toast.LENGTH_SHORT).show();
            rr_variant.setBackground(getResources().getDrawable(R.drawable.container_selected));
            nusa_variant.setBackground(getResources().getDrawable(R.drawable.container));

            Transition transition = new Fade();
            transition.setDuration(1200);
            transition.addTarget(R.id.checkRoot);
            TransitionManager.beginDelayedTransition(rr_variant, transition);
            checkRoot.setVisibility(View.VISIBLE);
        });

        havoc_variant.setOnClickListener(view -> {
            Toast.makeText(Weeabooify.getAppContext(), "Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        los_variant.setOnClickListener(view -> {
            Toast.makeText(Weeabooify.getAppContext(), "Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        // Check for root onClick
        checkRoot.setOnClickListener(v -> {
            warn.setVisibility(View.INVISIBLE);
            if (Objects.equals(PrefConfig.loadPrefSettings(Weeabooify.getAppContext(), "selectedRomVariant"), "null"))
                Toast.makeText(Weeabooify.getAppContext(), "Select a ROM before proceeding", Toast.LENGTH_SHORT).show();
            else {
                if (RootUtil.isDeviceRooted()) {
                    if (RootUtil.isMagiskInstalled()) {
                        if ((PrefConfig.loadPrefInt(this, "versionCode") < versionCode) || !ModuleUtil.moduleExists() || !OverlayUtils.overlayExists()) {

                            // Show spinner
                            spinner.setVisibility(View.VISIBLE);

                            // Block touch
                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            Runnable runnable = () -> {
                                try {
                                    ModuleUtil.handleModule(Weeabooify.getAppContext());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(() -> {
                                    // Hide spinner
                                    spinner.setVisibility(View.GONE);
                                    // Unblock touch
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    if (PrefConfig.loadPrefInt(Weeabooify.getAppContext(), "versionCode") != 0)
                                        Toast.makeText(getApplicationContext(), "Reboot to Apply Changes", Toast.LENGTH_LONG).show();
                                });
                            };
                            Thread thread = new Thread(runnable);
                            thread.start();
                        }
                        if (OverlayUtils.overlayExists()) {
                            PrefConfig.savePrefInt(this, "versionCode", versionCode);
                            Intent intent = new Intent(WelcomePage.this, HomePage.class);
                            startActivity(intent);
                            finish();
                        } else {
                            warn.setVisibility(View.VISIBLE);
                            warning.setText("Reboot your device first!");
                        }
                    } else {
                        warn.setVisibility(View.VISIBLE);
                        warning.setText("Use Magisk to root your device!");
                    }
                } else {
                    warn.setVisibility(View.VISIBLE);
                    warning.setText("Looks like your device is not rooted!");
                }
            }
        });
    }
}