package kaerushi.weeabooify.uwu.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import kaerushi.weeabooify.uwu.BuildConfig;
import kaerushi.weeabooify.uwu.Weeabooify;
import kaerushi.weeabooify.uwu.R;
import kaerushi.weeabooify.uwu.config.PrefConfig;
import kaerushi.weeabooify.uwu.utils.ModuleUtil;
import kaerushi.weeabooify.uwu.utils.OverlayUtils;
import kaerushi.weeabooify.uwu.utils.RootUtil;

import java.io.IOException;

public class WelcomePage extends AppCompatActivity {

    private final int versionCode = BuildConfig.VERSION_CODE;
    private final String versionName = BuildConfig.VERSION_NAME;
    private LinearLayout spinner;
    private RadioGroup radioGroup;
    Button nusaVariant, rrVariant;

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        // Progressbar while installing module
        spinner = findViewById(R.id.progressBar_installingModule);

        // Choose Variant
        nusaVariant = findViewById(R.id.nusa_variant);
        rrVariant = findViewById(R.id.rr_variant);
        radioGroup = findViewById(R.id.radio_variant);

        // Continue button
        Button checkRoot = findViewById(R.id.checkRoot);
        radioGroup.clearCheck();

        radioGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                        RadioButton radioButton = (RadioButton)radioGroup.findViewById(checkedId);
                        Transition transition = new Fade();
                        transition.setDuration(1200);
                        transition.addTarget(R.id.checkRoot);

                        TransitionManager.beginDelayedTransition(radioGroup, transition);

                        checkRoot.setVisibility(View.VISIBLE);
                    }
                }
        );
        // Dialog to show if root not found
        LinearLayout warn = findViewById(R.id.warn);
        TextView warning = findViewById(R.id.warning);

        // Check for root onClick
        checkRoot.setOnClickListener(v -> {

            // Selected Variant
            int selectedId = radioGroup.getCheckedRadioButtonId();
                switch (selectedId) {
                    case R.id.nusa_variant:
                        Toast.makeText(this, "Selected Nusantara", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rr_variant:
                        Toast.makeText(this, "Selected Resurrection Remix", Toast.LENGTH_SHORT).show();
                        break;
                }
            if (RootUtil.isDeviceRooted()) {
                if (RootUtil.isMagiskInstalled()) {
                    if ((PrefConfig.loadPrefInt(this, "versionCode") < versionCode) || !ModuleUtil.moduleExists()) {

                        radioGroup.setVisibility(View.GONE);

                        // Show spinner
                        spinner.setVisibility(View.VISIBLE);

                        // Block touch
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ModuleUtil.handleModule(Weeabooify.getAppContext());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Hide spinner
                                        spinner.setVisibility(View.GONE);
                                        // Unblock touch
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        if (PrefConfig.loadPrefInt(Weeabooify.getAppContext(), "versionCode") != 0)
                                            Toast.makeText(getApplicationContext(), "Reboot to Apply Changes", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
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

                        // Show spinner
                        spinner.setVisibility(View.VISIBLE);

                        // Block touch
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ModuleUtil.handleModule(Weeabooify.getAppContext());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Hide spinner
                                        spinner.setVisibility(View.GONE);
                                        // Unblock touch
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        warn.setVisibility(View.VISIBLE);
                                        warning.setText("Reboot your device first!");
                                    }
                                });
                            }
                        };
                        Thread thread = new Thread(runnable);
                        thread.start();
                    }
                } else {
                    warn.setVisibility(View.VISIBLE);
                    warning.setText("Use Magisk to root your device!");
                }
            } else {
                warn.setVisibility(View.VISIBLE);
                warning.setText("Looks like your device is not rooted!");
            }
        });
    }
}