# PlayIt Music App - Debugging Guide

## ✅ Project Status: READY FOR DEBUGGING

The app is properly configured and ready to run in Android Studio. All critical components have been verified.

## 📋 Pre-Debugging Checklist

### ✅ Build Configuration
- [x] Gradle files are properly configured
- [x] All dependencies are correctly specified
- [x] AndroidManifest.xml is properly configured
- [x] Hilt is properly set up
- [x] Firebase is configured (google-services.json present)

### ✅ Code Structure
- [x] All imports are correct
- [x] Package structure is organized
- [x] Constants are centralized
- [x] Extension functions are properly placed
- [x] ViewModels use viewModelScope correctly

### ✅ Architecture
- [x] MVVM pattern is followed
- [x] Dependency injection (Hilt) is configured
- [x] Repository pattern is implemented
- [x] Separation of concerns is maintained

## 🚀 Running the App in Android Studio

### Step 1: Sync Gradle
1. Open Android Studio
2. Click **File → Sync Project with Gradle Files**
3. Wait for sync to complete

### Step 2: Clean and Rebuild
1. Click **Build → Clean Project**
2. Wait for clean to complete
3. Click **Build → Rebuild Project**
4. Wait for build to complete

### Step 3: Verify Module Configuration
1. Go to **Run → Edit Configurations**
2. Ensure the module is set to **app**
3. Application is set to **com.example.playit**
4. Main activity is **com.example.playit.MainActivity**

### Step 4: Run the App
1. Connect an Android device or start an emulator
2. Click **Run** button (green play icon) or press **Shift+F10**
3. Select your device/emulator
4. Wait for app to install and launch

## 🐛 Debugging Tips

### Common Issues and Solutions

#### Issue 1: "Module not specified" Error
**Solution:**
- Go to **Run → Edit Configurations**
- Select your run configuration
- Under **General** tab, ensure **Module** is set to **app**
- Click **Apply** and **OK**

#### Issue 2: Gradle Sync Errors
**Solution:**
- Click **File → Invalidate Caches / Restart**
- Select **Invalidate and Restart**
- After restart, sync Gradle again

#### Issue 3: Kotlin Daemon Connection Errors
**Solution:**
- Open terminal in Android Studio
- Run: `./gradlew --stop` (or `gradlew.bat --stop` on Windows)
- Restart Android Studio
- Sync Gradle again

#### Issue 4: Linter Shows False Errors
**Solution:**
- These are IDE indexing issues, not actual compilation errors
- Wait for indexing to complete
- If persists, do **File → Invalidate Caches / Restart**

#### Issue 5: App Crashes on Launch
**Check:**
- Ensure permissions are requested (READ_MEDIA_AUDIO, POST_NOTIFICATIONS)
- Check Logcat for actual error messages
- Verify Firebase configuration (google-services.json)
- Check if Hilt is properly initialized

### Debugging Features

#### Breakpoints
- Set breakpoints in your code by clicking on the line number
- Run in debug mode (Shift+F9)
- Use step over (F8), step into (F7), step out (Shift+F8)

#### Logcat
- Use `Log.d(Constants.LOG_TAG, "message")` for debugging
- Filter by tag: `PlayIt`
- Filter by log level: Debug, Info, Warning, Error

#### ViewModel Debugging
- ViewModels are accessible via Hilt
- Use breakpoints in ViewModel methods
- Observe StateFlows in the debugger

## 📱 Testing Checklist

### Music Playback
- [ ] Play song from list
- [ ] Pause/Resume functionality
- [ ] Skip to next song
- [ ] Skip to previous song
- [ ] Seek functionality
- [ ] Shuffle toggle
- [ ] Repeat toggle

### Permissions
- [ ] Audio permission granted
- [ ] Notification permission granted (Android 13+)
- [ ] Permission denied handling

### Firebase Authentication
- [ ] Login functionality
- [ ] Sign up functionality
- [ ] Logout functionality
- [ ] User session persistence

### Data Loading
- [ ] Local songs loading
- [ ] Online songs loading
- [ ] Favorite toggle
- [ ] Error handling

### UI Navigation
- [ ] Bottom navigation
- [ ] Screen navigation
- [ ] Now playing bar
- [ ] Player screen

## 🔍 Key Components to Monitor

### 1. MusicController
- **Location:** `com.example.playit.media.MusicControllerImpl`
- **Purpose:** Manages media playback
- **Debug:** Check Logcat for "PlayIt" tag

### 2. PlaybackService
- **Location:** `com.example.playit.media.PlaybackService`
- **Purpose:** Background music playback service
- **Debug:** Check service lifecycle in Logcat

### 3. MediaPlayerViewModel
- **Location:** `com.example.playit.viewmodel.MediaPlayerViewModel`
- **Purpose:** UI state management for player
- **Debug:** Use breakpoints and observe StateFlows

### 4. SongRepository
- **Location:** `com.example.playit.data.repository.SongRepository`
- **Purpose:** Data access layer
- **Debug:** Check Firestore queries and local file access

## 📊 Logging Strategy

All logging uses the centralized tag: `Constants.LOG_TAG` ("PlayIt")

**Examples:**
```kotlin
Log.d(Constants.LOG_TAG, "Debug message")
Log.e(Constants.LOG_TAG, "Error message", exception)
```

**Filter in Logcat:**
- Tag: `PlayIt`
- Level: Debug, Info, Warning, Error

## ⚠️ Known Limitations

1. **onTerminate()**: Not guaranteed to be called on modern Android
2. **Kotlin Daemon**: May need restart if connection issues occur
3. **IDE Indexing**: May show false errors until indexing completes

## 🎯 Next Steps

1. **Sync Gradle** in Android Studio
2. **Clean and Rebuild** the project
3. **Run the app** on a device or emulator
4. **Monitor Logcat** for any issues
5. **Test all functionalities** systematically

## 📝 Notes

- The app uses Hilt for dependency injection
- All constants are centralized in `Constants.kt`
- Extension functions are in `core/extensions/`
- Result types are available for error handling
- ViewModels use `viewModelScope` for coroutines

---

**Status:** ✅ **READY FOR DEBUGGING**

All code is properly structured, dependencies are configured, and the app is ready to run in Android Studio.

