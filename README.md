# פותר פאזלים (Poter Pazalim) - GitHub-ready

הקבצים בפרויקט מוכנים לייבוא ל-GitHub ולבנייה באמצעות GitHub Actions.
לאחר העלאה ל-GitHub, עבור ל-**Actions** -> בחר את workflow "Build APK" -> לחץ **Run workflow**.
בסיום הבנייה תוכל להוריד את APK מתוך ה-Artifacts של הריצה.

הערות:
- זה בונה גרסת Debug של ה-APK (app-debug.apk). אם תרצה גרסת Release חתומה, אמליץ על יצירת keystore והוספתו כ-Secret ל-GitHub ועריכת ה-workflow לחתימה.
- ודא שיש לך חיבור אינטרנט כדי ש-Gradle יוכל להוריד תלויות.
