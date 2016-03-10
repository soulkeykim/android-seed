package com.{{company_name}}.android.{{app_package_name_prefix}};

import com.google.android.gms.tagmanager.TagManager;

import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;

/**
 * App class used to run Robolectric tests.
 */
@SuppressWarnings("unused")
public class Test{{app_class_prefix}}App extends {{app_class_prefix}}App {

    @Override
    void enableDebugTools() {
        // Not whilst running tests
    }

    @Override
    void enableAppOnlyFunctionality() {
        // Not whilst running tests
    }

    @Override
    protected TagManager getTagManager() {
        return mock(TagManager.class, RETURNS_MOCKS);
    }
}