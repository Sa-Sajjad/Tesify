package kaerushi.weeabooify.uwu.installer;

import kaerushi.weeabooify.uwu.Weeabooify;
import kaerushi.weeabooify.uwu.config.PrefConfig;
import kaerushi.weeabooify.uwu.services.ApplyOnBoot;
import com.topjohnwu.superuser.Shell;

import java.io.File;

public class QsStyleInstaller {

    private static final int TOTAL_QSSTYLE = 3;

    public static void install_pack(int n) {
        disable_others(n);
        enable_pack(n);
        PrefConfig.savePrefBool(Weeabooify.getAppContext(), "fabricatedcornerRadius", true);
    }


    private static void enable_pack(int n) {

        String[] paths = {"/system/product/overlay/WeeabooifyComponentADDAS" + n + ".apk", "/system/product/overlay/WeeabooifyComponentQS" + n + ".apk"};

        for (String path : paths) {
            if (new File(path).exists()) {

                String overlay = (path.replaceAll("/system/product/overlay/", "")).replaceAll("apk", "overlay");

                try {
                    Shell.cmd("cmd overlay enable --user current " + overlay).exec();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }


    public static void disable_pack(int n) {

        String[] paths = {"/system/product/overlay/WeeabooifyComponentADDAS" + n + ".apk", "/system/product/overlay/WeeabooifyComponentQS" + n + ".apk"};

        for (String path : paths) {
            if (new File(path).exists()) {

                String overlay = (path.replaceAll("/system/product/overlay/", "")).replaceAll("apk", "overlay");

                try {
                    Shell.cmd("cmd overlay disable --user current " + overlay).exec();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    protected static void disable_others(int n) {

        for (int i = 1; i <= TOTAL_QSSTYLE; i++) {
            if (i != n) {
                String[] paths = {"/system/product/overlay/WeeabooifyComponentADDAS" + n + ".apk", "/system/product/overlay/WeeabooifyComponentQS" + n + ".apk"};

                for (String path : paths) {
                    if (new File(path).exists()) {

                        String overlay = (path.replaceAll("/system/product/overlay/", "")).replaceAll("apk", "overlay");

                        try {
                            Shell.cmd("cmd overlay disable --user current " + overlay).exec();
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}
