/*
 * ******************************************************
 *  * Copyright (C) 2019-2020 Angelos Ladopoulos ladopoulos.angelos@gmail.com
 *  *
 *  * This file is part of MediaSpecs Android application.
 *  *
 *  *MediaSpecs application can not be copied and/or distributed without the express
 *  * permission of Angelos Ladopoulos
 *  ******************************************************
 */

package gr.medialab.mediaspecs;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import static android.app.admin.DevicePolicyManager.PERMISSION_POLICY_AUTO_GRANT;

public class DeviceAdmin extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        //Log.i("Device Admin: ", "Enabled");
    }

    @Override
    public String onDisableRequested(Context context, Intent intent) {
        return "Admin disable requested";

    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        //Log.i("Device Admin: ", "Disabled");
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        super.onPasswordChanged(context, intent);
        //Log.i("Device Admin: ", "Password changed");
    }

    public void onProfileProvisioningComplete (Context context, Intent intent){
        String KIOSK_PACKAGE = "gr.medialab.mediaspecs";
        String[] APP_PACKAGES = {KIOSK_PACKAGE};
        List<String> list = Arrays.asList(APP_PACKAGES);


        DevicePolicyManager dpm =
                (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminName = getComponentName(context);
        dpm.setLockTaskPackages(adminName, APP_PACKAGES);
        dpm.setMaximumTimeToLock(adminName, 60*1000L);
        dpm.setPermissionPolicy(adminName, PERMISSION_POLICY_AUTO_GRANT);
        dpm.setPermittedAccessibilityServices(adminName, list);
    }

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), DeviceAdmin.class);
    }

    private void admin(){
    }

}
