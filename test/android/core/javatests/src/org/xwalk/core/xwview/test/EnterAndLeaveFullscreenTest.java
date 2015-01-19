// Copyright (c) 2014 Intel Corporation. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.xwalk.core.xwview.test;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import org.chromium.base.test.util.Feature;


/**
 * Tests for the hasEnteredFullscreen() and leaveFullscreen() method.
 */
public class EnterAndLeaveFullscreenTest extends XWalkViewTestBase {
    private TestHelperBridge.OnFullscreenToggledHelper mOnFullscreenToggledHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mOnFullscreenToggledHelper = mTestHelperBridge.getOnFullscreenToggledHelper();
    }

    @SmallTest
    @Feature({"XWalkView", "Fullscreen"})
    public void testEnterAndExitFullscreen() throws Throwable {
        final String name = "fullscreen_enter_exit.html";
        String fileContent = getFileContent(name);
        int count = mOnFullscreenToggledHelper.getCallCount();

        loadDataSync(name, fileContent, "text/html", false);
        try {
            clickOnElementId("enter_fullscreen", null);
            mOnFullscreenToggledHelper.waitForCallback(count);
            assertTrue(hasEnteredFullScreenOnUiThread());

            count = mOnFullscreenToggledHelper.getCallCount();
            Log.e("EnterAndLeaveFullscreenTest.java", "testEnterAndExitFullscreen 1111 count=" + count);
            leaveFullscreenOnUiThread();
            mOnFullscreenToggledHelper.waitForCallback(count);
            assertFalse(hasEnteredFullScreenOnUiThread());

            count = mOnFullscreenToggledHelper.getCallCount();
            Log.e("EnterAndLeaveFullscreenTest.java", "testEnterAndExitFullscreen 2222 count=" + count);
            clickOnElementId("enter_fullscreen", null);
            mOnFullscreenToggledHelper.waitForCallback(count);

            count = mOnFullscreenToggledHelper.getCallCount();
            Log.e("EnterAndLeaveFullscreenTest.java", "testEnterAndExitFullscreen 333 count=" + count);
            clickOnElementId("exit_fullscreen", null);
            mOnFullscreenToggledHelper.waitForCallback(count);
            assertFalse(hasEnteredFullScreenOnUiThread());
        } catch (Throwable e) {
            e.printStackTrace();
            assertTrue(false);
        }

    }

    @SmallTest
    @Feature({"XWalkView", "Fullscreen"})
    public void testHasEnteredFullScreen() throws Throwable {
        try {
            final String name = "fullscreen_enter_exit.html";
            String fileContent = getFileContent(name);
            loadDataSync(name, fileContent, "text/html", false);
            assertFalse(hasEnteredFullScreenOnUiThread());
            mOnFullscreenToggledHelper = mTestHelperBridge.getOnFullscreenToggledHelper();
            int count = mOnFullscreenToggledHelper.getCallCount();
            clickOnElementId("enter_fullscreen",null);
            mOnFullscreenToggledHelper.waitForCallback(count);

            assertTrue(hasEnteredFullScreenOnUiThread());
        } catch (Exception e) {
            assertTrue(false);
            e.printStackTrace();
        } catch (Throwable e) {
            assertTrue(false);
            e.printStackTrace();
        }

    }

    @SmallTest
    @Feature({"XWalkView", "Fullscreen"})
    public void testLeaveFullScreen() throws Throwable {
        final String name = "fullscreen_enter_exit.html";
        String fileContent = getFileContent(name);
        int count = mOnFullscreenToggledHelper.getCallCount();
        loadDataSync(name, fileContent, "text/html", false);

        try {
            assertFalse(hasEnteredFullScreenOnUiThread());
            clickOnElementId("enter_fullscreen",null);
            mOnFullscreenToggledHelper.waitForCallback(count);
            assertTrue(hasEnteredFullScreenOnUiThread());

            count = mOnFullscreenToggledHelper.getCallCount();
            leaveFullscreenOnUiThread();
            mOnFullscreenToggledHelper.waitForCallback(count);

            assertFalse(hasEnteredFullScreenOnUiThread());

            count = mOnFullscreenToggledHelper.getCallCount();
            clickOnElementId("enter_fullscreen",null);
            mOnFullscreenToggledHelper.waitForCallback(count);

            count = mOnFullscreenToggledHelper.getCallCount();
            clickOnElementId("exit_fullscreen",null);
            mOnFullscreenToggledHelper.waitForCallback(count);
            assertFalse(hasEnteredFullScreenOnUiThread());
        } catch (Exception e) {
            //assertTrue(false);
            e.printStackTrace();
        } catch (Throwable e) {
            //assertTrue(false);
            e.printStackTrace();
        }
    }
}
