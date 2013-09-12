// Copyright (c) 2012 The Chromium Authors. All rights reserved.
// Copyright (c) 2013 Intel Corporation. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.xwalk.core.xwview.test;

import android.app.Activity;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import java.io.InputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.Timer;

import org.chromium.base.test.util.InstrumentationUtils;
import org.chromium.content.browser.LoadUrlParams;
import org.chromium.content.browser.test.util.CallbackHelper;
import org.xwalk.core.XWalkContent;
import org.xwalk.core.XWalkView;

public class XWalkViewTestBase
       extends ActivityInstrumentationTestCase2<XWalkViewTestRunnerActivity> {
    protected final static int WAIT_TIMEOUT_SECONDS = 15;
    private XWalkView mXWalkView;
    final TestXWalkViewContentsClient mTestContentsClient = new TestXWalkViewContentsClient();

    public XWalkViewTestBase() {
        super(XWalkViewTestRunnerActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Must call getActivity() here but not in main thread.
        final Activity activity = getActivity();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mXWalkView = new XWalkView(activity, activity);
                getActivity().addView(mXWalkView);
                mXWalkView.getXWalkViewContentForTest().installWebContentsObserverForTest(mTestContentsClient);
            }
        });
    }

    protected void loadUrlSync(final String url) throws Exception {
        CallbackHelper pageFinishedHelper = mTestContentsClient.getOnPageFinishedHelper();
        int currentCallCount = pageFinishedHelper.getCallCount();
        loadUrlAsync(url);

        pageFinishedHelper.waitForCallback(currentCallCount, 1, WAIT_TIMEOUT_SECONDS,
                TimeUnit.SECONDS);
    }

    protected void loadUrlAsync(final String url) throws Exception {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mXWalkView.loadUrl(url);
            }
        });
    }

    protected void postUrlSync(final String url, byte[] postData) throws Exception {
        CallbackHelper pageFinishedHelper = mTestContentsClient.getOnPageFinishedHelper();
        int currentCallCount = pageFinishedHelper.getCallCount();
        postUrlAsync(url, postData);
        pageFinishedHelper.waitForCallback(currentCallCount, 1, WAIT_TIMEOUT_SECONDS,
                TimeUnit.SECONDS);
    }

    protected void postUrlAsync(final String url, byte[] postData) throws Exception {
        class PostUrl implements Runnable {
            byte[] mPostData;
            public PostUrl(byte[] postData) {
                mPostData = postData;
            }
            @Override
            public void run() {
                mXWalkView.getXWalkViewContentForTest().getContentViewCoreForTest(
                        ).loadUrl(LoadUrlParams.createLoadHttpPostParams(url, mPostData));
            }
        }
        getInstrumentation().runOnMainSync(new PostUrl(postData));
    }

    protected void loadDataSync(final String data, final String mimeType,
            final boolean isBase64Encoded) throws Exception {
        CallbackHelper pageFinishedHelper = mTestContentsClient.getOnPageFinishedHelper();
        int currentCallCount = pageFinishedHelper.getCallCount();
        loadDataAsync(data, mimeType, isBase64Encoded);
        pageFinishedHelper.waitForCallback(currentCallCount, 1, WAIT_TIMEOUT_SECONDS,
                TimeUnit.SECONDS);
    }

    protected void loadDataAsync(final String data, final String mimeType,
             final boolean isBase64Encoded) throws Exception {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mXWalkView.getXWalkViewContentForTest().getContentViewCoreForTest(
                        ).loadUrl(LoadUrlParams.createLoadDataParams(
                        data, mimeType, isBase64Encoded));
            }
        });
    }

    protected String getTitleOnUiThread() throws Exception {
        return runTestOnUiThreadAndGetResult(new Callable<String>() {
            @Override
            public String call() throws Exception {
                XWalkContent xWalkContent = mXWalkView.getXWalkViewContentForTest();
                String title = xWalkContent.getContentViewCoreForTest().getTitle();
                return title;
            }
        });
    }

    protected <R> R runTestOnUiThreadAndGetResult(Callable<R> callable)
            throws Exception {
        FutureTask<R> task = new FutureTask<R>(callable);
        getInstrumentation().waitForIdleSync();
        getInstrumentation().runOnMainSync(task);
        return task.get();
    }

    protected String getFileContent(String fileName) {
        try {
            Context context = getInstrumentation().getContext();
            InputStream inputStream = context.getAssets().open(fileName);
            int size = inputStream.available();
            byte buffer[] = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String fileContent = new String(buffer);
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void loadAssetFile(String fileName) throws Exception {
        String fileContent = getFileContent(fileName);
        loadDataSync(fileContent, "text/html", false);
    }


    protected boolean canGoBackOnUiThread() throws Throwable {
        return InstrumentationUtils.runOnMainSyncAndGetResult(
                getInstrumentation(), new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return mXWalkView.canGoBack();
            }
        });
    }

    protected boolean canGoForwardOnUiThread() throws Throwable {
        return InstrumentationUtils.runOnMainSyncAndGetResult(
                getInstrumentation(), new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return mXWalkView.canGoForward();
            }
        });
    }

    protected XWalkView getXWalkView() {
        return mXWalkView;
    }
}
