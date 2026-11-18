# Password Manager

A native Android password manager built with Java and XML layouts for **Mobile Application Development Assignment 1 (Individual)**. The app stores account credentials locally, provides a four-digit PIN login, and lets users add, search, view, edit, and delete password entries.

## Features

- **PIN login:** Create and confirm a four-digit PIN on first launch, then use it to unlock the app. Five incorrect attempts trigger a 30-second lockout.
- **Credential management:** Save a site or app name, username or email, and password. View and edit existing entries.
- **Optional account details:** Store an additional PIN, challenge question, and challenge answer through the **Extra** section.
- **Search and sorting:** Filter entries by site name or username as you type, and toggle ascending or descending order by site name.
- **Password masking:** Passwords are hidden in the list. Press and hold the eye icon to reveal a password while adding, editing, or viewing an entry. The detail screen also supports revealing the extra PIN and challenge answer.
- **Deletion confirmation:** Two confirmation dialogs appear before an entry is permanently deleted.
- **Local storage:** Entries are saved in SQLite. No backend, account registration, API keys, or internet permission are required by the app.
- **Screen protection:** All four activities use Android's `FLAG_SECURE` to restrict screenshots and display on non-secure screens.

## Technology

| Component | Implementation |
| --- | --- |
| Application code | Java, with Java 11 source and target compatibility |
| User interface | XML layouts, Material Components, AppCompat, ConstraintLayout, and ListView |
| Database | SQLite through `SQLiteOpenHelper` |
| Login settings | SharedPreferences |
| Credential encryption | AES-256-GCM with an Android Keystore key |
| Build scripts | Gradle Kotlin DSL |
| Android Gradle Plugin | 9.0.1 |
| Gradle wrapper | 9.1.0 |
| Minimum Android version | Android 7.0 / API 24 |
| Compile / target SDK | API 36 |
| Application ID | `my.edu.utar.passwordmanager` |

The `.kts` files configure the build; the application's source code is Java.

## Getting started

### Requirements

- Android Studio with support for Android Gradle Plugin 9.0.1.
- JDK 17 to run Gradle. This is separate from the app's Java 11 source compatibility; see the [AGP 9.0 compatibility requirements](https://developer.android.com/build/releases/agp-9-0-0-release-notes).
- Android SDK Platform 36 and the required SDK Build Tools, installed through Android Studio's SDK Manager.
- An Android emulator or physical device running API 24 or later.
- Internet access for the initial Gradle and dependency downloads.

### Open and run

1. Clone the repository:

   ```sh
   git clone https://github.com/CasperNg-2004/PasswordManager.git
   cd PasswordManager
   ```

2. Open the `PasswordManager` project folder in Android Studio.
3. Let Gradle sync finish and install any requested SDK components. If needed, select JDK 17 in the project's Gradle settings.
4. Start an emulator or connect an Android device with USB debugging enabled.
5. Select the `app` run configuration and click **Run**.

For command-line builds, configure the local Android SDK path using `local.properties` (`sdk.dir`) or `ANDROID_HOME`, and ensure `JAVA_HOME` points to JDK 17. Keep `local.properties` out of version control.

### Build a debug APK

Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

macOS / Linux:

```sh
chmod +x gradlew
./gradlew assembleDebug
```

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

## Using the app

1. **Set up your PIN:** On first launch, enter a four-digit PIN twice and tap **Save PIN**. On subsequent launches of the login screen, enter the PIN and tap **Unlock**.
2. **Add an entry:** Tap the **+** button on **My Passwords**. Fill in the site/app name, username/email, and password. These three fields are required.
3. **Add optional details:** Tap **Extra** to enter an additional PIN, challenge question, or challenge answer, then tap **Save**.
4. **Find an entry:** Search by site or username, or use the sort button to reverse the site-name order.
5. **View or edit:** Tap an entry to open its details. Hold the eye icon to reveal a hidden value, or tap **Edit** to update the entry.
6. **Delete:** Tap the entry's delete icon and complete both confirmation dialogs.

The extra PIN and challenge question are saved account details; they do not add another authentication step. Text fields, including passwords, have leading and trailing whitespace removed when saved.

## Project structure

```text
PasswordManager/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/my/edu/utar/passwordmanager/
│       │   │   ├── LoginActivity.java        # PIN setup, login, and lockout
│       │   │   ├── MainActivity.java         # List, search, sorting, and deletion
│       │   │   ├── ViewPasswordActivity.java # Entry details and reveal controls
│       │   │   ├── EditPasswordActivity.java # Create and update entries
│       │   │   ├── PasswordListAdapter.java  # Password list rows
│       │   │   ├── PasswordEntry.java        # Credential data model
│       │   │   ├── PasswordDbHelper.java     # SQLite storage and queries
│       │   │   └── CryptoUtil.java           # Keystore and AES-GCM operations
│       │   └── res/                          # Layouts, themes, and other resources
│       ├── test/                             # Example JUnit test
│       └── androidTest/                      # Example instrumentation test
├── gradle/
│   ├── libs.versions.toml                    # Dependency and plugin versions
│   └── wrapper/                              # Gradle wrapper files
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
└── gradlew.bat
```

## Storage and security limitations

This is an educational project. The current implementation has limitations that should be addressed before using it for sensitive credentials:

- **Encryption can fall back to plaintext.** `CryptoUtil.encrypt()` returns the original value if encryption fails. Decryption failures also return the stored value rather than reporting an error.
- **Only selected fields are encrypted.** Passwords, extra PINs, and challenge answers pass through AES-GCM encryption before storage. Site names, usernames, and challenge questions remain plaintext in `password.db`.
- **The login PIN is stored unhashed** in the private `pm_prefs` SharedPreferences file. It is not used to derive the encryption key.
- **There is no automatic session lock on backgrounding**, biometric login, or in-app PIN change/recovery flow.
- **Database upgrades erase entries.** The current database version is 2, and `onUpgrade()` drops and recreates the passwords table.
- **Backup and recovery are incomplete.** The manifest enables Android backup, while the backup rule files contain no custom exclusions. There is no app-managed export/import flow or recovery mechanism for the device-local Keystore key, so restoring database files alone should not be treated as a reliable way to recover encrypted credentials.

## Tests

Run the included local unit test on Windows:

```powershell
.\gradlew.bat testDebugUnitTest
```

With an emulator or device connected, run the instrumentation test:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

On macOS/Linux, use `./gradlew` in place of `.\gradlew.bat`.

The repository currently contains template tests for basic arithmetic and the application package name. These do not validate credential management, PIN lockout, or encryption behavior.

## Author

[CasperNg-2004](https://github.com/CasperNg-2004)

## License

The repository does not currently include a license file.
