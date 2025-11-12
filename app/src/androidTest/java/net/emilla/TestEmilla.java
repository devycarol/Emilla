package net.emilla;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class TestEmilla {

    @Test
    public void testApplicationId() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals(BuildConfig.APPLICATION_ID, appContext.getPackageName());
    }

}