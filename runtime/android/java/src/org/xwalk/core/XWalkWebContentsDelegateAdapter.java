// Copyright 2013 The Chromium Authors. All rights reserved.
// Copyright (c) 2013 Intel Corporation. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.xwalk.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import org.chromium.content.browser.ContentViewCore;

public class XWalkWebContentsDelegateAdapter extends XWalkWebContentsDelegate {

    private XWalkContentsClient mXWalkContentsClient;

    public XWalkWebContentsDelegateAdapter(XWalkContentsClient client) {
        mXWalkContentsClient = client;
    }

    @Override
    public void onLoadProgressChanged(int progress) {
        if (mXWalkContentsClient != null)
            mXWalkContentsClient.onProgressChanged(progress);
    }

    @Override
    public boolean addNewContents(boolean isDialog, boolean isUserGesture) {
        // TODO: implement.
        return false;
    }

    @Override
    public void closeContents() {
        // TODO: implement.
    }

    @Override
    public void activateContents() {
        // TODO: implement.
    }

    @Override
    public void showRepostFormWarningDialog(final ContentViewCore contentViewCore) {
        // TODO(mkosiba) We should be using something akin to the JsResultReceiver as the
        // callback parameter (instead of ContentViewCore) and implement a way of converting
        // that to a pair of messages.
        final int MSG_CONTINUE_PENDING_RELOAD = 1;
        final int MSG_CANCEL_PENDING_RELOAD = 2;

        // TODO(sgurun) Remember the URL to cancel the reload behavior
        // if it is different than the most recent NavigationController entry.
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case MSG_CONTINUE_PENDING_RELOAD: {
                        contentViewCore.continuePendingReload();
                        break;
                    }
                    case MSG_CANCEL_PENDING_RELOAD: {
                        contentViewCore.cancelPendingReload();
                        break;
                    }
                    default:
                        throw new IllegalStateException(
                                "WebContentsDelegateAdapter: unhandled message " + msg.what);
                }
            }
        };

        Message resend = handler.obtainMessage(MSG_CONTINUE_PENDING_RELOAD);
        Message dontResend = handler.obtainMessage(MSG_CANCEL_PENDING_RELOAD);
        mXWalkContentsClient.onFormResubmission(dontResend, resend);
    }
}
