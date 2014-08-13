// Copyright (c) 2014 Intel Corporation. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.xwalk.core.xwview.test;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import org.chromium.base.test.util.Feature;

/**
 * Test suite for OnScaleChanged().
 */
public class OnScaleChangedTest extends XWalkViewTestBase {
    private TestHelperBridge.OnScaleChangedHelper mOnScaleChangedHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mOnScaleChangedHelper = mTestHelperBridge.getOnScaleChangedHelper();
    }

    @SmallTest
    @Feature({"OnScaleChanged"})
    public void testOnScaleChanged() throws Throwable {
        final String name = "scale_changed.html";
        String fileContent = getFileContent(name);
        int count = mOnScaleChangedHelper.getCallCount();

        loadDataAsync(null, fileContent, "text/html", false);
        //clickOnElementId("fullscreen_toggled", null);
        mOnScaleChangedHelper.waitForCallback(count);
        Log.e("OnScaleChangedTest.java", "scale=" + mOnScaleChangedHelper.getScale());
        //assertTrue(1.5 == mOnScaleChangedHelper.getScale());
        Thread.sleep(5000);
        Log.e("OnScaleChangedTest.java", "scale=" + mOnScaleChangedHelper.getScale());
    }
}
