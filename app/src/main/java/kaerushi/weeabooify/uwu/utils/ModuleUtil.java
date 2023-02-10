package kaerushi.weeabooify.uwu.utils;

import android.content.Context;
import android.util.Log;

import kaerushi.weeabooify.uwu.BuildConfig;
import kaerushi.weeabooify.uwu.Weeabooify;
import kaerushi.weeabooify.uwu.config.PrefConfig;

import com.topjohnwu.superuser.Shell;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ModuleUtil {

    public static final int BYTE_ACCESS_RATE = 8192;
    public static final String MAGISK_DIR = "/data/adb/modules";
    public static final String MODULE_DIR = "/data/adb/modules/Uwuify";
    public static final String OVERLAY_DIR = "/data/adb/modules/Uwuify/system/product/overlay";

    public static void handleModule(final Context context) throws IOException {
        if (moduleExists()) {
            Shell.cmd("rm -rf " + MODULE_DIR).exec();
        }
        installModule(context);
    }

    static void installModule(Context context) {
        Log.e("ModuleCheck", "Magisk module does not exist, creating!");
        // Clean temporary directory
        Shell.cmd("mkdir -p " + MODULE_DIR).exec();
        Shell.cmd("printf 'id=Uwuify\nname=Uwuify\nversion=" + BuildConfig.VERSION_NAME + "\nversionCode=" + BuildConfig.VERSION_CODE + "\nauthor=@KaeruShi\ndescription=Systemless module for Uwuify.\n' > " + MODULE_DIR + "/module.prop").exec();
        Shell.cmd("mkdir -p " + MODULE_DIR + "/common").exec();
        Shell.cmd("printf '#!/system/bin/sh\n" +
                "# Do NOT assume where your module will be located.\n" +
                "# ALWAYS use $MODDIR if you need to know where this script\n" +
                "# and module is placed.\n" +
                "# This will make sure your module will still work\n" +
                "# if Magisk change its mount point in the future\n" +
                "# This script will be executed in late_start service mode\n' > " + MODULE_DIR + "/common/post-fs-data.sh").exec();
        Shell.cmd("printf '#!/system/bin/sh\n" +
                "# Do NOT assume where your module will be located.\n" +
                "# ALWAYS use $MODDIR if you need to know where this script\n" +
                "# and module is placed.\n" +
                "# This will make sure your module will still work\n" +
                "# if Magisk change its mount point in the future\n" +
                "MODDIR=${0%%/*}\n" +
                "# This script will be executed in late_start service mode\n' > " + MODULE_DIR + "/common/service.sh").exec();
        Shell.cmd("touch " + MODULE_DIR + "/common/system.prop").exec();
        Shell.cmd("mkdir -p " + MODULE_DIR + "/system").exec();
        Shell.cmd("mkdir -p " + MODULE_DIR + "/system/product").exec();
        Shell.cmd("mkdir -p " + MODULE_DIR + "/system/product/overlay").exec();
        Shell.cmd("printf '#!/system/bin/sh\n" +
                "# Please do not hardcode /magisk/modname/... ; instead, please use $MODDIR/...\n" +
                "# This will make your scripts compatible even if Magisk change its mount point in the future\n" +
                "MODDIR=${0%%/*}\n" +
                "\n" +
                "# This script will be executed in late_start service mode\n" +
                "# More info in the main Magisk thread\n' > " + MODULE_DIR + "/service.sh").exec();
        Log.e("ModuleCheck", "Magisk module successfully created!");

        copyOverlays(context);
    }

    public static boolean moduleExists() {
        List<String> lines = Shell.cmd("test -d " + MODULE_DIR + " && echo '1'").exec().getOut();
        for (String line : lines) {
            if (line.contains("1"))
                return true;
        }
        return false;
    }

    static void copyOverlays(Context context) {
        String[] overlays = new String[0];
        String selectedRom = "Component/" + PrefConfig.loadPrefSettings(Weeabooify.getAppContext(), "selectedRomVariant");
        try {
            overlays = context.getAssets().list(selectedRom);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String data_dir = context.getFilesDir().toString();
        // Clean temporary directory
        Shell.cmd("rm -rf " + data_dir).exec();
        File device_file = new File(data_dir + "/" + selectedRom + "/");
        Shell.cmd("mkdir -p " + data_dir + "/Component/").exec();
        Shell.cmd("mkdir -p " + device_file).exec();

        for (String overlay : overlays) {
            File file = new File(data_dir + "/" + selectedRom +"/" + overlay);
            if (!file.exists()) {
                try {
                    copyFileTo(context, selectedRom + "/" + overlay, data_dir + "/" + selectedRom + "/" + overlay);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Shell.cmd("cp -f " + data_dir + "/" + selectedRom + "/" + overlay + " " + OVERLAY_DIR + '/' + overlay).exec();
        }
        RootUtil.setPermissionsRecursively(644, OVERLAY_DIR + '/');
        // Clean temporary directory
        Shell.cmd("rm -rf " + data_dir + "/" + selectedRom).exec();
    }

    static void copyFileTo(Context c, String source, String destination) throws IOException {
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(destination);
        myInput = c.getAssets().open(source);

        byte[] buffer = new byte[BYTE_ACCESS_RATE];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();
    }
}
