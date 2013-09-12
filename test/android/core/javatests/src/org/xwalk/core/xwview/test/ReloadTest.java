// Copyright (c) 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.xwalk.core.xwview.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.util.EncodingUtils;
import org.chromium.base.test.util.Feature;
import org.chromium.base.test.util.UrlUtils;
import org.chromium.content.browser.ContentViewCore;
import org.chromium.content.browser.test.util.TestCallbackHelperContainer;
import org.chromium.net.test.util.TestWebServer;

import org.xwalk.core.XWalkView;
import org.xwalk.core.XWalkClient;

/**
 * Tests for XWalkView.reload() method.
 */
public class ReloadTest extends XWalkViewTestBase {

    private class TestXWalkClient extends XWalkClient {
        // Number of times onFormResubmit is called.
        private int mResubmissions = 0;
        // Whether to resubmit Post data on reload.
        private boolean mResubmit = false;

        public int getResubmissions() {
            return mResubmissions;
        }

        public void setResubmit(boolean resubmit) {
            mResubmit = resubmit;
        }

        @Override
        public void onPageStarted(XWalkView view, String url, Bitmap favicon) {
            mTestContentsClient.onPageStarted(url);
        }

        @Override
        public void onPageFinished(XWalkView view, String url) {
            mTestContentsClient.didFinishLoad(url);
        }

        @Override
        public void onFormResubmission(XWalkView view, Message dontResend, Message resend) {
            mResubmissions++;

            if (mResubmit) {
                resend.sendToTarget();
            } else {
                dontResend.sendToTarget();
            }
        }
    }

    // Server responses for load and reload of posts.
    private static final String LOAD_RESPONSE =
            "<html><head><title>Load</title></head><body>HELLO</body></html>";
    private static final String RELOAD_RESPONSE =
            "<html><head><title>Reload</title></head><body>HELLO</body></html>";

    // Server timeout in seconds. Used to detect dontResend case.
    private static final int TIMEOUT = 3;

    // The web server.
    private TestWebServer mServer;

    TestXWalkClient mXWalkClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mXWalkClient = new TestXWalkClient();
        getXWalkView().setXWalkClient(mXWalkClient);
        mServer = new TestWebServer(false);
    }

    @Override
    public void tearDown() throws Exception {
        mServer.shutdown();
        super.tearDown();
    }

    protected void doReload() throws Throwable {
        String url = mServer.setResponse("/form", LOAD_RESPONSE, null);
        String postData = "content=blabla";
        byte[] data = EncodingUtils.getBytes(postData, "BASE64");

        postUrlSync(url, data);
        assertEquals(0, mXWalkClient.getResubmissions());
        assertEquals("Load", getTitleOnUiThread());
        // Verify reload works as expected.
        mServer.setResponse("/form", RELOAD_RESPONSE, null);
        TestCallbackHelperContainer.OnPageFinishedHelper onPageFinishedHelper =
                mTestContentsClient.getOnPageFinishedHelper();
        int callCount = onPageFinishedHelper.getCallCount();
        reload();
        try {
            // Wait for page finished callback, or a timeout. A timeout is necessary
            // to detect a dontResend response.
            onPageFinishedHelper.waitForCallback(callCount, 1, TIMEOUT, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
        }
    }

    private void reload() throws Throwable {
        // Run reload on UI thread.
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getXWalkView().reload();;
            }
        });
    }

    @SmallTest
    @Feature({"XWalkView", "Navigation"})
    public void testResend() throws Throwable {
        mXWalkClient.setResubmit(true);
        doReload();
        assertEquals(1, mXWalkClient.getResubmissions());
        assertEquals("Reload", getTitleOnUiThread());
    }

    @SmallTest
    @Feature({"XWalkView", "Navigation"})
    public void testDontResend() throws Throwable {
        mXWalkClient.setResubmit(false);
        doReload();
        assertEquals(1, mXWalkClient.getResubmissions());
        assertEquals("Load", getTitleOnUiThread());
    }

    @SmallTest
    @Feature({"Navigation"})
    public void testPageReload() throws Throwable {
        final String HTML_LOADTIME = "<html><head>" +
                "<script type=\"text/javascript\">var loadTimestamp = new Date().getTime();" +
                "function getLoadtime() { return loadTimestamp; }</script></head></html>";
        final String URL_LOADTIME = UrlUtils.encodeHtmlDataUri(HTML_LOADTIME);

        loadUrlSync(URL_LOADTIME);

        TestCallbackHelperContainer.OnEvaluateJavaScriptResultHelper javascriptHelper =
                mTestContentsClient.getOnEvaluateJavaScriptResultHelper();

        ContentViewCore contentViewCore = getXWalkView().getXWalkViewContentForTest(
                ).getContentViewCoreForTest();
        // Grab the first timestamp.
        javascriptHelper.evaluateJavaScript(contentViewCore, "getLoadtime();");
        javascriptHelper.waitUntilHasValue();
        String firstTimestamp = javascriptHelper.getJsonResultAndClear();
        assertNotNull("Timestamp was null.", firstTimestamp);

        // Grab the timestamp after a reload and make sure they don't match.
        reload();
        javascriptHelper.evaluateJavaScript(contentViewCore, "getLoadtime();");
        javascriptHelper.waitUntilHasValue();
        String secondTimestamp = javascriptHelper.getJsonResultAndClear();
        assertNotNull("Timestamp was null.", secondTimestamp);
        assertFalse("Timestamps matched.", firstTimestamp.equals(secondTimestamp));
    }
}

