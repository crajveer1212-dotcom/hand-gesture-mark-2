# Hand Gesture Control - Mark 2 👋

Control your Android phone with hand gestures using the front camera!

## ✨ Features

- 👆 **Point to tap** - Hold finger still for 1 second to tap/select
- ↔️ **Swipe gestures** - Swipe left/right to scroll
- 🤏 **Pinch zoom** - Thumb + index finger for zoom
- 🖐️ **Activation gesture** - Two fingers to enable/disable
- ⏸️ **Auto-pause** - Pauses when hand not visible
- 🔋 **Battery optimized** - 15 FPS, efficient processing
- 📱 **Works offline** - No internet needed
- 📷 **Front camera only** - Lower power consumption

## 📋 Requirements

- Android 7.0 (API 24) or higher
- Front-facing camera
- Minimum 2GB RAM

## 🚀 Installation

1. Download the latest APK from [Releases](https://github.com/crajveer1212-dotcom/hand-gesture-mark-2/releases)
2. Enable "Install from Unknown Sources" in Android settings
3. Install the APK
4. Grant Camera, Overlay, and Accessibility permissions
5. Start using hand gestures!

## 🛠️ Build from Source

```bash
# Clone repository
git clone https://github.com/crajveer1212-dotcom/hand-gesture-mark-2.git
cd hand-gesture-mark-2

# Download MediaPipe model
mkdir -p app/src/main/assets
curl -L "https://storage.googleapis.com/mediapipe-models/hand_landmarker/hand_landmarker/float16/latest/hand_landmarker.task" \
     -o app/src/main/assets/hand_landmarker.task

# Build with Android Studio or Gradle
./gradlew assembleDebug
```

## 📖 Technology Stack

- **Kotlin** - Primary language
- **CameraX** - Camera handling
- **MediaPipe** - Hand landmark detection
- **Android Accessibility Service** - Gesture execution

## 👨‍💻 Author

Created by Rajveer Chauhan

## 📄 License

MIT License

---

**Made with ❤️ for hands-free Android control**
