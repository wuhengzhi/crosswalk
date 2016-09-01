// Copyright 2013 The Chromium Authors. All rights reserved.
// Copyright 2016 Intel Corporation. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#include "xwalk/runtime/browser/android/xwalk_web_preferences_populater_impl.h"

#include "xwalk/runtime/browser/android/xwalk_settings.h"

namespace xwalk {

XWalkWebPreferencesPopulaterImpl::XWalkWebPreferencesPopulaterImpl() {
}

XWalkWebPreferencesPopulaterImpl::~XWalkWebPreferencesPopulaterImpl() {
}

void XWalkWebPreferencesPopulaterImpl::PopulateFor(
    content::WebContents* web_contents,
    content::WebPreferences* web_prefs) {
  XWalkSettings* xwalk_settings = XWalkSettings::FromWebContents(web_contents);
  if (xwalk_settings) {
    xwalk_settings->PopulateWebPreferences(web_prefs);
  }
}

}  // namespace xwalk
