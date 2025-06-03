# QR Scanner & Generator

[![Kotlin](https://img.shields.io/badge/Kotlin-Compose-purple)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Android-blue)](https://developer.android.com/jetpack/compose)
[![Latest Release](https://img.shields.io/github/v/release/thezayin/QR-Scanner-BarCode-Scanner)](https://github.com/thezayin/QR-Scanner-BarCode-Scanner/releases)
[![Google Play](https://img.shields.io/badge/Download%20on-Google%20Play-brightgreen.svg?logo=google-play&style=flat)](https://play.google.com/store/apps/details?id=com.thezayin.qrscanner&hl=en-US&ah=gyduvRlqgGn7O9dEahV7B53W9_Y)

<img src="https://github.com/thezayin/QR-Scanner-BarCode-Scanner/blob/master/common/values/src/main/res/drawable/ic_main.png" alt="App Icon" width="100"/>

A modern, fast, and feature-rich Android application for scanning and generating QR codes and various barcodes, built natively with Kotlin and Jetpack Compose. Designed with a clean architecture and a focus on user experience.

---

## ✨ Features

This application delivers a comprehensive set of functionalities for managing QR codes and barcodes:

*   **Fast & Accurate Scanning:**
    *   **Live Camera Scan:** Real-time scanning using **CameraX** and **Google ML Kit Barcode Scanning API**.
    *   **Batch Scanning:** Continuously scan multiple unique codes from the camera.
    *   **Gallery Scan:** Import and scan codes from images stored on your device.
    *   **Flashlight & Zoom Control:** Enhance scanning capabilities in various conditions.
*   **Intelligent Scan Results:**
    *   **Barcode Lookup:** Automatically fetches product details (name, image, brands) from **Open Food Facts API** for scanned barcodes (using **Ktor Client** for networking).
    *   **Dynamic Actions:** Perform type-specific actions like open URL, connect Wi-Fi, initiate calls/SMS/emails, copy to clipboard, or share content.
    *   **Favorite Management:** Mark frequently accessed scan results as favorites for quick retrieval.
*   **Comprehensive Code Generation:**
    *   Generate a wide variety of QR code and barcode types (e.g., URL, Contact, Wi-Fi, Call, SMS, Text, Calendar, Code 39, Code 128, Data Matrix, Aztec) using the **ZXing Library**.
    *   Intuitive input forms with real-time validation.
    *   Save generated codes to your device's gallery or share them instantly.
*   **History Management:**
    *   Access a complete history of all scanned and generated codes, categorized for easy browsing.
    *   Manage entries by marking as favorite or deleting.
*   **User Customization & Experience:**
    *   **Theming:** Customize the app's primary accent color and toggle between light/dark themes.
    *   **Language Support:** Select from an extensive list of languages. The app leverages **Android Play Feature Delivery (SplitInstallManager)** for dynamic language module downloads, providing a localized experience.
    *   **Sensory Feedback:** Haptic feedback and sound for successful scans enhance user experience.
*   **Monetization & Analytics:**
    *   **Google AdMob Integration:** Strategically places App Open, Interstitial, Native, and Banner ads to ensure effective monetization.
    *   **Google Play Billing:** Implements a premium subscription model (via **Funsol Billing Helper**) to offer an ad-free experience and unlimited features.
    *   **Google User Messaging Platform (UMP):** Ensures user consent for personalized advertising for privacy compliance.
    *   **Firebase Integration:** Utilizes **Firebase Remote Config** for dynamic app configuration, **Firebase Analytics** for user insights, **Firebase Crashlytics** for robust crash reporting, and **Firebase Performance Monitoring** for app health.

---

## 📱 Download the App

Experience the QR Scanner & Generator app yourself!

[![Google Play](https://play.google.com/badge/image/en_GA.png)](https://play.google.com/store/apps/details?id=com.thezayin.qrscanner&hl=en-US&ah=gyduvRlqgGn7O9dEahV7B53W9_Y)
**[Download on Google Play Store](https://play.google.com/store/apps/details?id=com.thezayin.qrscanner&hl=en-US&ah=gyduvRlqgGn7O9dEahV7B53W9_Y)**

---

## 🛠️ Technologies & Libraries

This application is built with a modern Android tech stack, ensuring high performance, scalability, and maintainability:

*   **Platform:** Android Native
*   **Language:** **Kotlin**
*   **UI Framework:** **Jetpack Compose** (Declarative UI)
*   **Architecture:**
    *   **MVVM** (Model-View-ViewModel) with **MVI-like unidirectional data flow**
    *   **Clean Architecture** (Domain, Data, Presentation Layers)
    *   **Kotlin Coroutines** & **Flows** (Asynchronous programming, reactive data streams)
*   **Dependency Injection:** **Koin**
*   **Persistence:**
    *   **Room Database** (for structured data like history, favorites)
    *   **SharedPreferences** (managed by `PreferencesManager` for app settings)
*   **Camera & ML:**
    *   **CameraX** (Camera integration)
    *   **Google ML Kit Barcode Scanning API** (On-device QR/barcode detection)
*   **Networking & Serialization:**
    *   **Ktor Client** (Multiplatform HTTP client for API requests)
    *   **Kotlinx Serialization** (Efficient JSON parsing and serialization)
    *   **Open Food Facts API** (External product data)
*   **QR/Barcode Generation:** **ZXing Library** (Encoding QR and Barcodes)
*   **Firebase Ecosystem:**
    *   **Firebase Remote Config** (Dynamic app configurations)
    *   **Firebase Analytics** (User behavior tracking)
    *   **Firebase Crashlytics** (Real-time crash reporting)
    *   **Firebase Performance Monitoring** (App performance metrics)
    *   **Firebase Messaging & In-App Messaging** (Notifications, targeted messages)
*   **Monetization & Compliance:**
    *   **Google Mobile Ads SDK (AdMob)** (For various ad formats)
    *   **Google User Messaging Platform (UMP)** (Consent management)
    *   **Google Play Billing Library** (In-app purchases)
    *   **Funsol Billing Helper** (Simplified Play Billing integration)
*   **Android System Libraries:**
    *   **AndroidX Libraries** (Core KTX, Lifecycle, Activity Compose, Navigation Compose)
    *   **Android Play Core Library (`feature-delivery`)** (For dynamic feature modules like language packs)
*   **Image Loading:** **Coil Compose** (Asynchronous image loading)
*   **Animations:** **Lottie Compose** (Animated UI elements)
*   **Dimension Scaler:** **SDP Compose** (Scalable dimensions)
*   **Logging:** **Timber** (Enhanced logging framework)
*   **Build System:** **Gradle** (Kotlin DSL)

---

## 📐 Architecture

The application adopts a **Clean Architecture** with an emphasis on modularity, testability, and separation of concerns.

### Layered Architecture

The codebase is logically divided into three primary layers:

1.  **Domain Layer:**
    *   **Core:** Contains the pure business logic, application rules, and core data models. It is framework-agnostic.
    *   **Components:** Data models (e.g., `ScanItem`, `QrContent`), Repository Interfaces (`QrRepository`, `ProductRepository`), and Use Cases (business logic orchestrators like `ScanQrUseCase`, `GenerateQrUseCase`).

2.  **Data Layer:**
    *   **Core:** Responsible for retrieving and storing data. It implements the repository interfaces defined in the Domain layer.
    *   **Components:** Repository Implementations (`QrRepositoryImpl`, `ProductRepositoryImpl`), Data Sources (Local - **Room Database** DAOs, `PreferencesManager`; Remote - **Ktor `ApiService`**, **Google ML Kit BarcodeScanner**), and Mapper functions (converting data between entities and domain models).

3.  **Presentation Layer:**
    *   **Core:** Handles the user interface and user interaction logic.
    *   **Components:** **View Models** (`ScannerViewModel`, `GenerateViewModel` etc.) manage UI state and handle user events. **Composables** (Jetpack Compose UI components) render the UI based on state and dispatch events.

### Multi-Module Structure

The project is structured into multiple Gradle modules, each responsible for a specific area of the application:

*   **`:app`**: The main application module, acting as the entry point and coordinating integration of other features. Contains `MainActivity` and `RootNavGraph`.
*   **`:scanner`**: Handles all aspects of QR/barcode scanning, ML Kit integration, and remote product lookups.
*   **`:generate`**: Manages the generation of various QR codes and barcodes, including input forms and image output.
*   **`:history`**: Manages the storage and display of all scanned and generated items.
*   **`:start-up`**: Responsible for the app's initial launch (splash, onboarding) and global settings.
*   **`:databases`**: Encapsulates the Room database setup (entities, DAOs, `AppDatabase`).
*   **`:common:framework`**: Provides reusable, cross-cutting concerns such as AdMob integrations, preference management, remote configuration, and core utilities.
*   **`:common:values`**: A dedicated module for shared resources like strings, drawables, and fonts.

This modularity enhances:
*   **Separation of Concerns:** Each module has a single responsibility.
*   **Build Speed:** Changes in one module affect only dependent modules.
*   **Testability:** Enables more isolated and focused testing.
*   **Reusability:** Common logic and resources can be easily shared across features.

---

## 🚀 Getting Started

To get a local copy of the project up and running on your Android device or emulator:

### Prerequisites

*   **Android Studio** (latest stable version recommended)
*   **Java Development Kit (JDK) 11** or higher
*   An **Android device or emulator** running API 24 (Android 7.0 Nougat) or higher.

### Installation & Build

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/thezayin/QR-Scanner-BarCode-Scanner.git
    cd QR-Scanner-BarCode-Scanner
    ```

2.  **Open in Android Studio:**
    *   Launch Android Studio and select `File > Open`.
    *   Navigate to the cloned `QR-Scanner-BarCode-Scanner` directory and click `Open`.

3.  **Sync Gradle Project:**
    *   Android Studio will automatically try to sync the Gradle project. If it doesn't, click the `Sync Project with Gradle Files` button (🐘 icon) in the toolbar.

4.  **Google Services Setup:**
    *   For Firebase and AdMob to function correctly, you'll need a `google-services.json` file.
    *   Follow the official Firebase documentation to add an Android app to your Firebase project. Download the `google-services.json` file and place it in the `app/` directory of the project.
    *   _If you don't have a Firebase project or wish to run without Firebase/AdMob features enabled for development, you may temporarily comment out related dependencies and plugins in the `build.gradle.kts` files (e.g., Firebase, AdMob, Play Billing, UMP) for all modules. Note: This is purely for local testing and not recommended for a full build._

5.  **Run the application:**
    *   Select the `app` module in the Gradle pane.
    *   Choose your desired device/emulator from the run configurations dropdown.
    *   Click the `Run 'app'` button (▶️) to build and install the application.

---

## 🤝 Contributing

Contributions are welcome! If you find a bug, have a feature request, or would like to contribute code, please feel free to:

1.  Fork the repository.
2.  Create a new branch (`git checkout -b feature/your-feature`).
3.  Make your changes and commit them (`git commit -m 'feat: Add your feature'`).
4.  Push to the branch (`git push origin feature/your-feature`).
5.  Open a Pull Request.

---

## 📧 Contact

For any inquiries or feedback, please reach out to:

**Zain Shahid**  
[zainshahidbuttt@gmail.com](mailto:zainshahidbuttt@gmail.com)

---
