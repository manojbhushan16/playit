# Fix Google Sign-In Issue

## Problem Identified

Your `google-services.json` for `com.example.playit` is **missing an Android OAuth Client ID** (type 1). 

Currently, it only has:
- ✅ Web Client ID (type 3): `299849895582-qpu3e721tccckqmi1o5rhrldmp5206c0.apps.googleusercontent.com`

But Google Sign-In One Tap requires:
- ✅ Web Client ID (type 3) - You have this
- ❌ Android OAuth Client ID (type 1) - **MISSING**

## Why This Causes the Issue

When you click "Sign in with Google":
1. The app tries to show Google Sign-In One Tap
2. Google needs an Android OAuth Client to verify your app
3. Without it, Google Sign-In fails immediately
4. That's why you see "Google Sign-In failed" immediately

## Solution: Add Android OAuth Client ID in Firebase Console

### Step 1: Get Your SHA-1 Fingerprint

**Option A: Using Gradle (Recommended)**
```bash
cd PlayIt
.\gradlew signingReport
```

Look for SHA1 under `Variant: debug` section. Copy the SHA-1 value.

**Option B: Using PowerShell**
```powershell
cd PlayIt
.\get-sha1.ps1
```

**Option C: Using keytool**
```bash
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
```

Copy the SHA-1 value (looks like: `XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX`)

### Step 2: Add SHA-1 to Firebase Console

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: **playbeat2**
3. Click the **⚙️ (gear icon)** → **Project settings**
4. Scroll down to **Your apps** section
5. Find your Android app: **com.example.playit**
6. Click **Add fingerprint** button
7. Paste your SHA-1 fingerprint
8. Click **Save**

### Step 3: Download Updated google-services.json

1. Still in Firebase Console → Project Settings
2. In **Your apps** section, find **com.example.playit**
3. Click **Download google-services.json**
4. Replace `PlayIt/app/google-services.json` with the new file

### Step 4: Verify Google Sign-In API is Enabled

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select project: **playbeat2**
3. Go to **APIs & Services** → **Library**
4. Search for **"Google Sign-In API"**
5. Click on it and make sure it's **ENABLED**
6. If not, click **Enable**

### Step 5: Rebuild the App

1. In Android Studio: **Build → Clean Project**
2. **Build → Rebuild Project**
3. Run the app again

## Alternative: Quick Test (Use Existing Client)

If you want to test quickly without adding SHA-1, you can temporarily use the Android Client ID from the first app (`com.example.playbeat`), but this is **NOT recommended** for production.

The Android Client ID from `com.example.playbeat` is:
- `299849895582-vmoqam6imgb80g0d434ovc6v4ocdmfnh.apps.googleusercontent.com`

But this won't work properly because the package names don't match.

## What Changed in Code

I've updated the code to:
1. ✅ Show detailed error messages (you'll see the actual error now)
2. ✅ Better logging for debugging
3. ✅ Improved error handling
4. ✅ Loading state management

## Expected Result After Fix

After adding SHA-1 and downloading the updated `google-services.json`:
1. ✅ Google Sign-In button will show the account picker
2. ✅ You can select your Google account
3. ✅ Sign-in will complete successfully
4. ✅ You'll be logged into Firebase

## Common Error Messages

- **"10:"** - SHA-1 fingerprint not registered or wrong
- **"12500:"** - Google Sign-In API not enabled
- **"12501:"** - Sign-in was canceled by user
- **"DEVELOPER_ERROR"** - Wrong client ID or configuration

## Verification Checklist

- [ ] SHA-1 fingerprint added to Firebase Console
- [ ] Updated `google-services.json` downloaded and replaced
- [ ] Google Sign-In API enabled in Google Cloud Console
- [ ] App rebuilt after changes
- [ ] Tested Google Sign-In button

---

**Note:** The email/password login works because it doesn't require Android OAuth Client - it only uses Firebase Auth directly. Google Sign-In requires both Web Client ID and Android OAuth Client ID.

