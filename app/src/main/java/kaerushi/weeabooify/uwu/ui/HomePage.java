package kaerushi.weeabooify.uwu.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import kaerushi.weeabooify.uwu.VariantContainer;
import kaerushi.weeabooify.uwu.Weeabooify;
import kaerushi.weeabooify.uwu.R;
import kaerushi.weeabooify.uwu.adapter.VariantAdapter;
import kaerushi.weeabooify.uwu.config.PrefConfig;
import kaerushi.weeabooify.uwu.services.BackgroundService;
import kaerushi.weeabooify.uwu.utils.FabricatedOverlay;
import kaerushi.weeabooify.uwu.utils.OverlayUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    public static boolean isServiceRunning = false;
    private final String TAG = "WelcomePage";
    LinearLayout home_qsStyle, home_progressBar, home_extras, home_info;
    private ViewGroup container;
    ArrayList<VariantContainer> variantArrayContainer;

    @SuppressLint({"MissingInflatedId", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        PrefConfig.savePrefBool(Weeabooify.getAppContext(), "onHomePage", true);

        ViewPager2 variantViewPager = findViewById(R.id.variantPager);
        String[] titleHeading = {"Weeabooify UwU", "Weeabooify Anti-Entropy", "Weeabooify Schicksal"};
        String[] detail = {"Free Version", "For A12", "For A10"};
        int[] images = {R.drawable.uwu_banner, R.drawable.ae_banner, R.drawable.sc_banner};

        variantArrayContainer = new ArrayList<>();

        for (int i = 0; i < images.length; i++){
            VariantContainer variantItem = new VariantContainer(images[i], titleHeading[i], detail[i]);
            variantArrayContainer.add(variantItem);
        }

        VariantAdapter VAdapter = new VariantAdapter(variantArrayContainer);
        variantViewPager.setAdapter(VAdapter);
        variantViewPager.setClipChildren(false);
        variantViewPager.setClipToPadding(false);
        variantViewPager.setOffscreenPageLimit(3);
        variantViewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(30));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.10f);
            }
        });

        variantViewPager.setPageTransformer(compositePageTransformer);

        // Home page list items
        container = (ViewGroup) findViewById(R.id.home_page_list);
        addItem(R.id.home_qsStyle, "QS Style", "Change Quick Settings Layout", R.drawable.ic_extras_home);
        // addItem(R.id.home_progressBar, "Progress Bar", "Change progress bar style", R.drawable.ic_progress_home);
        addItem(R.id.home_extras, "Extras", "Additions tweaks and settings", R.drawable.ic_extras_home);
        addItem(R.id.home_info, "About", "Information about this app", R.drawable.ic_info_home);

        // Get list of enabled overlays
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                List<String> EnabledOverlays = OverlayUtils.getEnabledOverlayList();
                for (String overlay : EnabledOverlays)
                    PrefConfig.savePrefBool(Weeabooify.getAppContext(), overlay, true);

                List<String> EnabledFabricatedOverlays = FabricatedOverlay.getEnabledOverlayList();
                for (String overlay : EnabledFabricatedOverlays)
                    PrefConfig.savePrefBool(Weeabooify.getAppContext(), "fabricated" + overlay, true);
            }
        };
        Thread thread1 = new Thread(runnable1);
        thread1.start();

        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                if (!isServiceRunning)
                    startService(new Intent(Weeabooify.getAppContext(), BackgroundService.class));
            }
        };
        Thread thread2 = new Thread(runnable2);
        thread2.start();

        // QS Style item OnCLick
        home_qsStyle = findViewById(R.id.home_qsStyle);
        home_qsStyle.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_a));
        home_qsStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, QsStyle.class);
                startActivity(intent);
            }
        });

        // Extras item onClick
        home_extras = findViewById(R.id.home_extras);
        home_extras.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_b));
        home_extras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Extras.class);
                startActivity(intent);
            }
        });

        // About item onClick
        home_info = findViewById(R.id.home_info);
        home_info.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_c));
        home_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Info.class);
                startActivity(intent);
            }
        });
    }

    // Function to add new item in list
    private void addItem(int id, String title, String desc, int preview) {
        View list_view = LayoutInflater.from(this).inflate(R.layout.list_view, container, false);

        TextView list_title = (TextView) list_view.findViewById(R.id.list_title);
        TextView list_desc = (TextView) list_view.findViewById(R.id.list_desc);
        ImageView list_preview = (ImageView) list_view.findViewById(R.id.list_preview);

        list_view.setId(id);
        list_title.setText(title);
        list_desc.setText(desc);
        list_preview.setImageResource(preview);

        container.addView(list_view);
    }
}