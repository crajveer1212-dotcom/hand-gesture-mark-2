# 🚀 Build Instructions for Hand Gesture Mark 2

## Quick Build on Your Computer

### Prerequisites:
- Android Studio installed
- Java 17 or higher

### Steps:

1. **Clone the repository:**
```bash
cd C:\Users\USR\Desktop
git clone https://github.com/crajveer1212-dotcom/hand-gesture-mark-2.git
cd hand-gesture-mark-2
```

2. **Download MediaPipe Model:**
```bash
mkdir -p app\src\main\assets
curl -L "https://storage.googleapis.com/mediapipe-models/hand_landmarker/hand_landmarker/float16/latest/hand_landmarker.task" -o app\src\main\assets\hand_landmarker.task
```

Or download manually:
- Go to: https://storage.googleapis.com/mediapipe-models/hand_landmarker/hand_landmarker/float16/latest/hand_landmarker.task
- Save to: `app/src/main/assets/hand_landmarker.task`

3. **Open in Android Studio:**
- File → Open
- Select the `hand-gesture-mark-2` folder
- Wait for Gradle sync (2-3 minutes)

4. **Build APK:**
- Build → Generate Signed Bundle / APK
- Select APK
- Click Next
- Click "Create new..." if you don't have a keystore (or skip signing for debug)
- Click Finish

OR simply:
- Build → Build Bundle(s) / APK(s) → Build APK(s)

5. **Find your APK:**
```
app\build\outputs\apk\debug\app-debug.apk
```

### Troubleshooting:

**If Gradle sync fails:**
- File → Invalidate Caches / Restart
- Try again

**If model download fails:**
- Download manually from the URL above
- Place in `app/src/main/assets/` folder

**If build fails:**
- Check that Java 17 is installed
- Check that Android SDK is installed
- Try: Build → Clean Project, then rebuild

### Alternative - Use Command Line:

```bash
# On Windows
gradlew.bat assembleDebug

# On Mac/Linux
./gradlew assembleDebug
```

APK will be in: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📱 After Building:

1. Transfer APK to your phone (WhatsApp, USB, etc.)
2. Install the APK
3. Grant all permissions (Camera, Overlay, Accessibility)
4. Start using hand gestures!

---

## ✨ Features:

- 👆 Point to tap
- ↔️ Swipe gestures  
- 🤏 Pinch zoom
- ✌️ Two finger activation
- 📷 Front camera only
- 🔋 Battery optimized

---

**Need help? Check the main README.md**
