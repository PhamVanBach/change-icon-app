package com.change_app_icon.JSBridge;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

import java.util.HashSet;
import java.util.Set;

@ReactModule(name = RNChangeIconModule.NAME)
public class RNChangeIconModule extends ReactContextBaseJavaModule implements Application.ActivityLifecycleCallbacks {

    private final String packageName;

    private Boolean iconChanged = false;

    public static final String NAME = "RNChangeIcon";
    public static final String MAIN_ACTVITY_BASE_NAME = ".MainActivity";

    private String componentClass = "";
    private final Set<String> classesToKill = new HashSet<>();

    public RNChangeIconModule(ReactApplicationContext reactContext, String packageName) {
        super(reactContext);
        this.packageName = packageName;
    }
    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void getIcon(Promise promise) {
        final Activity activity = getCurrentActivity();
        if (activity == null) {
            promise.reject("ACTIVITY_NOT_FOUND", "Activity was not found");
            return;
        }

        final String activityName = activity.getComponentName().getClassName();

        if (activityName.endsWith(MAIN_ACTVITY_BASE_NAME)) {
            promise.resolve("Default");
            return;
        }
        String[] activityNameSplit = activityName.split("MainActivity");
        if (activityNameSplit.length != 2) {
            promise.reject("ANDROID:UNEXPECTED_COMPONENT_CLASS:", this.componentClass);
            return;
        }
        promise.resolve(activityNameSplit[1]);
        return;
    }

    @ReactMethod
    public void changeIcon(String iconName, Promise promise) {

        final Activity activity = getCurrentActivity();

        if (activity == null) {
            promise.reject("ACTIVITY_NOT_FOUND", "The activity is null. Check if the app is running properly.");
            return;
        }
        if (iconName.isEmpty()) {
            promise.reject("EMPTY_ICON_STRING", "Icon name is missing i.e. changeIcon('YOUR_ICON_NAME_HERE')");
            return;
        }
        if (this.componentClass.isEmpty()) {
            this.componentClass = activity.getComponentName().getClassName(); // i.e. MyActivity
        }

        final String newIconName = (iconName == null || iconName.isEmpty()) ? "Default" : iconName;
        final String activeClass = this.packageName + MAIN_ACTVITY_BASE_NAME + newIconName;

        if (this.componentClass.equals(activeClass)) {
            promise.reject("ICON_ALREADY_USED", "This icons is the current active icon. " +  this.componentClass);
            return;
        }

        try {
            activity.getPackageManager().setComponentEnabledSetting(
                    new ComponentName(this.packageName, activeClass),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
            );
            promise.resolve(newIconName);
        } catch (Exception e) {
            promise.reject("ICON_INVALID", e.getLocalizedMessage());
            return;
        }

        this.classesToKill.add(this.componentClass);
        this.componentClass = activeClass;
        activity.getApplication().registerActivityLifecycleCallbacks(this);
//        completeIconChange(); // This then makes the current active class disabled
        iconChanged = true;
    }

    private void completeIconChange() {
        if (!iconChanged)
            return;
        final Activity activity = getCurrentActivity();
        if (activity == null)
            return;

        classesToKill.remove(componentClass);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            classesToKill.forEach((cls) -> activity.getPackageManager().setComponentEnabledSetting(
                    new ComponentName(this.packageName, cls),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP));
        }
        classesToKill.clear();
        iconChanged = false;
    }

    @Override
    public void onActivityPaused(Activity activity) {
         completeIconChange();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
