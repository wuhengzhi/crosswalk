// Copyright (c) 2013 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#ifndef XWALK_RUNTIME_BROWSER_ANDROID_XWALK_SETTINGS_H_
#define XWALK_RUNTIME_BROWSER_ANDROID_XWALK_SETTINGS_H_

#include <jni.h>

#include <memory>

#include "base/android/jni_weak_ref.h"
#include "base/android/scoped_java_ref.h"
#include "components/prefs/pref_service.h"
#include "content/public/browser/web_contents_observer.h"

namespace content {
struct WebPreferences;
}

namespace xwalk {

class XWalkRenderViewHostExt;

class XWalkSettings : public content::WebContentsObserver {
 public:
  static XWalkSettings* FromWebContents(content::WebContents* web_contents);

  XWalkSettings(JNIEnv* env, jobject obj, content::WebContents* web_contents);
  ~XWalkSettings() override;

  // Called from Java.
  void Destroy(JNIEnv* env, const base::android::JavaParamRef<jobject>& obj);
  void ResetScrollAndScaleState(JNIEnv* env,
      const base::android::JavaParamRef<jobject>& obj);
  void UpdateEverythingLocked(JNIEnv* env,
      const base::android::JavaParamRef<jobject>& obj);
  void UpdateInitialPageScaleLocked(JNIEnv* env,
      const base::android::JavaParamRef<jobject>& obj);
  void UpdateUserAgentLocked(JNIEnv* env,
      const base::android::JavaParamRef<jobject>& obj);
  void UpdateWebkitPreferencesLocked(
      JNIEnv* env,
      const base::android::JavaParamRef<jobject>& obj);
  void UpdateAcceptLanguagesLocked(JNIEnv* env,
      const base::android::JavaParamRef<jobject>& obj);
  void UpdateFormDataPreferencesLocked(JNIEnv* env,
      const base::android::JavaParamRef<jobject>& obj);
  void PopulateWebPreferencesLocked(
      JNIEnv* env,
      const base::android::JavaParamRef<jobject>& obj,
      jlong web_prefs_ptr);

  void PopulateWebPreferences(content::WebPreferences* web_prefs);

 private:
  struct FieldIds;

  XWalkRenderViewHostExt* GetXWalkRenderViewHostExt();
  void UpdateEverything();
  void UpdatePreferredSizeMode();
  PrefService* GetPrefs();

  // WebContentsObserver overrides:
  void RenderViewCreated(
      content::RenderViewHost* render_view_host) override;

  // Java field references for accessing the values in the Java object.
  std::unique_ptr<FieldIds> field_ids_;

  JavaObjectWeakGlobalRef xwalk_settings_;
};

bool RegisterXWalkSettings(JNIEnv* env);

}  // namespace xwalk

#endif  // XWALK_RUNTIME_BROWSER_ANDROID_XWALK_SETTINGS_H_
