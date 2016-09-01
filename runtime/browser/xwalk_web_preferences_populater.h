// Copyright 2013 The Chromium Authors. All rights reserved.
// Copyright 2016 Intel Corporation. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#ifndef XWALK_RUNTIME_BROWSER_XWALK_WEB_PREFERENCES_POPULATER_H_
#define XWALK_RUNTIME_BROWSER_XWALK_WEB_PREFERENCES_POPULATER_H_

namespace content {
class WebContents;
struct WebPreferences;
}

namespace xwalk {

// Empty base class so this can be destroyed by XWalkContentBrowserClient.
class XWalkWebPreferencesPopulater {
 public:
  virtual ~XWalkWebPreferencesPopulater();

  virtual void PopulateFor(content::WebContents* web_contents,
                           content::WebPreferences* web_prefs) = 0;
};

}  // namespace xwalk

#endif  // XWALK_RUNTIME_BROWSER_XWALK_WEB_PREFERENCES_POPULATER_H_
