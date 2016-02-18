package com.australiansuper.android.australiansuper;

import com.australiansuper.android.australiansuper.data.util.StubServerUtils;
import com.byoutline.mockserver.NetworkType;
import com.google.android.gms.tagmanager.TagManager;

import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;

public class Test{{app_class_prefix}}App extends {{app_class_prefix}}App {
    @Override
    protected TagManager getTagManager() {
        return mock(TagManager.class, RETURNS_MOCKS);
    }
}