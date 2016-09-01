// Copyright 2013 The Chromium Authors. All rights reserved.
// Copyright 2016 Intel Corporation. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#ifndef XWALK_RUNTIME_BROWSER_ANDROID_XWALK_WEB_PREFERENCES_POPULATER_IMPL_H_
#define XWALK_RUNTIME_BROWSER_ANDROID_XWALK_WEB_PREFERENCES_POPULATER_IMPL_H_

#include "base/compiler_specific.h"
#include "xwalk/runtime/browser/xwalk_web_preferences_populater.h"

namespace xwalk {

class XWalkSettings;

class XWalkWebPreferencesPopulaterImpl : public XWalkWebPreferencesPopulater {
 public:
  XWalkWebPreferencesPopulaterImpl();
  ~XWalkWebPreferencesPopulaterImpl() override;

  // XWalkWebPreferencesPopulater
  void PopulateFor(content::WebContents* web_contents,
                   content::WebPreferences* web_prefs) override;
};

}  // namespace xwalk

#endif  // XWALK_RUNTIME_BROWSER_ANDROID_XWALK_WEB_PREFERENCES_POPULATER_IMPL_H_
