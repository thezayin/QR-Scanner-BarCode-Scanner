# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontwarn com.thezayin.databases.di.DatabaseModuleKt
-dontwarn com.thezayin.framework.di.FrameworkModuleKt
-dontwarn com.thezayin.framework.preferences.PreferencesManager
-dontwarn com.thezayin.generate.presentation.GenerateScreenKt
-dontwarn com.thezayin.generate.presentation.GenerateViewModel
-dontwarn com.thezayin.generate.presentation.di.GenerateModuleKt
-dontwarn com.thezayin.history.presentation.HistoryScreenKt
-dontwarn com.thezayin.history.presentation.HistoryViewModel
-dontwarn com.thezayin.history.presentation.di.HistoryModuleKt
-dontwarn com.thezayin.scanner.presentation.di.ScannerModuleKt
-dontwarn com.thezayin.scanner.presentation.result.ResultScreenKt
-dontwarn com.thezayin.scanner.presentation.result.ResultScreenViewModel
-dontwarn com.thezayin.scanner.presentation.scanner.ScannerScreenKt
-dontwarn com.thezayin.scanner.presentation.scanner.ScannerViewModel
-dontwarn com.thezayin.start_up.di.SettingsModuleKt
-dontwarn com.thezayin.start_up.languages.LanguageScreenKt
-dontwarn com.thezayin.start_up.languages.LanguageViewModel
-dontwarn com.thezayin.start_up.setting.SettingsScreenKt