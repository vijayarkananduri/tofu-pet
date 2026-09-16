-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep all public members
-keepclasseswithmembernames class * {
    public <methods>;
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enum members
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep WebView
-keep class android.webkit.** { *; }
-keep interface android.webkit.** { *; }

# Keep Tofu classes
-keep class com.tofu.pet.** { *; }
-keep interface com.tofu.pet.** { *; }

# Keep JavascriptInterface
-keepclassmembers class com.tofu.pet.overlay.PetOverlayService$AndroidBridge {
    <methods>;
}

# Keep Jetpack Compose
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }

# Keep DataStore
-keep class androidx.datastore.** { *; }
-keep interface androidx.datastore.** { *; }

# Keep WorkManager
-keep class androidx.work.** { *; }
-keep interface androidx.work.** { *; }

# Keep Android components
-keep class android.app.** { *; }
-keep class android.content.** { *; }
-keep class android.os.** { *; }
