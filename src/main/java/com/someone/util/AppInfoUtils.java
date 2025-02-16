package com.someone.util;

/*
 * @Author Someone
 * @Date 2024/09/25 19:41
 */

import android.content.pm.ActivityInfo;
import android.content.pm.Attribution;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureGroupInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.os.Build;

public class AppInfoUtils {
    
    private static final PackageManager packageManager = GlobalContextUtil.getContext().getPackageManager();

    public static PackageInfo getPackageInfo(String packageName, int flags) throws PackageManager.NameNotFoundException {
            return packageManager.getPackageInfo(packageName, flags);
    }

    public static String getPackageName() {
        return GlobalContextUtil.getContext().getPackageName();
    }


    public static byte[] getSignatures() throws PackageManager.NameNotFoundException {
        Signature signature = getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES).signatures[0];
        return signature.toByteArray();
    }

    public static Integer getVersionCode() throws PackageManager.NameNotFoundException {
        return getPackageInfo(getPackageName(), 0).versionCode;
    }

    public static String getVersionName() throws PackageManager.NameNotFoundException {
        return getPackageInfo(getPackageName(), 0).versionName;
    }

    public static String getShareUserId() throws PackageManager.NameNotFoundException {
        return getPackageInfo(getPackageName(), 0).sharedUserId;
    }

    public static int getShareUserLabel() throws PackageManager.NameNotFoundException {
        return getPackageInfo(getPackageName(), 0).sharedUserLabel;
    }

    public static int getBaseRevisionCode() throws PackageManager.NameNotFoundException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return getPackageInfo(getPackageName(), 0).baseRevisionCode;
        }
        return 0;
    }

    public static long getFirstInstallTime() throws PackageManager.NameNotFoundException {
        return getPackageInfo(getPackageName(), 0).firstInstallTime;
    }

    public static long getLastUpdateTime() throws PackageManager.NameNotFoundException {
        return getPackageInfo(getPackageName(), 0).lastUpdateTime;
    }

    public static int getInstallLocation() throws PackageManager.NameNotFoundException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getPackageInfo(getPackageName(), 0).installLocation;
        }
        return 0;
    }

    public static ActivityInfo[] getActivities() throws PackageManager.NameNotFoundException {
        return getPackageInfo(getPackageName(), 0).activities;
    }

    public static ConfigurationInfo[] getConfigPreferences() throws PackageManager.NameNotFoundException {
        return getPackageInfo(getPackageName(), 0).configPreferences;
    }

    public static FeatureGroupInfo[] getFeatureGroups() throws PackageManager.NameNotFoundException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getPackageInfo(getPackageName(), 0).featureGroups;
        }
        return new FeatureGroupInfo[0];
    }

    public static Attribution[] getAttributions() throws PackageManager.NameNotFoundException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return getPackageInfo(getPackageName(), 0).attributions;
        }
        return new Attribution[0];
    }

    public static PermissionInfo[] getPermissions() throws PackageManager.NameNotFoundException {
        return getPackageInfo(getPackageName(), 0).permissions;
    }

    public static int[] getGids() throws PackageManager.NameNotFoundException {
        return getPackageInfo(getPackageName(), 0).gids;
    }

    public static InstrumentationInfo[] getInstrumentation() throws PackageManager.NameNotFoundException {
        return getPackageInfo(getPackageName(), 0).instrumentation;
    }

    public static ProviderInfo[] getProviders() throws PackageManager.NameNotFoundException {
        return getPackageInfo(getPackageName(), 0).providers;
    }

    public static ActivityInfo[] getReceivers() throws PackageManager.NameNotFoundException {
        return getPackageInfo(getPackageName(), 0).receivers;
    }

    public static FeatureInfo[] getReqFeatures() throws PackageManager.NameNotFoundException {
        return getPackageInfo(getPackageName(), 0).reqFeatures;
    }

    public static String[] geRequestedPermissions() throws PackageManager.NameNotFoundException {
        return getPackageInfo(getPackageName(), 0).requestedPermissions;
    }

    public static int[] getRequestedPermissionsFlags() throws PackageManager.NameNotFoundException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return getPackageInfo(getPackageName(), 0).requestedPermissionsFlags;
        }
        return new int[0];
    }

    public static ServiceInfo[] getServices() throws PackageManager.NameNotFoundException {
        return getPackageInfo(getPackageName(), 0).services;
    }

    public static String[] getSplitNames() throws PackageManager.NameNotFoundException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getPackageInfo(getPackageName(), 0).splitNames;
        }
        return new String[0];
    }

    public static int[] getSplitRevisionCodes() throws PackageManager.NameNotFoundException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return getPackageInfo(getPackageName(), 0).splitRevisionCodes;
        }
        return new int[0];
    }

}
