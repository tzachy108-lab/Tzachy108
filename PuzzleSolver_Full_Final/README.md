PuzzleSolver - Full Debug (Kotlin) - GitHub-ready

How to use:
1. Unzip and upload this folder to a new GitHub repository (or push via Git).
2. On GitHub go to Actions -> Build APK -> Run workflow (or push to trigger).
3. Wait for the workflow to finish and download the artifact 'PuzzleSolver-debug' containing app-debug.apk.

Alternatively, open the project in Android Studio:
1. File -> Open -> select this folder.
2. Let Android Studio sync Gradle (it will download Gradle wrapper and SDK components if needed).
3. Build -> Build Bundle(s) / APK(s) -> Build APK(s).
4. The APK will be at app/build/outputs/apk/debug/app-debug.apk

Notes:
- This project uses CameraX and OpenCV dependencies. If OpenCV native libs cause issues, import OpenCV Android SDK manually.
- For release builds, add signing configs and modify the workflow.
