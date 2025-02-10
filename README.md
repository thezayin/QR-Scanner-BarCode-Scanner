
```markdown
# QRScanner App

![QRScanner Icon](https://github.com/thezayin/QR-Scanner-BarCode-Scanner/blob/master/common/values/src/main/res/drawable/ic_main.jpeg)

QRScanner is a modular Android application built with Jetpack Compose, Clean Architecture, and the MVI pattern. It allows users to scan and generate QR/barcodes, manage their history, and customize app settings—all with smooth animated transitions and modern dependency injection via Koin.

## Features

- **Scan & Generate:**  
  Use ML Kit for scanning and ZXing for generating QR/barcodes.

- **History & Favorites:**  
  View, mark, and delete scanned/generated codes.

- **Dynamic Themes & Settings:**  
  Change primary color, toggle dark mode, and adjust sound/vibration options.

- **Onboarding & Localization:**  
  Guided onboarding and language selection.

- **Animated Navigation:**  
  Seamless transitions with Accompanist Navigation Animation.

## Architecture & Modules

**Architecture:**  
The app follows Clean Architecture with a clear separation into:
- **Presentation:** Jetpack Compose UI with MVI.
- **Domain:** Business logic and use cases.
- **Data:** Local persistence (Room) and network communication (Ktor).

**Modules:**
- **app:** Main activity, navigation host, bottom navigation, theme, and Application class.
- **common:**  
  - **framework:** Reusable components (ads, utilities, remote config, session manager).  
  - **values:** Assets, strings, drawables, and styles.
- **databases:** Room configuration (DAOs, entities, migrations).
- **generate:** QR/barcode generation using ZXing.
- **history:** Manages scan/generation history with MVI (state & events).
- **scanner:** QR/barcode scanning using ML Kit.
- **start-up:** Onboarding, language selection, settings, and splash screens.

## Technologies & Libraries

- **Jetpack Compose** for declarative UI.
- **Accompanist Navigation Animation** for animated screen transitions.
- **Koin** for dependency injection.
- **Ktor** for networking.
- **Room** for local data storage.
- **ML Kit** for barcode scanning.
- **ZXing (Core)** for QR/barcode generation.
- **Kotlin Coroutines & Flow** for asynchronous operations.
- **MVI Pattern** for state management.

## Setup & Installation

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/thezayin/QR-Scanner-BarCode-Scanner.git
   cd QR-Scanner-BarCode-Scanner
   ```
2. **Open the Project:**  
   Use Android Studio (Arctic Fox or newer) to open the project as a Gradle project.
3. **Download Dependencies:**  
   Gradle will automatically download all required libraries.
4. **Configure Environment:**  
   Set up any necessary API keys or configurations in `gradle.properties` or your local config files.

## Building & Running

- **Debug Build:**  
  Run the app directly from Android Studio.
- **Release Build:**  
  Configure signing in your app-level `build.gradle`, then use **Build > Generate Signed Bundle/APK…** to create a signed APK or AAB.

## Navigation Flow

- **Root Navigation:**  
  Manages the splash, onboarding, and language selection screens.
- **Main Navigation:**  
  Contains a bottom navigation bar with routes for Scan, Create, History, Settings, and Favorite screens. Animated transitions are provided by Accompanist.

## Developer

**Zain Shahid**  
Email: [zainshahidbuttt@gmail.com](mailto:zainshahidbuttt@gmail.com)

## Contributing

Contributions are welcome! To contribute:
1. Fork the repository.
2. Create a feature branch:  
   ```bash
   git checkout -b feature/YourFeatureName
   ```
3. Make your changes and commit.
4. Push and open a Pull Request.

## License

This project is licensed under the [MIT License](LICENSE).
