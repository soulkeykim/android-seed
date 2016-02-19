package com.{{company_name}}.android.{{app_package_name_prefix}};

import android.app.Application;
import android.os.StrictMode;
import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import io.fabric.sdk.android.Fabric;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Cache;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.{{company_name}}.android.{{app_package_name_prefix}}.module.AppServicesComponent;
import com.{{company_name}}.android.{{app_package_name_prefix}}.module.DaggerAppServicesComponent;
import com.{{company_name}}.android.{{app_package_name_prefix}}.module.{{app_class_prefix}}Module;
import com.{{company_name}}.android.{{app_package_name_prefix}}.util.Api;
import com.{{company_name}}.android.{{app_package_name_prefix}}.util.CrashlyticsLogger;
import com.google.android.gms.tagmanager.TagManager;

import timber.log.Timber;

/**
 *
 */
public class {{app_class_prefix}}App extends Application {

    private AppServicesComponent mAppServicesComponent;

    private RefWatcher mRefWatcher = RefWatcher.DISABLED;

    private TagManager mTagManager;

    Cache mPicassoImageCache;

    @Override
    public void onCreate() {
        super.onCreate();

        mTagManager = getTagManager();

        if (BuildConfig.DEBUG) {
            enableDebugTools();
        }

        enableAppOnlyFunctionality();

        mAppServicesComponent = DaggerAppServicesComponent.builder()
                .{{app_class_prefix_lowercase}}Module(getModule(mTagManager))
                .build();
    }

    /**
     * Extracts application instance from context
     */
    public static {{app_class_prefix}}App from(@NonNull Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        return ({{app_class_prefix}}App) context.getApplicationContext();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= TRIM_MEMORY_UI_HIDDEN) {
            Timber.d("Android is suggesting to trim memory .. clearing picasso cache. Level = %s", level);
            // Clear our picasso cache
            mPicassoImageCache.clear();
        }
    }

    {{app_class_prefix}}Module getModule(TagManager tagManager) {
        return new {{app_class_prefix}}Module(this, tagManager);
    }

    /**
     * So we can return a mock in the test application
     */
    protected TagManager getTagManager() {
        return TagManager.getInstance(this);
    }

    void enableAppOnlyFunctionality() {
        if (BuildConfig.CRASHLYTICS_ENABLED) {
            Fabric.with(this, new Crashlytics(), new Answers());
            Timber.plant(new CrashlyticsLogger());
        }

        int container = getResources().getIdentifier(BuildConfig.GTM_BINARY_NAME, "raw", getPackageName());
        if (container > 0) {
            mTagManager.loadContainerPreferFresh(BuildConfig.GTM_CONTAINER_ID, container);
        }

        createPicassoCache();
    }

    /**
     * Enables all debug-only functionality.
     * <p/>
     * Extract into a method to allow overriding in other modules/tests
     */
    void enableDebugTools() {
        Timber.plant(new Timber.DebugTree());

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());

        StrictMode.enableDefaults();

        if (BuildConfig.LEAK_CANARY_ENABLED && Api.isUpTo(Api.LOLLIPOP)) {
            // LeakCanary causes a crash on M Developer Preview
            mRefWatcher = LeakCanary.install(this);
        }
    }

    /**
     * Installs a custom picasso instance with a memory cache that can be controlled
     */
    void createPicassoCache() {
        Picasso.setSingletonInstance(new Picasso.Builder(this)
                .memoryCache(mPicassoImageCache = new LruCache(this))
                .build());
    }

     /**
     * @return an {@link AppServicesComponent} which holds all the necessary dependencies
     * other application components may want to use for injection purposes
     */
    public AppServicesComponent getAppServicesComponent() {
        return mAppServicesComponent;
    }

    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }
}
