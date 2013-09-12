// Copyright (c) 2012 The Chromium Authors. All rights reserved.
// Copyright (c) 2013 Intel Corporation. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.xwalk.core.xwview.test;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import org.chromium.base.test.util.DisabledTest;
import org.chromium.base.test.util.Feature;
import org.chromium.base.test.util.UrlUtils;

/**
 * Tests for XWalkView.clearHistory() method.
 */
public class ClearHistoryTest extends XWalkViewTestBase {
    private static final String[] URLS = new String[3];
    {
        for (int i = 0; i < URLS.length; i++) {
            URLS[i] = UrlUtils.encodeHtmlDataUri(
                    "<html><head></head><body>" + i + "</body></html>");
        }
    }

    protected void clearHistoryOnUiThread() throws Exception {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getXWalkView().clearHistory();
            }
        });
    }

    /*
    @SmallTest
    @Feature({"History", "Main"})
    This test is only failing on JellyBean bots.
    See crbug.com/178762.
    */
    @DisabledTest
    public void testClearHistory() throws Throwable {
        for (int i = 0; i < URLS.length; i++) {
            loadUrlSync(URLS[i]);
        }

        getXWalkView().goBack();
        assertTrue("Should be able to go back", canGoBackOnUiThread());
        assertTrue("Should be able to go forward", canGoForwardOnUiThread());

        clearHistoryOnUiThread();
        assertFalse("Should be able to go back", canGoBackOnUiThread());
        assertFalse("Should be able to go forward", canGoForwardOnUiThread());
    }
}
