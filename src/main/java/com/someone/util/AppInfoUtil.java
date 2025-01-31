package com.someone.util;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
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

import com.someone.debug.LogReceiver;

/**
 * @Author Someone
 * @Date 2024/09/25 19:41
 */
public class AppInfoUtil {
    
    private static final PackageManager packageManager;
    private static ApplicationInfo applicationInfo;
    static{
        packageManager = GlobalUtilSetting.getContext().getPackageManager();
    }

    public static PackageInfo getPackageInfo(String packageName, int flags) {
        try {
            return packageManager.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            LogReceiver.i("ApplicationUtils", "getPackageInfo(): " + e);
            return null;
        }
    }

    public static String getPackageName() {
        return GlobalUtilSetting.getContext().getPackageName();
    }


    public static byte[] getSignatures() {
        Signature signature = getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES).signatures[0];
        return signature.toByteArray();
    }

    public static Integer getVersionCode() {
        return getPackageInfo(getPackageName(), 0).versionCode;
    }

    public static String getVersionName() {
        return getPackageInfo(getPackageName(), 0).versionName;
    }

    public static String getShareUserId() {
        return getPackageInfo(getPackageName(), 0).sharedUserId;
    }

    public static int getShareUserLabel() {
        return getPackageInfo(getPackageName(), 0).sharedUserLabel;
    }

    public static int getBaseRevisionCode() {
        return getPackageInfo(getPackageName(), 0).baseRevisionCode;
    }

    public static long getFirstInstallTime() {
        return getPackageInfo(getPackageName(), 0).firstInstallTime;
    }

    public static long getLastUpdateTime() {
        return getPackageInfo(getPackageName(), 0).lastUpdateTime;
    }

    public static int getInstallLocation() {
        return getPackageInfo(getPackageName(), 0).installLocation;
    }

    public static ActivityInfo[] getActivities() {
        return getPackageInfo(getPackageName(), 0).activities;
    }

    public static ConfigurationInfo[] getConfigPreferences() {
        return getPackageInfo(getPackageName(), 0).configPreferences;
    }

    public static FeatureGroupInfo[] getFeatureGroups() {
        return getPackageInfo(getPackageName(), 0).featureGroups;
    }

    public static Attribution[] getAttributions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return getPackageInfo(getPackageName(), 0).attributions;
        }
        return new Attribution[0];
    }

    public static PermissionInfo[] getPermissions() {
        return getPackageInfo(getPackageName(), 0).permissions;
    }

    public static int[] getGids() {
        return getPackageInfo(getPackageName(), 0).gids;
    }

    public static InstrumentationInfo[] getInstrumentation() {
        return getPackageInfo(getPackageName(), 0).instrumentation;
    }

    public static ProviderInfo[] getProviders() {
        return getPackageInfo(getPackageName(), 0).providers;
    }

    public static ActivityInfo[] getReceivers() {
        return getPackageInfo(getPackageName(), 0).receivers;
    }

    public static FeatureInfo[] getReqFeatures() {
        return getPackageInfo(getPackageName(), 0).reqFeatures;
    }

    public static String[] geRequestedPermissions() {
        return getPackageInfo(getPackageName(), 0).requestedPermissions;
    }

    public static int[] getRequestedPermissionsFlags() {
        return getPackageInfo(getPackageName(), 0).requestedPermissionsFlags;
    }

    public static ServiceInfo[] getServices() {
        return getPackageInfo(getPackageName(), 0).services;
    }

    public static String[] getSplitNames() {
        return getPackageInfo(getPackageName(), 0).splitNames;
    }

    public static int[] getSplitRevisionCodes() {
        return getPackageInfo(getPackageName(), 0).splitRevisionCodes;
    }

}
