// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#ifndef XWALK_RUNTIME_BROWSER_ANDROID_XWALK_AUTOFILL_CLIENT_H_
#define XWALK_RUNTIME_BROWSER_ANDROID_XWALK_AUTOFILL_CLIENT_H_

#include <jni.h>
#include <vector>

#include "base/android/jni_weak_ref.h"
#include "base/basictypes.h"
#include "base/compiler_specific.h"
#include "base/prefs/pref_registry_simple.h"
#include "base/prefs/pref_service_factory.h"
#include "components/autofill/core/browser/autofill_client.h"
#include "content/public/browser/web_contents_user_data.h"

namespace autofill {
class AutofillMetrics;
class AutofillPopupDelegate;
class CardUnmaskDelegate;
class CreditCard;
class FormStructure;
class PasswordGenerator;
class PersonalDataManager;
struct FormData;
}

namespace content {
class WebContents;
}

namespace gfx {
class RectF;
}

class PersonalDataManager;
class PrefService;

namespace xwalk {

// Manager delegate for the autofill functionality. XWalkView
// supports enabling autocomplete feature for each XWalkView instance
// (different than the browser which supports enabling/disabling for
// a profile). Since there is only one pref service for a given browser
// context, we cannot enable this feature via UserPrefs. Rather, we always
// keep the feature enabled at the pref service, and control it via
// the delegates.
class XWalkAutofillClient
    : public autofill::AutofillClient,
      public content::WebContentsUserData<XWalkAutofillClient> {
 public:
  virtual ~XWalkAutofillClient();

  void SetSaveFormData(bool enabled);
  bool GetSaveFormData();

  // AutofillClient:
  virtual autofill::PersonalDataManager* GetPersonalDataManager() override;
  virtual scoped_refptr<autofill::AutofillWebDataService> GetDatabase()
      override;
  virtual PrefService* GetPrefs() override;
  virtual void HideRequestAutocompleteDialog() override;
  virtual void ShowAutofillSettings() override;
  virtual void ShowUnmaskPrompt(
      const autofill::CreditCard& card,
      base::WeakPtr<autofill::CardUnmaskDelegate> delegate) override;
  virtual void OnUnmaskVerificationResult(bool success) override;
  virtual void ConfirmSaveCreditCard(
      const base::Closure& save_card_callback) override;
  virtual bool HasCreditCardScanFeature() override;
  virtual void ScanCreditCard(const CreditCardScanCallback& callback) override;
  virtual void ShowRequestAutocompleteDialog(
      const autofill::FormData& form,
      content::RenderFrameHost* rfh,
      const ResultCallback& callback) override;
  virtual void ShowAutofillPopup(
      const gfx::RectF& element_bounds,
      base::i18n::TextDirection text_direction,
      const std::vector<autofill::Suggestion>& suggestions,
      base::WeakPtr<autofill::AutofillPopupDelegate> delegate) override;
  virtual void UpdateAutofillPopupDataListValues(
      const std::vector<base::string16>& values,
      const std::vector<base::string16>& labels) override;
  virtual void HideAutofillPopup() override;
  virtual bool IsAutocompleteEnabled() override;
  virtual void DetectAccountCreationForms(
      content::RenderFrameHost* rfh,
      const std::vector<autofill::FormStructure*>& forms) override;
  virtual void DidFillOrPreviewField(
      const base::string16& autofilled_value,
      const base::string16& profile_full_name) override;
  virtual void OnFirstUserGestureObserved() override;

  void SuggestionSelected(JNIEnv* env, jobject obj, jint position);

 private:
  explicit XWalkAutofillClient(content::WebContents* web_contents);
  friend class content::WebContentsUserData<XWalkAutofillClient>;

  void ShowAutofillPopupImpl(
      const gfx::RectF& element_bounds,
      bool is_rtl,
      const std::vector<autofill::Suggestion>& suggestions);

  // The web_contents associated with this delegate.
  content::WebContents* web_contents_;
  bool save_form_data_;
  JavaObjectWeakGlobalRef java_ref_;

  // The current Autofill query values.
  std::vector<autofill::Suggestion> suggestions_;
  base::WeakPtr<autofill::AutofillPopupDelegate> delegate_;

  DISALLOW_COPY_AND_ASSIGN(XWalkAutofillClient);
};

bool RegisterXWalkAutofillClient(JNIEnv* env);

}  // namespace xwalk

#endif  // XWALK_RUNTIME_BROWSER_ANDROID_XWALK_AUTOFILL_CLIENT_H_
